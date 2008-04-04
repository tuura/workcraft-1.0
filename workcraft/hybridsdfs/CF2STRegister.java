package workcraft.hybridsdfs;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import workcraft.DocumentBase;
import workcraft.DuplicateIdException;
import workcraft.Document;
import workcraft.UnsupportedComponentException;
import workcraft.WorkCraftServer;
import workcraft.editor.BasicEditable;
import workcraft.sdfs.RegisterState;
import workcraft.sdfs.SDFSNode;
import workcraft.sdfs.SDFSRegisterBase;
import workcraft.util.Colorf;
import workcraft.util.Mat4x4;
import workcraft.util.Vec2;
import workcraft.visual.BlendMode;
import workcraft.visual.LineMode;
import workcraft.visual.Painter;
import workcraft.visual.ShapeMode;
import workcraft.visual.shapes.CompoundPath;
import workcraft.visual.shapes.Shape;
import workcraft.visual.shapes.Vertex;

public class CF2STRegister extends SDFSRegisterBase   {
	public static final UUID _modeluuid = UUID.fromString("aab78c50-e6bf-11dc-95ff-0800200c9a66");
	public static final String _displayname = "CF-ST Register";

	private static Colorf token_color = new Colorf (0.0f, 0.0f, 0.0f, 1.0f);
	
	private static Colorf or_token_color = new Colorf (0.0f, 0.0f, 0.0f, 1.0f);
	private static Colorf and_token_color = new Colorf (0.0f, 0.0f, 0.0f, 1.0f);
	
	private static Shape triangleShape;
	
	private boolean marked;
	private boolean orMarked;
	private boolean andMarked;	
	
	private RegisterState state = RegisterState.DISABLED;
	private RegisterState forward_state = RegisterState.DISABLED;
	private RegisterState backward_state = RegisterState.DISABLED;	
	
	
	private boolean can_work = false;
	private boolean waiting_for_user = false;
	private boolean or_waiting = false;
	private boolean and_waiting = false;

	private LinkedList<SDFSNode> out = new LinkedList<SDFSNode>();
	private LinkedList<SDFSNode> in = new LinkedList<SDFSNode>();

	private String markFunc;
	private String unmarkFunc;
	private String enableFunc;
	private String disableFunc;
	private String fwdEnableFunc;
	private String backEnableFunc;
	private String fwdDisableFunc;
	private String backDisableFunc;
	private String orMarkFunc;
	private String orUnmarkFunc;
	private String andMarkFunc;
	private String andUnmarkFunc;

	
	public CF2STRegister(BasicEditable parent) throws UnsupportedComponentException {
		super(parent);
		boundingBox.setExtents(new Vec2(-0.1f, -0.05f), new Vec2(0.1f, 0.05f) );
		marked = false;
		
		CompoundPath tpath = new CompoundPath();
		tpath.addElement(new Vertex(0.0f, 0.0f));
		tpath.addElement(new Vertex(0.5f, 1.0f));
		tpath.addElement(new Vertex(1.0f, 1.0f));
		tpath.setClosed(true);
		
		triangleShape = new Shape(tpath);
	}

