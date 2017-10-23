
import static java.lang.System.out;
import java.lang.reflect.*;

public class Inspector {
	private String format = "%-20s %-30s %n";
	
	public Inspector() {}
	
	private void printList(Object[] list, String header) {
		if(list.length!=0) {
			for (int i=0;i<list.length;++i)
				out.printf(format, header, list[i]);
		} else
			out.printf(format, header, "none");
		out.println();	
	}
	
	private void inspectHierarchy(Class c, boolean recurse) {
		
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
		 * - traverse inheritance hierarchy to find all methods, constructors, fields and field values that each superclass/superinterface declares
		 *  	- must handle arrays you might encounter
		 *  	- print out name, component type, length, contents
		 *  	
		 */
		
		out.println("--------\nSUMMARY:\n--------\n");
		
		//find name of declaring class
		Class c = o.getClass();
		out.printf(format, "CLASS", c.getName() + "\n");
		
		//find superclass
		Class sup = c.getSuperclass();
		out.printf(format, "SUPERCLASS", sup.getName() + "\n");
		
		if(!sup.getName().equals("java.lang.Object") && 
				!Modifier.isAbstract(sup.getModifiers())) {
			
			Constructor superCons;
			try {
				superCons = sup.getConstructor(null);
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
			// manually print out info
			out.println("--------\nSUPERCLASS SUMMARY:\n--------\n");
			
			Class[] supInterfaces = sup.getInterfaces();
			printList(supInterfaces, "INTERFACE");
			
			//find methods
			Method[] methods = sup.getDeclaredMethods();
			printList(methods, "METHOD");
			
			
			//find constructor
			Constructor[] constructors = sup.getConstructors();
			printList(constructors, "CONSTRUCTOR");
			
			//find fields

			Field[] fields = sup.getDeclaredFields();
			if(fields.length!=0) {
				for (int i=0; i<fields.length;++i) {
					//get field name
					String info = fields[i] + " = ";
					// read field
					fields[i].setAccessible(true);

					try {
						Object value = fields[i].get(o);
						
						if(!fields[i].getType().isPrimitive()) {
							info += value + " / hashcode=" + System.identityHashCode(value);
						}
						else if(fields[i].getType().isArray()) {
							
						}
						else if(value!=null)
							info += value;
						else
							info += "null";
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					out.printf(format, "FIELD", info);
				} 
			} else
				out.printf(format, "FIELD", "none");
			
			out.println("--------\nEND SUPERCLASS SUMMARY\n--------\n");
		}
		
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
				
				if(!fields[i].getType().isPrimitive()) {
					info += value + " / hashcode=" + System.identityHashCode(value);
				}
				else if(fields[i].getType().isArray()) {
					
				}
				else if(value!=null)
					info += value;
				else
					info += "null";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			out.printf(format, "FIELD", info);
		}
		
		/* 
		 * TODO:
		 * recursive inspection :
		 * if recurse = false, find info for object specified
		 * if recurse = true, fully inspect each field that is an object (recurse into object)
		 * 
		 */	
	}

}
