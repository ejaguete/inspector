
import static java.lang.System.out;
import java.lang.reflect.*;

public class Inspector {
	private String format = "%-20s %-20s %n";
	
	public Inspector() {}
	
	private void printList(Object[] list, String header) {
		if(list.length!=0) {
			for (int i=0;i<list.length;++i)
				out.printf(format, header, list[i]);
			out.println();
		} else
			out.printf(format, header, "none");
		
	}

	public void inspect(Object o, boolean recurse) {
		/*
		 * TODO:
		 * find the following info about the object:
		 * - name of declaring class
		 * - name of immediate superclass
		 * - name of interfaces class implements
		 * - methods class declares
		 * 		- include:
		 * 		- exceptions thrown
		 * 		- parameter types
		 * 		- return type
		 * 		- modifiers
		 * - constructors class declares
		 * 		- include:
		 * 		- parameter types
		 * 		- modifiers
		 * - fields class declares
		 *  	- include:
		 *  	- type
		 *  	- modifiers
		 *  	- current value of each field
		 *  		* if field is an object reference and recursive is set to false, print "reference value" directly
		 *  		(ref value = name of object's class, object's identity hash code)
		 * - traverse inheritance hierarchy to find all methods, constructors, fields and field balues that each superclass/superinterface declares
		 *  	- must handle arrays you might encounter
		 *  	- print out name, component type, length, contents
		 *  	
		 */
		
		out.println("\nSUMMARY: \n");
		
		//find name of declaring class
		Class c = o.getClass();
		out.printf(format, "DECLARING CLASS", c.getName() + "\n");
		
		//find superclass
		out.printf(format, "SUPERCLASS", c.getSuperclass().getName() + "\n");
		
		//find interfaces
		Class[] interfaces = c.getInterfaces();
		printList(interfaces, "INTERFACE");
		
		//find methods
		Method[] methods = c.getDeclaredMethods();
		printList(methods, "METHOD");
		
		
		//find constructor
		Constructor[] constructors = c.getConstructors();
		
		/* 
		 * TODO:
		 * recursive inspection :
		 * if recurse = false, find info for object specified
		 * if recurse = true, fully inspect each field that is an object (recurse into object)
		 * 
		 */	
	}

}