	@Override
	public void doDraw(Painter p) {
		p.setTransform(transform.getLocalToViewMatrix());
		
		Colorf frame_color;
		
		if (selected)
			frame_color = selected_frame_color;
		else
			if (can_work)
				frame_color = active_frame_color;
			else
				if (highlight && ((DocumentBase)ownerDocument).isShowHighlight())
					frame_color = (((DocumentBase)ownerDocument).getHighlightColor());
				else
					frame_color = inactive_frame_color;

		p.setShapeMode(ShapeMode.FILL_AND_OUTLINE);
		p.setFillColor(disabled_color);
		p.setLineColor(frame_color);
		p.setLineMode(LineMode.SOLID);
		p.setLineWidth(0.005f);

		p.drawRect(-0.1f, 0.05f, 0.1f, -0.05f);

		if (state == RegisterState.ENABLED)
			p.setFillColor(enabled_color);
		else
			p.setFillColor(disabled_color);
		p.drawRect(0.0f, 0.05f, 0.08f, -0.05f);
		
		if (forward_state == RegisterState.ENABLED)
			p.setFillColor(enabled_color);
		else
			p.setFillColor(disabled_color);
		
		p.drawRect(-0.08f, 0.05f, 0.0f, 0.0f);

		if (backward_state == RegisterState.ENABLED)
			p.setFillColor(enabled_color);
		else
			p.setFillColor(disabled_color);

		p.drawRect(-0.08f, 0.0f, 0.0f, -0.05f);
		p.drawLine(-0.08f, 0.0f, 0.0f, 0.0f);
		
		if (waiting_for_user) {
			p.blendEnable();
			p.setBlendConstantAlpha(0.5f);
			p.setBlendMode(BlendMode.CONSTANT_ALPHA);

			if (state == RegisterState.DISABLED) {
				p.setFillColor(token_color);
				p.setShapeMode(ShapeMode.FILL);
				p.drawCircle(0.015f, 0.04f, 0.0f);
			} else {
				p.setFillColor(enabled_color);
				p.setLineColor(token_color);
				p.setShapeMode(ShapeMode.FILL_AND_OUTLINE);
				p.setLineMode(LineMode.SOLID);
				p.setLineWidth(0.0025f);
				p.drawCircle(0.015f, 0.04f, 0.0f);
			}
			p.blendDisable();
		} else {
			if (marked) {
				p.setFillColor(token_color);
				p.setShapeMode(ShapeMode.FILL);
				p.drawCircle(0.015f, 0.04f, 0.0f);

			}
		}

		if (or_waiting) {
			p.blendEnable();
			p.setBlendConstantAlpha(0.5f);
			p.setBlendMode(BlendMode.CONSTANT_ALPHA);


			if (orMarked) { // shadow
				p.setFillColor(or_token_color);
				p.setShapeMode(ShapeMode.FILL);
				//p.drawString(s, location, height, align)
				p.drawCircle(0.015f, -0.04f, 0.025f);
			} 
			else // excitement
			{
				p.setFillColor(enabled_color);
				p.setLineColor(or_token_color);
				p.setShapeMode(ShapeMode.FILL_AND_OUTLINE);
				p.setLineMode(LineMode.SOLID);
				p.setLineWidth(0.0025f);
				p.drawCircle(0.015f, -0.04f, 0.025f);
			}
			p.blendDisable();
		} else {
			if (orMarked) { // solid
				p.setFillColor(or_token_color);
				p.setShapeMode(ShapeMode.FILL);
				p.drawCircle(0.015f, -0.04f, 0.025f);
			} 
		}

		if (and_waiting) {
			p.blendEnable();
			p.setBlendConstantAlpha(0.5f);
			p.setBlendMode(BlendMode.CONSTANT_ALPHA);

			if (andMarked) { // shadow
				p.setFillColor(or_token_color);
				p.setShapeMode(ShapeMode.FILL);
				p.drawRect(-0.025f, -0.015f, -0.055f, -0.045f);
			} 
			else // excitement
			{
				p.setFillColor(enabled_color);
				p.setLineColor(or_token_color);
				p.setShapeMode(ShapeMode.FILL_AND_OUTLINE);
				p.setLineMode(LineMode.SOLID);
				p.setLineWidth(0.0025f);
				p.drawRect(-0.025f, -0.015f, -0.055f, -0.045f);
			}
			p.blendDisable();
		} else {
			if (andMarked) { // solid
				p.setFillColor(or_token_color);
				p.setShapeMode(ShapeMode.FILL);
				p.drawRect(-0.025f, -0.015f, -0.055f, -0.045f);
			} 
		}		
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

	public List<String> getEditableProperties() {
		List<String> list = super.getEditableProperties();
		list.add("bool,Marked,isMarked,setMarked");
		list.add("bool,OR marked,isOrMarked,setOrMarked");
		list.add("bool,AND marked,isAndMarked,setAndMarked");
		list.add("str,Enabling,getEnableFunc,setEnableFunc");
		list.add("str,Disabling,getDisableFunc,setDisableFunc");
		list.add("str,Marking,getMarkFunc,setMarkFunc");
		list.add("str,Unmarking,getUnmarkFunc,setUnmarkFunc");
		list.add("str,Fwd enabling,getFwdEnableFunc,setFwdEnableFunc");
		list.add("str,Back enabling,getBackEnableFunc,setBackEnableFunc");
		list.add("str,Fwd disabling,getFwdDisableFunc,setFwdDisableFunc");
		list.add("str,Back disabling,getBackDisableFunc,setBackDisableFunc");
		list.add("str,OR marking,getOrMarkFunc,setOrMarkFunc");
		list.add("str,OR unmarking,getOrUnmarkFunc,setOrUnmarkFunc");
		list.add("str,AND marking,getAndMarkFunc,setAndMarkFunc");
		list.add("str,AND unmarking,getAndUnmarkFunc,setAndUnmarkFunc");
		return list;
	}

	public void fromXmlDom(Element element) throws DuplicateIdException {
		NodeList nl = element.getElementsByTagName("cf2st-register");
		Element ne = (Element) nl.item(0);
		setMarked(Boolean.parseBoolean(ne.getAttribute("marked")));
		setOrMarked(Boolean.parseBoolean(ne.getAttribute("or-token")));
		setAndMarked(Boolean.parseBoolean(ne.getAttribute("and-token")));
		
		markFunc = ne.getAttribute("mark-func");
		unmarkFunc = ne.getAttribute("unmark-func");
		enableFunc = ne.getAttribute("enable-func");
		disableFunc = ne.getAttribute("disable-func");
		fwdEnableFunc = ne.getAttribute("fwd-enable-func");
		fwdDisableFunc = ne.getAttribute("fwd-disable-func");
		backEnableFunc = ne.getAttribute("back-enable-func");
		backDisableFunc = ne.getAttribute("back-disable-func");
		orMarkFunc = ne.getAttribute("or-mark-func");
		orUnmarkFunc = ne.getAttribute("or-unmark-func");
		andMarkFunc = ne.getAttribute("and-mark-func");
		andUnmarkFunc = ne.getAttribute("and-unmark-func");
		
		
		super.fromXmlDom(element);		
	}

	public Element toXmlDom(Element parent_element) {
		Element ee = super.toXmlDom(parent_element);
		org.w3c.dom.Document d = ee.getOwnerDocument();
		Element ppe = d.createElement("cf2st-register");
		ppe.setAttribute("marked", Boolean.toString(isMarked()));
		ppe.setAttribute("or-token", Boolean.toString(isOrMarked()));
		ppe.setAttribute("and-token", Boolean.toString(isAndMarked()));
		
		ppe.setAttribute("mark-func", markFunc);
		ppe.setAttribute("unmark-func", unmarkFunc);		
		ppe.setAttribute("enable-func", enableFunc);
		ppe.setAttribute("disable-func", disableFunc);
		ppe.setAttribute("fwd-enable-func", fwdEnableFunc);
		ppe.setAttribute("fwd-disable-func", fwdDisableFunc);
		ppe.setAttribute("back-enable-func", backEnableFunc);
		ppe.setAttribute("back-disable-func", backDisableFunc);
		ppe.setAttribute("or-mark-func", orMarkFunc);
		ppe.setAttribute("or-unmark-func", orUnmarkFunc);
		ppe.setAttribute("and-mark-func", andMarkFunc);
		ppe.setAttribute("and-unmark-func", andUnmarkFunc);
		ee.appendChild(ppe);
		return ee;
	}

	public boolean simTick(int time_ms) {
		WorkCraftServer server = ownerDocument.getServer();
		HybridSDFSModel doc = (HybridSDFSModel)ownerDocument;
		boolean user = doc.isUserInteractionEnabled();
		
		switch (state) {
		case ENABLED:
			if (!isMarked()) {
				if (server.python.eval(markFunc).__nonzero__()) {
					if ( doc.isUserInteractionEnabled())
					{
						if (can_work) {
							setMarked(true);
							waiting_for_user = false;
							can_work = false;
							
							doc.addTraceEvent(this.id + "/marked");
						} else {
							waiting_for_user = true;
						}
					}
					else	{
						setMarked(true);
						doc.addTraceEvent(this.id + "/marked");
					}
				}

			} else {
				if (server.python.eval(disableFunc).__nonzero__()) {
					state = RegisterState.DISABLED;
					doc.addTraceEvent(this.id + "/disabled");
				}
			}
			break;
			
		case DISABLED:
			if (isMarked()) {
				boolean can_unmark = server.python.eval(unmarkFunc).__nonzero__();
				if (can_unmark) {
					if ( doc.isUserInteractionEnabled() ) {
						if (can_work) {
							setMarked(false);							
							can_work = false;
							waiting_for_user = false;
							doc.addTraceEvent(this.id + "/unmarked");
						} else {
							waiting_for_user = true;
						}
					}
					else {
						setMarked(false);
						doc.addTraceEvent(this.id + "/unmarked");
					}
				}
			} else {
				if ( server.python.eval(enableFunc).__nonzero__()) {
					state = RegisterState.ENABLED;
					doc.addTraceEvent(this.id + "/enabled");
				}
			}
			break;				
		}
		
		switch (forward_state) {
		case ENABLED:
			if (server.python.eval(fwdDisableFunc).__nonzero__()) {
				forward_state = RegisterState.DISABLED;
				doc.addTraceEvent(this.id + "/fwd_dis");
			}
			break;
		case DISABLED:
			if (server.python.eval(fwdEnableFunc).__nonzero__()) {
				forward_state = RegisterState.ENABLED;
				doc.addTraceEvent(this.id + "/fwd_enb");
			}
			break;				
		}

		switch (backward_state) {
		case ENABLED:
			if (server.python.eval(backDisableFunc).__nonzero__()) {
				backward_state = RegisterState.DISABLED;
				doc.addTraceEvent(this.id + "/back_dis");
			}
			break;
		case DISABLED:
			if (server.python.eval(backEnableFunc).__nonzero__()) {
				backward_state = RegisterState.ENABLED;
				doc.addTraceEvent(this.id + "/back_enb");
			}
			break;				
		}

		if (!isOrMarked()) {
			if (server.python.eval(orMarkFunc).__nonzero__()) {
				if (user) {
					if (can_work) {
						orMarked = true;
						doc.addTraceEvent(this.id + "/or_mrk");
						can_work = false;
						or_waiting = false;
					} else {
						or_waiting = true;												
					}
				}
				else {
					or_waiting = false;
					orMarked = true;
					doc.addTraceEvent(this.id + "/or_mrk");
				}
			}
		} else {


			if (server.python.eval(orUnmarkFunc).__nonzero__()) {
				if (user) {
					if (can_work) {
						orMarked = false;
						doc.addTraceEvent(this.id + "/or_unmrk");
						can_work = false;
						or_waiting = false;
					} else {
						or_waiting = true;												
					}
				}
				else {
					or_waiting = false;
					doc.addTraceEvent(this.id + "/or_unmrk");
					orMarked = false;
				}
			}
		}

		if (!isAndMarked()) {

			if (server.python.eval(andMarkFunc).__nonzero__())
				if (user) {
					if (can_work) {
						andMarked = true;
						doc.addTraceEvent(this.id + "/and_mrk");
						can_work = false;
						and_waiting = false;
					} else {
						and_waiting = true;												
					}
				}
				else {
					and_waiting = false;
					andMarked = true;
					doc.addTraceEvent(this.id + "/and_mrk");
				}
		} else {

			if (server.python.eval(andUnmarkFunc).__nonzero__())
				if (user) {
					if (can_work) {
						andMarked = false;
						doc.addTraceEvent(this.id + "/and_unmrk");
						can_work = false;
						and_waiting = false;
					} else {
						and_waiting = true;												
					}
				}
				else {
					and_waiting = false;
					andMarked = false;
					doc.addTraceEvent(this.id + "/and_unmrk");
				}
		}
		
		return true;
	}

	@Override
	public void rebuildRuleFunctions() {
		
		markFunc = expandRule("self re,r-postset !m");
		unmarkFunc = expandRule("self !re,r-postset m");
		
		orMarkFunc = expandRule("self !am,self rfe|self rbe,r-preset !am");
		orUnmarkFunc = expandRule("self am,self !rfe|self !rbe,r-preset am");
//		andMarkFunc = expandRule("self om,self rfe,self rbe");
		andMarkFunc = expandRule("self om,self rfe,self rbe,r-preset om");
//		andUnmarkFunc = expandRule("self !om,self !rfe,self !rbe");
		andUnmarkFunc = expandRule("self !om,self !rfe,self !rbe,r-preset !om");
		
		enableFunc = expandRule("self !m,self om");
		disableFunc = expandRule("self m,self !om");
		
		fwdEnableFunc = expandRule("self !am,preset:l lfe,preset:r om");
		fwdDisableFunc = expandRule("self am,preset:l lfr,preset:r !om");
		backEnableFunc = expandRule("self !am,self m");
		backDisableFunc = expandRule("self am,self !m");
	}

	public void simAction(int flag) {
		if (flag == MouseEvent.BUTTON1) {
			can_work = !can_work;
		}
	}

	public Boolean isMarked() {
		return marked;
	}
	
	public void setMarked(Boolean marked) {
		this.marked = marked;
	}

	public boolean isEnabled() {
		return state == RegisterState.ENABLED;
	}

	public boolean canWork() {
		return can_work;
	}

	public void setState(RegisterState state) {
		this.state = state;
	}

	public RegisterState getState() {
		return state;
	}

	public String getMarkFunc() {
		return markFunc;
	}

	public void setMarkFunc(String markFunc) {
		this.markFunc = markFunc;
	}

	public String getUnmarkFunc() {
		return unmarkFunc;
	}

	public void setUnmarkFunc(String unmarkFunc) {
		this.unmarkFunc = unmarkFunc;
	}

	public String getEnableFunc() {
		return enableFunc;
	}

	public void setEnableFunc(String enableFunc) {
		this.enableFunc = enableFunc;
	}

	public String getDisableFunc() {
		return disableFunc;
	}

	public void setDisableFunc(String disableFunc) {
		this.disableFunc = disableFunc;
	}
	public Boolean isForwardEnabled() {
		return forward_state == RegisterState.ENABLED;
	}

	public Boolean isBackwardEnabled() {
		return backward_state == RegisterState.ENABLED;
	}

	public void setForwardState(RegisterState forward_state) {
		this.forward_state = forward_state;
	}

	public RegisterState getForwardState() {
		return forward_state;
	}

	public void setBackwardState(RegisterState backward_state) {
		this.backward_state = backward_state;
	}

	public RegisterState getBackwardState() {
		return backward_state;
	}

	public Boolean isOrMarked() {
		return orMarked;		
	}

	public void setOrMarked(Boolean marked) {
		orMarked = marked;
	}

	public Boolean isAndMarked() {
		return andMarked;		
	}

	public void setAndMarked(Boolean marked) {
		andMarked = marked;
	}	

	public String getFwdEnableFunc() {
		return fwdEnableFunc;
	}

	public void setFwdEnableFunc(String fwdEnableFunc) {
		this.fwdEnableFunc = fwdEnableFunc;
	}

	public String getBackEnableFunc() {
		return backEnableFunc;
	}

	public void setBackEnableFunc(String backEnableFunc) {
		this.backEnableFunc = backEnableFunc;
	}

	public String getAndMarkFunc() {
		return andMarkFunc;
	}

	public void setAndMarkFunc(String andMarkFunc) {
		this.andMarkFunc = andMarkFunc;
	}

	public String getAndUnmarkFunc() {
		return andUnmarkFunc;
	}

	public void setAndUnmarkFunc(String andUnmarkFunc) {
		this.andUnmarkFunc = andUnmarkFunc;
	}

	public String getBackDisableFunc() {
		return backDisableFunc;
	}

	public void setBackDisableFunc(String backDisableFunc) {
		this.backDisableFunc = backDisableFunc;
	}

	public String getFwdDisableFunc() {
		return fwdDisableFunc;
	}

	public void setFwdDisableFunc(String fwdDisableFunc) {
		this.fwdDisableFunc = fwdDisableFunc;
	}

	public String getOrMarkFunc() {
		return orMarkFunc;
	}

	public void setOrMarkFunc(String orMarkFunc) {
		this.orMarkFunc = orMarkFunc;
	}

	public String getOrUnmarkFunc() {
		return orUnmarkFunc;
	}

	public void setOrUnmarkFunc(String orUnmarkFunc) {
		this.orUnmarkFunc = orUnmarkFunc;
	}

	@Override
	public void restoreState(Object object) {
		marked = (Boolean)((Object[])object)[0];
		orMarked = (Boolean)((Object[])object)[1];
		andMarked = (Boolean)((Object[])object)[2];
		
		state = (RegisterState)((Object[])object)[3];
		forward_state = (RegisterState)((Object[])object)[4];
		backward_state = (RegisterState)((Object[])object)[5];
		
		or_waiting = false;
		and_waiting = false;
		can_work = false;
		waiting_for_user = false;		
	}

	@Override
	public Object saveState() {
		Object[] s = new Object[6];
		s[0] = marked;
		s[1] = orMarked;
		s[2] = andMarked;
		s[3] = state;
		s[4] = forward_state;
		s[5] = backward_state;
		return s;
	}
}
