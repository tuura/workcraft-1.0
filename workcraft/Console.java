package workcraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Console {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WorkCraftServer server  = new WorkCraftServer();
		server.loadPlugins("Plugins");
		
		BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
		
		Context cx = new Context();
		Context.enter();
		
		ScriptableObject scope = cx.initStandardObjects();
		
		Object wrappedServer = Context.javaToJS(server, scope);
		ScriptableObject.putProperty(scope, "framework", wrappedServer);
		scope.setAttributes("framework", ScriptableObject.READONLY);
		scope.sealObject();
		
		Scriptable newScope = cx.newObject(scope);
	    newScope.setPrototype(scope);
	    newScope.setParentScope(null);
		System.out.println("Workcraft revision 2 (Metastability strikes back)\n");
		
	
		
		
		while (true) {
			System.out.print ("js>");
			try {
				String line = in.readLine();
				Object result = cx.evaluateString(newScope, line, "stdin", 1, null);
				System.out.println(result.toString());
			}
			catch (org.mozilla.javascript.EcmaError e) {
				System.err.println(e.getMessage());
			}
			catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		
		Context.exit();
	}
}
