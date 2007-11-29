package workcraft;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import workcraft.editor.BasicEditable;
import workcraft.editor.EditableConnection;
import workcraft.editor.GroupNode;

public class Framework {
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

	public UUID getModelUUID(Class model_class) {
		return mmgr.getModelUUID(model_class);
	}
	
	public Document openDocument(String path) throws DocumentOpenException {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			org.w3c.dom.Document xmldoc;
			Document doc;
			DocumentBuilder db;

			try {
				db = dbf.newDocumentBuilder();
				xmldoc = db.parse(new File(path));
			} catch (ParserConfigurationException e) {
				throw new DocumentOpenException(e.getMessage());
			} catch (IOException e) {
				throw new DocumentOpenException(e.getMessage());
			} catch (SAXException e) {
				throw new DocumentOpenException(e.getMessage());
			}

			Element xmlroot = xmldoc.getDocumentElement();

			try {
				// TODO: proper validation
				if (xmlroot.getNodeName()!="workcraft")
					throw new DocumentOpenException("Invalid root element");
				NodeList nl;

				nl = xmlroot.getElementsByTagName("document");
				Element d = (Element)nl.item(0);

				UUID model_uuid = UUID.fromString(d.getAttribute("model-uuid"));

				Class model_class = getModelByUUID(model_uuid);
				if (model_class == null)
					throw new DocumentOpenException("Unrecognized model id - "+d.getAttribute("model-uuid"));

				doc = (Document)model_class.newInstance();
				
				nl = xmldoc.getElementsByTagName("editable");
				Element re = (Element)nl.item(0);

				GroupNode root = new GroupNode(doc);
				doc.setRoot(root);
				
				if (re.getAttribute("class").equals(GroupNode.class.getName()))
					try {
						root.fromXmlDom(re);
					} catch (DuplicateIdException e1) {
						e1.printStackTrace();
					}
					else
						System.err.println("Invalid file format: invalid root group element (id="+re.getAttribute("id")+"; class="+ GroupNode.class.getName() +")");

				nl = xmldoc.getElementsByTagName("editable-connection");
				for (int i=0; i<nl.getLength(); i++ ) {
					Element e = (Element)nl.item(i);

					BasicEditable first = doc.getComponentById(Integer.parseInt(e.getAttribute("first")));
					BasicEditable second = doc.getComponentById(Integer.parseInt(e.getAttribute("second")));

					if (first == null) {
						System.err.println ("Component "+e.getAttribute("first")+" not found while creating connections!");
					} else if (second == null) {
						System.err.println ("Component "+e.getAttribute("second")+" not found while creating connections!");
					} else
						try {
							EditableConnection con = doc.createConnection(first, second);
							con.fromXmlDom(e);
						} catch (InvalidConnectionException ex) {
							ex.printStackTrace();
						} catch (DuplicateIdException ex) {
							ex.printStackTrace();
						}
				}
				return doc;
			} 	 catch (InstantiationException e2) {
				throw new DocumentOpenException (e2.getMessage());
			} catch (IllegalAccessException e2) {
				throw new DocumentOpenException (e2.getMessage());
			}/* catch (UnsupportedComponentException e) {
				e.printStackTrace();
				throw new DocumentOpenException (e.getMessage());
			}*/
	}
}