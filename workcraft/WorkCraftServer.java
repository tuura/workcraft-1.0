package workcraft;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.python.util.*;
import org.python.core.*;

import workcraft.editor.BasicEditable;

public class WorkCraftServer {
	private boolean init_ok = false;
	private PluginManager pmgr;
	public ModelManager mmgr;
	HashMap <String, Tool> toolInstances = new HashMap <String, Tool>();

	public void initialize()
	{
		if (init_ok)
		{
			System.out.println("Server already initialized.");
			return;
		}
		
		System.out.println("Initializing server...");
		
		 
		System.out.println ("Server initialized.\n\n");
		System.out.println ("Initializing components...");
		
		pmgr = new PluginManager();
		pmgr.loadManifest("Plugins");
		mmgr = new ModelManager();

		System.out.println("Models:");

		LinkedList<Class> models = pmgr.getClassesBySuperclass(DocumentBase.class);
		for (Class cls : models) {
			mmgr.addModel(cls);		
		}

		System.out.println("Components:");

		LinkedList<Class> components = pmgr.getClassesBySuperclass(BasicEditable.class);
		for (Class cls : components) {
			mmgr.addComponent(cls);		
		}

		LinkedList<Class> tools = pmgr.getClassesByInterface(Tool.class);
		System.out.println("Tools:");
		for (Class cls : tools) {
			mmgr.addTool(cls, this);		
		}

		init_ok = true;
		
		System.out.println ("Components initialized.\n\n");
	}
	
	public void registerToolInstance (Tool tool) {
		toolInstances.put(tool.getClass().getName(), tool);
	}
	
	public Tool getToolInstance (String className) {
		return (Tool) toolInstances.get(className);
	}
	
	public Tool getToolInstance (Class cls) {
		return getToolInstance (cls.getName());
	}

	public LinkedList<Class> getClassesByInterface(Class interf)
	{
		return pmgr.getClassesByInterface(interf);
	}

	public LinkedList<Class> getClassesBySuperclass(Class superclass)
	{
		return pmgr.getClassesBySuperclass(superclass);
	}
}