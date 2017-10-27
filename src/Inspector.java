import static java.lang.System.out;
import java.lang.reflect.*;
import java.util.ArrayList;

public class Inspector {
	private String format = "%-20s %-30s %n";
	private final String CLASS = "CLASS";
	private final String FIELD = "FIELD";
	private final String METHOD = "METHOD";
	private final String CON = "CONSTRUCTOR";
	private final String SUPER = "SUPERCLASS";
	private final String INTER = "INTERFACE";
	private final String NONE = "none";
	
	public static ArrayList<String> foundSupers = new ArrayList<String>();
	
	private boolean rec;
	
	public Inspector() {}
	
	public void inspect(Object obj, boolean recursion) {
		this.rec = recursion;
		
		out.println("***********************");
		
		Class c = obj.getClass();
		printClass(c, false);
	
		if(c.getSuperclass()!=null) {
			printClass(c.getSuperclass(),true);
			if(!foundSupers.contains(c.getSuperclass().getName())) {
				foundSupers.add(c.getSuperclass().getName());
				inspectSuper(obj, c.getSuperclass());
				out.println("<< Resuming inspection of " + obj.getClass().getName() + " >>");
				out.println();
			} else {
				out.println("<< Superclass \"" + c.getSuperclass().getName() + "\" has already been inspected. >>" );
				out.println();
			}
		}	
		else {
			out.printf(format, SUPER, NONE);
		}
		
		Class[] inters = c.getInterfaces();
		if(inters.length==0) {
			for(Class i : inters) {
				
			}
		} else {
			out.printf(format, INTER, NONE);
			out.println();
		}
		
		Field[] fields = c.getDeclaredFields();
		if(fields.length!=0) {
			for (Field f : fields) {
				f.setAccessible(true);
				printField(obj, f);
			}
		} else {
			out.printf(format, FIELD, NONE);
			out.println();
		}
		
		Constructor[] constructors = c.getDeclaredConstructors();
		for(Constructor con : constructors) {
			printConstructor(con);
		}

		out.println("***********************");
	}

	private void inspectSuper(Object o, Class c) {
		out.println("<< Discovered new superclass \"" + c.getName() +  "\". Inspecting... >>");
		out.println();
		out.println("***********************");
		
		printClass(c, false);

		if(c.getSuperclass()!=null) {
			if(!foundSupers.contains(c.getSuperclass().getName())) {
				foundSupers.add(c.getSuperclass().getName());
				inspectSuper(o, c.getSuperclass());
				out.println("<< Resuming inspection of " + c.getName() + " >>");
				out.println();
			} else {
				out.println("<< Superclass \"" + c.getSuperclass().getName() + "\" has already been inspected. >>" );
				out.println();
			}
		}	
		else {
			out.printf(format, SUPER, NONE);
			out.println();
		}
		
		Class[] inters = c.getInterfaces();
		if(inters.length==0) {
			for(Class i : inters) {
				
			}
		} else {
			out.printf(format, INTER, NONE);
			out.println();
		}
		
		Field[] fields = c.getDeclaredFields();
		if(fields.length!=0) {
			for (Field f : fields) {
				f.setAccessible(true);
				printField(o, f);
			}
		} else {
			out.printf(format, FIELD, NONE);
			out.println();
		}
		
		Constructor[] constructors = c.getDeclaredConstructors();
		for(Constructor con : constructors) {
			printConstructor(con);
		}
		
		out.println("***********************");
		out.println();
		out.println("<< Inspection of superclass \"" + c.getName() + "\" complete >>");
	}
	
	private void printClass(Class c, boolean supercl) {
		if(supercl)
			out.printf(format, SUPER, c.getName());
		else
			out.printf(format, CLASS, c.getName());
		out.println();
	}
	
	private void printConstructor(Constructor con) {
		out.printf(format, CON, con);
		out.println();	
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
				out.printf(format,  FIELD, field);
				out.printf(format, "value", value);
				out.println();
			} else if(val.getClass().isArray()){
				value += val.getClass() + " (hash:" + val.hashCode() + ")";
				// traverse array
				out.printf(format,  FIELD, field);
				out.printf(format, "value", value);
				out.println();
			} else {
				value += val.getClass() + " (hash:" + val.hashCode() + ")";
				out.printf(format,  FIELD, field);
				out.printf(format, "value", value);
				out.println();
				if(rec) {
					out.println("<< Discovered object \"" + f.getName() + "\", recursing... >>");
					out.println();
					inspect(val,rec);
					out.println("<< Recursion into \"" + f.getName() + "\" complete >>\n\n"
							+ "<< Resuming inspection of " + o.getClass().getName() + " >>\n");	
				}			
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
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
