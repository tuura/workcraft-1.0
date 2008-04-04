package workcraft.spreadtoken;

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
import workcraft.sdfs.SDFSModelBase;
import workcraft.sdfs.SDFSNode;
import workcraft.sdfs.SDFSRegisterBase;
import workcraft.util.Colorf;
import workcraft.util.Mat4x4;
import workcraft.util.Vec2;
import workcraft.visual.BlendMode;
import workcraft.visual.LineMode;
import workcraft.visual.Painter;
import workcraft.visual.ShapeMode;
import workcraft.visual.TextAlign;

public class STController extends SDFSRegisterBase   {
	public static final UUID _modeluuid = UUID.fromString("a57b3350-73d3-11db-9fe1-0800200c9a66");
	public static final UUID[] _modeluuidex = new UUID[] {UUID.fromString("aab78c50-e6bf-11dc-95ff-0800200c9a66")};
	public static final String _displayname = "Controller";

	private static Colorf selColor = new Colorf (0.0f, 0.75f, 0.0f, 1.0f);
	private static Colorf selColor2 = new Colorf (0.75f,  0.75f, 0.75f, 1.0f);
	private static Colorf nselColor = new Colorf (0.75f,  0.0f, 0.0f, 1.0f);
	private static Colorf nselColor2 = new Colorf (0.75f,  0.75f, 0.75f, 1.0f);
	private boolean sel = false;
	private boolean nsel = false;

	private RegisterState state = RegisterState.DISABLED;


	private boolean canWorkSel = false;
	private boolean canWorkNsel = false;

	private boolean waiting_for_user = false;

	private LinkedList<SDFSNode> out = new LinkedList<SDFSNode>();
	private LinkedList<SDFSNode> in = new LinkedList<SDFSNode>();

	private String selMarkFunc;// = "";
	private String selUnmarkFunc;

	private String nselMarkFunc;// = "";
	private String nselUnmarkFunc;

	private String enableFunc; //= "";
	private String disableFunc; //= "";


