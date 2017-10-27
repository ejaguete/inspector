import static java.lang.System.out;
import java.lang.reflect.*;
import java.util.ArrayList;

public class Inspector {
	private String format =  "%-" + 12 + "s -> %s %n";
	private final String NONE = "none";
	
	public static ArrayList<String> foundSupers = new ArrayList<String>();
	
	private boolean rec;
	
	public Inspector() {}
	
	public void inspect(Object obj, boolean recursion) {
		this.rec = recursion;
		
		if(obj.getClass().isArray()) {
			inspectArray(null, obj);
		} else {
			inspectClass(obj, obj.getClass());
		}
	}
	
	private void divider() {
		out.println("***********************");
	}

	private void inspectClass(Object o, Class c) {
		out.println();
		out.println("<< Beginning inspection of class \"" + c.getName() + "\" >>");
		divider();
		printClass(c, false);
		
		if(c.getSuperclass()!=null) {
			printClass(c.getSuperclass(),true);
			if(!foundSupers.contains(c.getSuperclass().getName())) {
				
				foundSupers.add(c.getSuperclass().getName());
				out.println("<< Discovered new superclass \"" + c.getSuperclass().getName() + "\". Inspecting... >>");

				inspectClass(o, c.getSuperclass());
				
				out.println("<< Inspection of superclass \"" + c.getSuperclass().getName() + "\" complete >>");
				out.println("<< Resuming inspection of " + c.getName() + " >>");
				out.println();
			} else {
				out.println("<< Superclass \"" + c.getSuperclass().getName() + "\" has already been inspected. >>" );
				out.println();
			}
		}	
		else {
			out.printf(format, "SUPERCLASS", NONE);
			out.println();
		}

		Class[] inters = c.getInterfaces();
		if(inters.length==0) {
			for(Class i : inters) {
				
			}
		} else {
			out.printf(format, "INTERFACE", NONE);
			out.println();
		}
		
		Field[] fields = c.getDeclaredFields();
		if(fields.length!=0) {
			for (Field f : fields) {
				f.setAccessible(true);
				printField(o, f);
			}
		} else {
			out.printf(format, "FIELD", NONE);
			out.println();
		}
		
		Constructor[] constructors = c.getDeclaredConstructors();
		for(Constructor con : constructors) {
			printConstructor(con);
		}
		
		Method[] methods = c.getDeclaredMethods();
		for (Method m : methods) {
			printMethod(m);
		}
		
		divider();
		out.println("<< Completed inspection of class \"" + c.getName() + "\" >>");
		out.println();
	}
	
	private void printClass(Class c, boolean supercl) {
		if(supercl)
			out.printf(format, "SUPERCLASS", c.getName());
		else
			out.printf(format, "CLASS", c.getName());
		out.println();
	}
	
	private void printConstructor(Constructor con) {
		out.printf(format, "CONSTRUCTOR", con);
		out.println();	
	}
	
	private void inspectArray(Field f, Object array) {
		out.println("<< Inspecting array \"" + array.getClass().getName() + "\" >>");
		divider();
		if(f!=null) {
			out.printf(format, "ARRAY NAME", f.getName());
		} else {
			out.printf(format, "ARRAY NAME", array.getClass().getName());
		}
		int len = Array.getLength(array);
		out.printf(format, "ARRAY LENGTH", len);
		out.printf(format, "COMPONENT TYPE", array.getClass().getComponentType());
		out.println("ARRAY CONTENTS:");
		
		if(len!=0) {
			String contents = "";
			for(int i=0; i<len;++i) {
				Object item = Array.get(array, i);
				
				if(item==null) {
					contents += "[null] ";
				} else {
					contents += "[" + item + "] ";
				}
			}
			out.println(contents);
			
			// go through contents again to recurse into objects
			for(int i=0; i<len;++i) {
				Object item = Array.get(array, i);

				if(item==null) {
					// do nothing
				} else if(item.getClass().isArray()) {
					inspectArray(null,item);
				} else if(!array.getClass().getComponentType().isPrimitive() && rec) {
					inspect(item,rec);
				}
			}
		} else {
			out.println("Array is empty.");
		}
		divider();
		out.println("<< Inspection of array \"" + array.getClass().getName() + "\" complete >>");
		out.println();
	}
	
	private void printFieldInfo(String field, String value) {
		out.printf(format,  "FIELD", field);
		out.printf(format, "value", value);
	}
	private void printField(Object o, Field f) {
		Object val;
		String field = f + "";
		String value = "";
		try {
			val = f.get(o);

			if(val==null) {
				value += "null";
			}
			else if(f.getType().isPrimitive()) {
				value += val;
				printFieldInfo(field, value);
				out.println();
			} else if(f.getType().isArray()){
				value += val.getClass() + " (hash:" + val.hashCode() + ")";
				printFieldInfo(field, value);
				inspectArray(f, val);
				out.println();
			} else {
				value += val.getClass() + " (hash:" + val.hashCode() + ")";
				printFieldInfo(field, value);
				out.println();
				if(rec) {
					out.println("<< Discovered object \"" + f.getName() + "\", recursing... >>");
					inspect(val,rec);
					out.println("<< Recursion into \"" + f.getName() + "\" complete >>");
					out.println();
				}			
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
	}
	
	private void printMethod(Method m) {
		out.printf(format, "METHOD", m);
		out.println();
		
	}

	
	private String determineModifier(int m) {
		String mod = "";
		if(Modifier.isAbstract(m))
			mod = "abstract";
		else if(Modifier.isFinal(m))
			mod = "final";
		else if(Modifier.isInterface(m))
			mod = "interface";
		return mod;
	}
}
