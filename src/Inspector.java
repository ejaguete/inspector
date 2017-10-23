
import static java.lang.System.out;
import java.lang.reflect.*;

public class Inspector {
	
	public Inspector() {}

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
		String format = "%-20s %-20s %n";
		
		//find name of declaring class
		Class c = o.getClass();
		out.printf(format, "DECLARING CLASS", c.getName() + "\n");
		
		//find superclass
		out.printf(format, "SUPERCLASS", c.getSuperclass().getName() + "\n");
		
		//find interfaces
		Class[] interfaces = c.getInterfaces();
		if(interfaces.length!=0) {
			for (int i=0;i<interfaces.length;++i)
				out.printf(format, "INTERFACE", interfaces[i]);
			out.println();
		} else 
			out.printf(format, "INTERFACE", "none\n");
		
		//find methods
		Method[] methods = c.getDeclaredMethods();
		if(methods.length!=0) {
			//method name
			for(int i=0;i<methods.length;++i) {
				out.printf(format, "METHOD", methods[i]);
			}
		} else
			out.printf(format, "METHOD", "none");
		
		/* 
		 * TODO:
		 * recursive inspection :
		 * if recurse = false, find info for object specified
		 * if recurse = true, fully inspect each field that is an object (recurse into object)
		 * 
		 */	
	}

}
