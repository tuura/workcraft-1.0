package workcraft.petri;


import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.UUID;

import org.python.core.PyObject;
import org.w3c.dom.Element;

import workcraft.DuplicateIdException;
import workcraft.UnsupportedComponentException;
import workcraft.WorkCraftServer;
import workcraft.common.DefaultConnection;
import workcraft.editor.BasicEditable;
import workcraft.editor.BoundingBox;
import workcraft.util.Colorf;
import workcraft.util.Mat4x4;
import workcraft.util.Vec2;
import workcraft.visual.Painter;
import workcraft.visual.ShapeMode;

public class EditablePetriTransition extends BasicEditable {
	public static final UUID _modeluuid = UUID.fromString("65f89260-641d-11db-bd13-0800200c9a66");
	public static final String _displayname = "Transition";
	public static final String _hotkey = "t";
	public static final int _hotkeyvk = KeyEvent.VK_T;
	//private static VertexBuffer geometry = null;

	private LinkedList<EditablePetriPlace> out;
	private LinkedList<EditablePetriPlace> in;

	public boolean canFire = false;
	public boolean canWork = false;
	
	private static Colorf transitionColor = new Colorf(1.0f, 1.0f, 1.0f, 1.0f);
	private static Colorf selectedTransitionColor = new Colorf(1.0f, 0.9f, 0.9f, 1.0f);
	private static Colorf enabledTransitionColor = new Colorf(0.6f, 0.6f, 1.0f, 1.0f);
	private static Colorf transitionOutlineColor = new Colorf(0.0f, 0.0f, 0.0f, 1.0f);
	private static Colorf selectedTransitionOutlineColor = new Colorf(0.5f, 0.0f, 0.0f, 1.0f);
	private static Colorf userTransitionOutlineColor = new Colorf(0.0f, 0.6f, 0.0f, 1.0f);
	
	public boolean hits(Vec2 pointInViewSpace) {
		
		Vec2 v = new Vec2(pointInViewSpace);
		transform.getViewToLocalMatrix().transform(v);
		if (!getIsDrawBorders()) {
			float mx = Math.max(Math.abs(v.getX()),Math.abs(v.getY()));
			return mx<=0.04;
		}
		
		return boundingBox.isInside(v);
	}
	
	protected boolean getIsDrawBorders() {
		
		WorkCraftServer server = ownerDocument.getServer();
		PyObject po;
		if (server != null) 
			po = server.python.get("_draw_labels");
		else
			po = null;
		
		return canWork||!getIsShorthandNotation()||getLabel().equals("")||po==null||!po.__nonzero__();
		
	}
	
	protected float getLabelYOffset() {
		return (getLabelOrder()==0)?0.03f:-0.07f;
	}

	public LinkedList<EditablePetriPlace> getOut() {
		return (LinkedList<EditablePetriPlace>)out.clone();
	}

	public LinkedList<EditablePetriPlace> getIn() {
		return (LinkedList<EditablePetriPlace>)in.clone();
	}
	public void removeIn(EditablePetriPlace t) {
		in.remove(t);
	}

	public void removeOut(EditablePetriPlace t) {
		out.remove(t);
	}

	public boolean addIn(DefaultConnection con) {
		EditablePetriPlace t = (EditablePetriPlace)con.getFirst();
		if (in.contains(t))
			return false;
		in.add(t);
		connections.add(con);
		return true;
	}

	public boolean addOut(DefaultConnection con) {
		EditablePetriPlace t = (EditablePetriPlace)con.getSecond();
		if (out.contains(t))
			return false;
		out.add(t);
		connections.add(con);
		return true;
	}

	public EditablePetriTransition(BasicEditable parent) throws UnsupportedComponentException {
		super(parent);
		boundingBox.setExtents(new Vec2(-0.05f, -0.05f), new Vec2(0.05f, 0.05f));
		in = new LinkedList<EditablePetriPlace>();
		out = new LinkedList<EditablePetriPlace>();
	}

	public void doDraw(Painter p) {
		p.setTransform(transform.getLocalToViewMatrix());
		p.setShapeMode(ShapeMode.FILL);


		if (selected)
			p.setFillColor(selectedTransitionOutlineColor);
		else
			if (canWork)
				p.setFillColor(userTransitionOutlineColor);
			else
				p.setFillColor(transitionOutlineColor);
		
		if (getIsDrawBorders())
		// draw the rectangle only if there is no text in it
			p.drawRect(-0.05f, 0.05f, 0.05f, -0.05f);
		
		if (selected)
			p.setFillColor(selectedTransitionColor);
		else
			if (canFire)
				p.setFillColor(enabledTransitionColor);
			else
				p.setFillColor(transitionColor);
		
		p.drawRect(-0.04f, 0.04f, 0.04f, -0.04f);
		
		super.doDraw(p);
	}

	@Override
	public BasicEditable getChildAt(Vec2 point) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void update(Mat4x4 matView) {
		// TODO Auto-generated method stub

	}

	public void fromXmlDom(Element element) throws DuplicateIdException {
//		NodeList nl = element.getElementsByTagName("transition");
//		Element te = (Element) nl.item(0);
		super.fromXmlDom(element);		
	}

	public Element toXmlDom(Element parent_element) {
		Element ee = super.toXmlDom(parent_element); 
		org.w3c.dom.Document d = ee.getOwnerDocument();
		Element ppe = d.createElement("transition");
		ee.appendChild(ppe);
		return ee;
	}
	
	public void simAction(int flag) {
		if (flag == MouseEvent.BUTTON1) {
			canWork = !canWork;
		}
	}	
}