	public STController(BasicEditable parent) throws UnsupportedComponentException {
		super(parent);
		boundingBox.setExtents(new Vec2(-0.075f, -0.045f), new Vec2(0.075f, 0.045f) );
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



		p.setLineColor(frame_color);
		p.setLineMode(LineMode.SOLID);
		p.setLineWidth(0.005f);

		p.setShapeMode(ShapeMode.FILL_AND_OUTLINE);

		if (state == RegisterState.ENABLED)
			p.setFillColor(enabled_color);
		else
			p.setFillColor(disabled_color);

		p.drawRect(-0.075f, 0.045f, 0.075f, -0.045f);

		if (waiting_for_user) {
			p.blendEnable();
			p.setBlendConstantAlpha(0.5f);
			p.setBlendMode(BlendMode.CONSTANT_ALPHA);

			Vec2 pos = new Vec2 (0.0f, -0.015f);
			transform.getLocalToViewMatrix().transform(pos);

			if (state == RegisterState.DISABLED) {
				if (sel) {
					p.setTextColor(selColor2);
					p.drawString ("SEL", pos, 0.07f, TextAlign.CENTER);
				} else if (nsel) {
					p.setTextColor(nselColor2);
					p.drawString ("NSEL", pos, 0.07f, TextAlign.CENTER);
				} 
			} else {
				p.setTextColor(selColor);
				p.drawString ("(*)", pos, 0.07f, TextAlign.CENTER);  
			}
			p.blendDisable();
		} else {
			Vec2 pos = new Vec2 (0.0f, -0.015f);
			transform.getLocalToViewMatrix().transform(pos);

			if (sel) {
				p.setTextColor(selColor);
				p.drawString ("SEL", pos, 0.07f, TextAlign.CENTER);  

			} else if (nsel) {
				p.setTextColor(nselColor);
				p.drawString ("NSEL", pos, 0.07f, TextAlign.CENTER);  
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
		//list.add("bool,Marked,isMarked,setMarked");
		//list.add("str,Enabling,getEnableFunc,setEnableFunc");
		//list.add("str,Disabling,getDisableFunc,setDisableFunc");
//		list.add("str,Marking,getMarkFunc,setMarkFunc");
		//list.add("str,Unmarking,getUnmarkFunc,setUnmarkFunc");
		return list;
	}

	public void fromXmlDom(Element element) throws DuplicateIdException {
		NodeList nl = element.getElementsByTagName("st-controller");
		Element ne = (Element) nl.item(0);

		selMarkFunc = ne.getAttribute("sel-mark-func");
		selUnmarkFunc = ne.getAttribute("sel-unmark-func");
		nselMarkFunc = ne.getAttribute("nsel-mark-func");
		nselUnmarkFunc = ne.getAttribute("nsel-unmark-func");

		enableFunc = ne.getAttribute("enable-func");
		disableFunc = ne.getAttribute("disable-func");
		super.fromXmlDom(element);		
	}

	public Element toXmlDom(Element parent_element) {
		Element ee = super.toXmlDom(parent_element);
		org.w3c.dom.Document d = ee.getOwnerDocument();
		Element ppe = d.createElement("st-controller");
		ppe.setAttribute("sel-mark-func", selMarkFunc);
		ppe.setAttribute("sel-unmark-func", selUnmarkFunc);
		ppe.setAttribute("nsel-mark-func", selMarkFunc);
		ppe.setAttribute("nsel-unmark-func", selUnmarkFunc);		
		ppe.setAttribute("enable-func", enableFunc);
		ppe.setAttribute("disable-func", disableFunc);
		ee.appendChild(ppe);
		return ee;
	}

	public boolean simTick(int time_ms) {
		WorkCraftServer server = ownerDocument.getServer();
		SDFSModelBase doc = (SDFSModelBase)ownerDocument;

		switch (state) {
		case ENABLED:
			if (!isSel() && !isNsel()) {
				if (server.python.eval(selMarkFunc).__nonzero__()) {
					if ( doc.isUserInteractionEnabled())
					{
						if (canWorkSel) {
							setSel (true);
							doc.addTraceEvent(this.id + "/sel");
							waiting_for_user = false;
							canWorkNsel = false;
							canWorkSel = false;
						}	else {
							waiting_for_user = true;
						}
					}
					else	{
						setSel(true);
					}
				}

				if (server.python.eval(nselMarkFunc).__nonzero__()) {
					if ( doc.isUserInteractionEnabled())
					{
						if (canWorkNsel) {
							setNsel (true);
							doc.addTraceEvent(this.id + "/nsel");
							waiting_for_user = false;
							canWorkNsel = false;
							canWorkSel = false;
						}	else {
							waiting_for_user = true;
						}
					}
					else	{
						setNsel(true);
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
			if (isSel()) {
				boolean can_unmark = server.python.eval(selUnmarkFunc).__nonzero__();
				if (can_unmark) {
					if ( doc.isUserInteractionEnabled() ) {
						if (canWorkSel || canWorkNsel) {
							setSel(false);							
							canWorkSel = false;
							canWorkNsel = false;
							waiting_for_user = false;
							doc.addTraceEvent(this.id + "/unmarked");
						} else {
							waiting_for_user = true;
						}
					}
					else {
						setSel(false);							
						doc.addTraceEvent(this.id + "/unmarked");
					}
				}
			}

			if (isNsel()) {
				boolean can_unmark = server.python.eval(nselUnmarkFunc).__nonzero__();
				if (can_unmark) {
					if ( doc.isUserInteractionEnabled() ) {
						if (canWorkSel || canWorkNsel) {
							setNsel(false);							
							canWorkSel = false;
							canWorkNsel = false;
							waiting_for_user = false;
							doc.addTraceEvent(this.id + "/unmarked");
						} else {
							waiting_for_user = true;
						}
					}
					else {
						setNsel(false);							
						doc.addTraceEvent(this.id + "/unmarked");
					}
				}
			}

			if (!isSel() && !isNsel()) {
				if ( server.python.eval(enableFunc).__nonzero__()) {
					state = RegisterState.ENABLED;
					doc.addTraceEvent(this.id + "/enabled");
				}
			}
			break;				
		}

		return true;
	}

	@Override
	public void rebuildRuleFunctions() {
		selMarkFunc = expandRule("self re,r-preset cs,r-postset !cs,r-postset !cn");
		selUnmarkFunc = expandRule("self !re,r-preset !cs,r-preset !cn,r-postset cs|r-postset cn");

		nselMarkFunc = expandRule("self re,r-preset cn,r-postset !cs,r-postset !cn");
		nselUnmarkFunc = expandRule("self !re,r-preset !cs,r-preset !cn,r-postset cs|r-postset cn");

		enableFunc = expandRule("self !cs,self !cn,preset:l e,preset:r cs|preset:r cn");
		disableFunc = expandRule("self cs|self cn,preset:l r,preset:r !cs,preset:r !cn");
	}

	public void simAction(int flag) {
		if (flag == MouseEvent.BUTTON1) {
			canWorkSel = true;
		} else if (flag == MouseEvent.BUTTON3) {
			canWorkNsel = true;
		}
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

	@Override
	public void restoreState(Object object) {
		sel = (Boolean)((Object[])object)[0];
		nsel = (Boolean)((Object[])object)[1];
		state = (RegisterState)((Object[])object)[2];

		canWorkSel = false;
		canWorkNsel = false;
		waiting_for_user = false;		
	}

	@Override
	public Object saveState() {
		Object[] s = new Object[3];
		s[0] = sel;
		s[1] = nsel;
		s[2] = state;
		return s;
	}

	public boolean isNsel() {
		return nsel;
	}

	public void setNsel(boolean nsel) {
		this.nsel = nsel;
	}

	public boolean isSel() {
		return sel;
	}

	public void setSel(boolean sel) {
		this.sel = sel;
	}

	public String getSelMarkFunc() {
		return selMarkFunc;
	}

	public void setSelMarkFunc(String selMarkFunc) {
		this.selMarkFunc = selMarkFunc;
	}

	public String getSelUnmarkFunc() {
		return selUnmarkFunc;
	}

	public void setSelUnmarkFunc(String selUnmarkFunc) {
		this.selUnmarkFunc = selUnmarkFunc;
	}
}
