
import static java.lang.System.out;
import java.lang.reflect.*;

public class Inspector {
	private String format = "%-20s %-30s %n";
	Object o;
	boolean rec;
	
	public Inspector() {}
	
	private void printList(Object[] list, String header) {
		if(list.length!=0) {
			for (Object item : list)
				out.printf(format, header, item);
		} else
			out.printf(format, header, "none");
		out.println();	
	}
	
	private void printInfo(Class c) {
		out.printf(format, "CLASS", c.getName() + "\n");
		
		//find superclass
		Class sup = c.getSuperclass();
		out.printf(format, "SUPERCLASS", sup.getName() + "\n");
		inspectHierarchy(sup, rec, o);
		
		//find interfaces
		Class[] interfaces = c.getInterfaces();
		printList(interfaces, "INTERFACE");
		
		//find methods
		Method[] methods = c.getDeclaredMethods();
		printList(methods, "METHOD");
		
		
		//find constructor
		Constructor[] constructors = c.getConstructors();
		printList(constructors, "CONSTRUCTOR");
		
		//find fields

		Field[] fields = c.getDeclaredFields();		
		for (int i=0; i<fields.length;++i) {
			//get field name
			String info = fields[i] + " = ";
			// read field
			fields[i].setAccessible(true);

			try {
				Object value = fields[i].get(o);
				
				if(!fields[i].getType().isPrimitive()) 
					info += value + " / hashcode=" + System.identityHashCode(value);
				
				else if(value!=null)
					info += value;
				
				else
					info += "null";
				out.printf(format, "FIELD", info);
				
				if(fields[i].getType().isArray()) {
					out.println("--------\nARRAY SUMMARY:\n--------\n");
					
					int len = Array.getLength(value);
					out.printf(format, "ARRAY TYPE", fields[i].getType().getComponentType());
					if(!fields[i].getType().getComponentType().toString().contains("java.io")) {
						String contents = "";
						for(int j=0; j<len-1;++j) {
							contents += Array.get(value, j) + ", ";
						}
						contents += Array.get(value, len-1);
						out.printf(format, "ARRAY CONTENTS", contents);
						out.println("--------\nEND ARRAY SUMMARY\n--------\n");
					} else {
						out.printf(format, "ARRAY CONTENTS", "Cannot display contents of array.");
					}
				} 
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void inspectHierarchy(Class c, boolean recurse, Object o) {
		if(!c.getName().equals("java.lang.Object") && 
				!Modifier.isAbstract(c.getModifiers())) {		
		
			try {
				Constructor superCons = c.getConstructor(null);
				try {
					Object superObj = superCons.newInstance(null);
					inspect(superObj, recurse);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e1) {
					e1.printStackTrace();
				}
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}	
		} else {
			out.println("--------\nSUPERCLASS SUMMARY:\n--------\n");
			printInfo(c);
			out.println("--------\nEND SUPERCLASS SUMMARY\n--------\n");
		}
		
	}

	public void inspect(Object object, boolean recurse) {
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
		 * - traverse inheritance hierarchy to find all methods, constructors, fields and field values that each superclass/superinterface declares
		 *  	- must handle arrays you might encounter
		 *  	- print out name, component type, length, contents
		 *  	
		 */
		
		this.o = object;
		this.rec = recurse;
		out.println("--------\nSUMMARY:\n--------\n");
		printInfo(o.getClass());
		
	}
}

