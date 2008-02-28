package workcraft.stg;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import workcraft.InvalidConnectionException;
import workcraft.UnsupportedComponentException;
import workcraft.common.DefaultConnection;
import workcraft.editor.BasicEditable;
import workcraft.editor.EditableConnection;
import workcraft.petri.EditablePetriPlace;
import workcraft.petri.EditablePetriTransition;
import workcraft.petri.PetriModel;
import workcraft.util.Vec2;

public class STGModel extends PetriModel {
	public static final UUID _modeluuid = UUID.fromString("10418180-D733-11DC-A679-A32656D89593");
	public static final String _displayname = "Petri Net (STG)";
	
	protected String inputList = "";
	protected String outputList = "";

	// returns all the labels of transitions
	// /, ~, +, and - are ignored
	public HashSet<String> getTransitionLabelList(String label) {
		
		// check if there are any transitions of places bearing this label
		List<EditablePetriTransition> cmp = new LinkedList<EditablePetriTransition>();
		getTransitions(cmp);
		
		HashSet<String> s= new HashSet<String>();
		Pattern p = Pattern.compile("([a-zA-Z\\_][a-zA-Z\\_0-9]+)[\\+\\-~]?(\\/[0-9]+)?$");
		for (BasicEditable be: cmp) {
			Matcher m = p.matcher(be.getLabel());
			if (m.matches()&&!s.contains(m.group(1))) s.add(m.group(1));
		}
		return s;
	}
	
	// returns all non-empty labels of places
	public HashSet<String> getPlaceLabelList(String label) {
		
		List<EditablePetriPlace> cmp = new LinkedList<EditablePetriPlace>();
		getPlaces(cmp);

		HashSet<String> s= new HashSet<String>();
		for (BasicEditable be: cmp) {
			String l = be.getLabel();
			if (!l.equals("")) {
				if (!s.contains(l)) s.add(l);
			}
		}
		return s;
	}
	
	public EditableConnection createConnection(BasicEditable first, BasicEditable second) throws InvalidConnectionException {
		if (first == second)
			throw new InvalidConnectionException ("Can't connect to self!");

		if (first instanceof EditableSTGTransition && second instanceof EditableSTGTransition) {
			try {
				EditableSTGPlace p = new EditableSTGPlace(getRoot());

				Vec2 v1 = first.transform.getTranslation2d();
				v1.add(second.transform.getTranslation2d());
				v1.mul(0.5f);
				p.transform.translateAbs(v1);
				
				DefaultConnection con = new DefaultConnection(first, p);
				
				if (p.addIn(con) && ((EditableSTGTransition)first).addOut(con)) {
					connections.add(con);
				}

				con = new DefaultConnection(p, second);
				if (p.addOut(con) && ((EditableSTGTransition)second).addIn(con)) {
					connections.add(con);
					return con;
				}
				
			} catch (UnsupportedComponentException e) {
				e.printStackTrace();
				return null;
			}
		} else if (first instanceof EditableSTGPlace && second instanceof EditableSTGPlace) {

			try {
				EditableSTGTransition t = new EditableSTGTransition(getRoot());
				Vec2 v1 = first.transform.getTranslation2d();
				v1.add(second.transform.getTranslation2d());
				v1.mul(0.5f);
				t.transform.translateAbs(v1);				

				DefaultConnection con = new DefaultConnection(first, t);
				if (t.addIn(con) && ((EditableSTGPlace)first).addOut(con)) {
					connections.add(con);
				}

				con = new DefaultConnection(t, second);
				if (t.addOut(con) && ((EditableSTGPlace)second).addIn(con)) {
					connections.add(con);
					return con;
				}
			} catch (UnsupportedComponentException e) {
				e.printStackTrace();
				return null;

			}
		} else {
			EditableSTGPlace p;
			EditableSTGTransition t;
			if (first instanceof EditableSTGPlace) {
				p = (EditableSTGPlace)first;
				t = (EditableSTGTransition)second;
				DefaultConnection con = new DefaultConnection(p, t);
				if (p.addOut(con) && t.addIn(con)) {
					connections.add(con);
					return con;
				}
			} else {
				p = (EditableSTGPlace)second;
				t = (EditableSTGTransition)first;
				DefaultConnection con = new DefaultConnection(t, p);
				if (p.addIn(con) && t.addOut(con)) {
					connections.add(con);
					return con;
				}
			}
		}
		return null;	
	}
	
	public STGModel() {
		setShorthandNotation(true); // shorthand notation by default
		BasicEditable.setLabelOrder(1); // default order is 'Label on Top'
	}
	
	public List<String>getEditableProperties()  {
		
		List<String> list = super.getEditableProperties(); 
		list.add("str,Inputs,getSTGInputList,setSTGInputList");
		list.add("str,Outputs,getSTGOutputList,setSTGOutputList");
		
		return list;
	}
	
	// when loading is finished, do update transition types
	public void loadEnd() {
		super.loadEnd();
		updateTransitionTypes();
	}
	
	public void updateTransitionTypes() {
		
		if (isLoading()) return;
		
		if (inputList=="" && outputList=="") return;
		
		String [] inputs = inputList.split(" ");
		String [] outputs = outputList.split(" ");
		
		// set all to internals, if no name, then it is dummy
		for (EditablePetriTransition t : transitions) {
			if (t.getLabel()=="") {
				((EditableSTGTransition)t).setTransitionType(3);
			} else
				((EditableSTGTransition)t).setTransitionType(0);
		}
		
		String pat = "[\\+\\-~]?(\\/[0-9]+)?$";
		
		// set outputs
		for (String o : outputs) {
			Pattern p = Pattern.compile(o+pat);
			for (EditablePetriTransition t : transitions) {
				Matcher m = p.matcher(t.getLabel());
				if (m.matches())
					((EditableSTGTransition)t).setTransitionType(2);
			}
		}

		// set inputs
		for (String i : inputs) {
			Pattern p = Pattern.compile(i+pat);
			for (EditablePetriTransition t : transitions) {
				Matcher m = p.matcher(t.getLabel());
				if (m.matches())
					((EditableSTGTransition)t).setTransitionType(1);
			}
		}

	}
	
	public String getSTGInputList() {
		return inputList;
	}
	
	public void setSTGInputList(String inputs) {
		inputList = inputs;
		updateTransitionTypes();
	}

	public String getSTGOutputList() {
		return outputList;
	}
	
	public void setSTGOutputList(String outputs) {
		outputList = outputs;
		updateTransitionTypes();
	}
	
	public void fromXmlDom(Element element)  {
		Element el = (Element)element.getElementsByTagName("options").item(0);
		if (el!=null) {
			setSTGInputList(el.getAttribute("inputTransitions"));
			setSTGOutputList(el.getAttribute("outputTransitions"));
		}
		super.fromXmlDom(element);
	}

	public Element toXmlDom(Element parent_element) {
		
		super.toXmlDom(parent_element);

		org.w3c.dom.Document d = parent_element.getOwnerDocument();

		if (getSTGInputList()+getSTGOutputList()!="") {
			
			Element ee = d.createElement("options");
			if (getSTGInputList()!="")
				ee.setAttribute("inputTransitions", getSTGInputList());
			
			if (getSTGOutputList()!="")
				ee.setAttribute("outputTransitions", getSTGOutputList());
			
			parent_element.appendChild(ee);
			return ee;
		}
		
		return null;
	}
	
}
