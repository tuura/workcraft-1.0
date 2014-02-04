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

public abstract class DynamicElementBase extends SDFSRegisterBase   {
	//public static final UUID _modeluuid = UUID.fromString("a57b3350-73d3-11db-9fe1-0800200c9a66");
	//public static final UUID[] _modeluuidex = new UUID[] {UUID.fromString("aab78c50-e6bf-11dc-95ff-0800200c9a66")};
	///public static final String _displayname = "Spreadtoken Register";

	private static Colorf token_color = new Colorf (0.0f, 0.0f, 0.0f, 1.0f);
	private boolean tokenLeft;
	private boolean tokenRight;
	private boolean sel;
	private boolean nsel;
	private RegisterState leftState = RegisterState.DISABLED;
	private RegisterState rightState = RegisterState.DISABLED;
	private RegisterState controlState = RegisterState.DISABLED;
	private boolean canWorkData = false;
	private boolean canWorkControl = false;
	private boolean waiting_for_user = false;
	
	private static Colorf selColor = new Colorf (0.0f, 0.75f, 0.0f, 1.0f);
	private static Colorf selColor2 = new Colorf (0.75f,  0.75f, 0.75f, 1.0f);
	private static Colorf nselColor = new Colorf (0.75f,  0.0f, 0.0f, 1.0f);
	private static Colorf nselColor2 = new Colorf (0.75f,  0.75f, 0.75f, 1.0f);


	private LinkedList<SDFSNode> out = new LinkedList<SDFSNode>();
	private LinkedList<SDFSNode> in = new LinkedList<SDFSNode>();

	private String leftMarkFunc;
	private String leftUnmarkFunc;
	private String leftEnableFunc;
	private String leftDisableFunc;

	private String rightMarkFunc;
	private String rightUnmarkFunc;
	private String rightEnableFunc;
	private String rightDisableFunc;

	private String selMarkFunc;
	private String selUnmarkFunc;
	private String nselMarkFunc;
	private String nselUnmarkFunc;
	private String ctlEnableFunc;
	private String ctlDisableFunc;


