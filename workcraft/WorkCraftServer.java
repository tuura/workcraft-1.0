package workcraft;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import workcraft.editor.BasicEditable;

public class WorkCraftServer {
	protected PluginManager pmgr = null;
	protected ModelManager mmgr = null;
	HashMap <String, Tool> toolInstances = new HashMap <String, Tool>();

	public void loadPlugins(String directory) {
		System.out.println("Loading plugins from \""+directory+"\"...");
		
		if (pmgr == null)
			pmgr = new PluginManager();
		
		pmgr.loadManifest(directory);
		
		if (mmgr == null)
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
		System.out.println ("Load complete.\n\n");
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

	public LinkedList<Class> getComponentsByModelUUID(UUID uuid) {
		return mmgr.getComponentsByModelUUID(uuid);
	}

	public Class getModelByUUID(UUID uuid) {
		return mmgr.getModelByUUID(uuid);
	}

	public LinkedList<Class> getModelList() {
		return mmgr.getModelList();
	}

	public String[] getModelNames() {
		LinkedList<Class> list = mmgr.getModelList();
		String a[] = new String[list.size()];
		int i=0;
		for (Class cls : list)
			a[i++] = cls.getName();
		return a;
	}
	
	public LinkedList<Tool> getMultiModelTools() {
		return mmgr.getMultiModelTools();
	}

	public LinkedList<Class> getToolsByModelUUID(UUID uuid) {
		return mmgr.getToolsByModelUUID(uuid);
	}
}