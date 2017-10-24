
import static java.lang.System.out;
import java.lang.reflect.*;

public class Inspector {
	private String format = "%-20s %-30s %n";
	Object o;
	boolean rec;
	
	public Inspector() {}
	
	private void printList(Object[] list, String header) {
		if(list.length==0)
			out.printf(format, header, "none");
		
		for (Object item : list)
			out.printf(format, header, item);		
		out.println();	
	}
	
	private void printFields(Field[] fields) {
		if(fields.length==0) 
			out.printf(format, "FIELD", "none");
	
		for (Field f : fields) {
			String info = f + " = ";
			f.setAccessible(true);

			try {
				Object value = f.get(o);

				if(!f.getType().isPrimitive()) 
					info += value + " / hashcode=" + System.identityHashCode(value);
				else if(value!=null)
					info += value;
				else
					info += "null";
				
				out.printf(format, "FIELD", info);
				out.println();
				
				if(f.getType().isArray()) {
					out.println("--------\nARRAY SUMMARY:\n--------\n");
					printArray(f, value);
					out.println();
					out.println("--------\nEND ARRAY SUMMARY: " + f.getName() + "\n--------\n");
				} 
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String returnArray(Object obj) {

		int len = Array.getLength(obj);
		String contents = "";
		if(len!=0) {	
			for(int j=0; j<len;++j) {
				Object item = Array.get(obj, j);
				if(item!=null) {
					if(item.getClass().isArray()) {
						contents += returnArray(item) + "\n";
					} else
						contents += item + "/ ";
				} else {
					contents += "null/ ";
				}
			}
		}
		return contents;
	}
	
	private void printArray(Field f, Object value) {
		
		out.printf(format, "ARRAY NAME", f.getName());
		int len = Array.getLength(value);
		out.printf(format, "ARRAY LENGTH", len);
		out.printf(format, "COMPONENT TYPE", value.getClass().getComponentType());
		if(len!=0) {
			out.println("\nARRAY CONTENTS");
			out.println(returnArray(value));
			out.println();
		} else {
			out.printf(format, "ARRAY CONTENTS", "Array is empty.");
			out.println();		
		}
	}
	
private void printArray(Object value) {
		
		out.printf(format, "ARRAY NAME", value.getClass().getName());
		int len = Array.getLength(value);
		out.printf(format, "ARRAY LENGTH", len);
		out.printf(format, "COMPONENT TYPE", value.getClass().getComponentType());
		if(len!=0) {
			out.println("\nARRAY CONTENTS");
			out.println(returnArray(value));
			out.println();
		} else {
			out.printf(format, "ARRAY CONTENTS", "Array is empty.");
			out.println();		
		}
	}
	
	private void printInfo(Class c) {
		
		out.printf(format, "CLASS", c.getName());
		out.println();
		
		Class sup = c.getSuperclass();
		if(sup==null)
			out.printf(format, "SUPERCLASS", "none");
		else {
			out.printf(format, "SUPERCLASS", sup.getName());
			out.println("\n--------\nSUPERCLASS SUMMARY:\n--------\n");
			printInfo(sup);
			out.println("\n--------\nEND SUPERCLASS SUMMARY: " + sup.getName() + "\n--------");
		}
		out.println();
		
		Class[] interfaces = c.getInterfaces();
		if(interfaces.length==0)
			out.printf(format,"INTERFACE", "none\n");
		else {
			for (Class face : interfaces) {
				out.printf(format, "INTERFACE", face);
				out.println("\n--------\nINTERFACE SUMMARY:\n--------\n");
				printInfo(face);
				out.println("\n--------\nEND INTERFACE SUMMARY: " + face.getName() + "\n--------\n");
			}
		}

		Method[] methods = c.getDeclaredMethods();
		printList(methods, "METHOD");

		Constructor[] constructors = c.getConstructors();
		printList(constructors, "CONSTRUCTOR");

		Field[] fields = c.getDeclaredFields();
		printFields(fields);
	}

	public void inspect(Object obj, boolean recurse) {
		this.o = obj;
		rec = recurse;
		out.println("--------\nSUMMARY:\n--------\n");
		Class c = obj.getClass();
		if(c.isArray()) {		
			printArray(o);
		} else
			printInfo(c);
		out.println("--------\nEND SUMMARY: " + o.getClass().getName() + "\n--------\n");
	}

}