	public DynamicElementBase(BasicEditable parent) throws UnsupportedComponentException {
		super(parent);
		boundingBox.setExtents(new Vec2(-0.075f, -0.05f), new Vec2(0.075f, 0.05f) );
		tokenLeft = false;
		tokenRight = false;
		sel = false;
		nsel = false;
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
		p.setLineColor(frame_color);
		p.setLineMode(LineMode.SOLID);
		p.setLineWidth(0.005f);

		p.setFillColor(disabled_color);
		p.drawRect(-0.075f, 0.090f, 0.075f, -0.045f);
		
		if (controlState == RegisterState.ENABLED)
			p.setFillColor(enabled_color);
		else
			p.setFillColor(disabled_color);
		p.drawRect(-0.075f, 0.090f, 0.075f, 0.045f);
		
		if (leftState == RegisterState.ENABLED)
			p.setFillColor(enabled_color);
		else
			p.setFillColor(disabled_color);
		p.drawRect(-0.055f, 0.045f, 0.0f, -0.045f);
		
		if (rightState == RegisterState.ENABLED)
			p.setFillColor(enabled_color);
		else
			p.setFillColor(disabled_color);
		p.drawRect(0.0f, 0.045f, 0.055f, -0.045f);

		
		if (waiting_for_user) {
			p.blendEnable();
			p.setBlendConstantAlpha(0.5f);
			p.setBlendMode(BlendMode.CONSTANT_ALPHA);

			if (leftState == RegisterState.DISABLED) {
				p.setFillColor(token_color);
				p.setShapeMode(ShapeMode.FILL);
				p.drawCircle(0.015f, -0.0275f, 0.0f);
			} else {
				p.setFillColor(enabled_color);
				p.setLineColor(token_color);
				p.setShapeMode(ShapeMode.FILL_AND_OUTLINE);
				p.setLineMode(LineMode.SOLID);
				p.setLineWidth(0.0025f);
				p.drawCircle(0.015f, -0.0275f, 0.0f);
			}
			
			if (rightState == RegisterState.DISABLED) {
				p.setFillColor(token_color);
				p.setShapeMode(ShapeMode.FILL);
				p.drawCircle(0.015f, 0.0275f, 0.0f);
			} else {
				p.setFillColor(enabled_color);
				p.setLineColor(token_color);
				p.setShapeMode(ShapeMode.FILL_AND_OUTLINE);
				p.setLineMode(LineMode.SOLID);
				p.setLineWidth(0.0025f);
				p.drawCircle(0.015f, 0.0275f, 0.0f);
			}		
			
			p.blendDisable();

			Vec2 pos = new Vec2 (0.0f, 0.05f);
			transform.getLocalToViewMatrix().transform(pos);

			if (controlState == RegisterState.DISABLED) {
				if (sel) {
					p.setTextColor(selColor2);
					p.drawString ("SEL", pos, 0.06f, TextAlign.CENTER);
				} else if (nsel) {
					p.setTextColor(nselColor2);
					p.drawString ("NSEL", pos, 0.06f, TextAlign.CENTER);
				} 				
			}
		} else {
			Vec2 pos = new Vec2 (0.0f, 0.05f);
			transform.getLocalToViewMatrix().transform(pos);
			
			if (tokenLeft) {
				p.setFillColor(token_color);
				p.setShapeMode(ShapeMode.FILL);
				p.drawCircle(0.015f, -0.0275f, 0.0f);
			} 	
			
			if (tokenRight) {
				p.setFillColor(token_color);
				p.setShapeMode(ShapeMode.FILL);
				p.drawCircle(0.015f, 0.0275f, 0.0f);
			}
			
			if (sel) {
				p.setFillColor(token_color);
				p.setTextColor(selColor);
				p.drawString ("SEL", pos, 0.06f, TextAlign.CENTER);
			}
			
			if (nsel) {
				p.setFillColor(token_color);
				p.setTextColor(selColor);
				p.drawString ("NSEL", pos, 0.06f, TextAlign.CENTER);
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
		/*list.add("bool,Marked,isMarked,setMarked");
		list.add("str,Enabling,getEnableFunc,setEnableFunc");
		list.add("str,Disabling,getDisableFunc,setDisableFunc");
		list.add("str,Marking,getMarkFunc,setMarkFunc");
		list.add("str,Unmarking,getUnmarkFunc,setUnmarkFunc");*/
		return list;
	}

	public void fromXmlDom(Element element) throws DuplicateIdException {
		NodeList nl = element.getElementsByTagName("st-register");
		Element ne = (Element) nl.item(0);
/*		setMarked(Boolean.parseBoolean(ne.getAttribute("marked")));
		markFunc = ne.getAttribute("mark-func");
		unmarkFunc = ne.getAttribute("unmark-func");
		enableFunc = ne.getAttribute("enable-func");
		disableFunc = ne.getAttribute("disable-func");*/
		super.fromXmlDom(element);		
	}

	public Element toXmlDom(Element parent_element) {
		Element ee = super.toXmlDom(parent_element);
		org.w3c.dom.Document d = ee.getOwnerDocument();
		Element ppe = d.createElement("st-register");
		/*ppe.setAttribute("marked", Boolean.toString(isMarked()));
		ppe.setAttribute("mark-func", markFunc);
		ppe.setAttribute("unmark-func", unmarkFunc);		
		ppe.setAttribute("enable-func", enableFunc);
		ppe.setAttribute("disable-func", disableFunc);*/
		ee.appendChild(ppe);
		return ee;
	}

	public boolean simTick(int time_ms) {
		WorkCraftServer server = ownerDocument.getServer();
		SDFSModelBase doc = (SDFSModelBase)ownerDocument;
		/*
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
		 */
		return true;
	}

	@Override
	public void rebuildRuleFunctions() {
		/*	markFunc = expandRule("self re,r-preset m,r-postset !m");
		unmarkFunc = expandRule("self !re,r-preset !m,r-postset m");
		enableFunc = expandRule("self !m,preset:l e,preset:r m");
		disableFunc = expandRule("self m,preset:l r,preset:r !m");*/
	}

	public void simAction(int flag) {
		if (flag == MouseEvent.BUTTON1) {
			can_work = !can_work;
		}
	}



	public boolean canWork() {
		return can_work;
	}



	@Override
	public void restoreState(Object object) {
		/*	marked = (Boolean)((Object[])object)[0];
		state = (RegisterState)((Object[])object)[1];

		can_work = false;
		waiting_for_user = false;		*/
	}

	@Override
	public Object saveState() {
		Object[] s = new Object[2];
		/*s[0] = marked;
		s[1] = state;*/
		return s;
	}
}
