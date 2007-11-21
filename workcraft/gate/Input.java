package workcraft.gate;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.UUID;

import workcraft.Model;
import workcraft.UnsupportedComponentException;
import workcraft.editor.BasicEditable;
import workcraft.util.Colorf;
import workcraft.util.Vec2;
import workcraft.visual.LineMode;
import workcraft.visual.Painter;
import workcraft.visual.ShapeMode;

public class Input extends BasicGate {
	public static final UUID _modeluuid = UUID.fromString("6f704a28-e691-11db-8314-0800200c9a66");
	public static final String _displayname = "Input";
	
	private static Colorf setFillColor = new Colorf(0.0f, 1.0f, 0.0f, 1.0f);
	
	private GateContact.StateType state = GateContact.StateType.reset;

	public Input(BasicEditable parent) throws UnsupportedComponentException {
		super(parent);
		rad = 0.015f;
		min_inputs = 0;
		max_inputs = 0;
		min_outputs = 1;
		max_outputs = 1;
		initContacts();
	}
	
	public void doDraw(Painter p) {
		super.doDraw(p);
		p.setTransform(transform.getLocalToViewMatrix());
		p.setShapeMode(ShapeMode.FILL_AND_OUTLINE);
		p.setLineMode(LineMode.SOLID);
		p.setLineWidth(0.005f);
		p.setLineColor((selected)?selectedOutlineColor:outlineColor);
		p.setFillColor((state==GateContact.StateType.set)?setFillColor:(selected)?selectedFillColor:fillColor);
		p.drawRect(-rad, rad, rad, -rad);
	}
	
	protected void updateContactOffsets() {
		boundingBox.setExtents(new Vec2(-0.02f, -0.02f), new Vec2(0.02f, 0.02f));
		for(GateContact c : out) {
			Vec2 offs = new Vec2(0.0f, 0.0f);
			c.setOffs(offs);
		}
	}
	
	@Override
	public BasicEditable getChildAt(Vec2 point) {
		return null;
	}

	public List<String> getEditableProperties() {
		List<String> list = super.getEditableProperties();
		list.add("enum,Signal,getState,setState,reset,set");
		return list;
	}
	
	public Integer getState() {
		return state.ordinal();
	}
	
	public void setState(Integer state) {
		this.state = GateContact.StateType.values()[state];
	}
	
	public void refresh() {
		out.getFirst().setState(state);
		super.refresh();
		fire();
	}
	
	public void switchState() {
		state = (state==GateContact.StateType.reset)?
				GateContact.StateType.set:GateContact.StateType.reset;
	}
	
	public void simAction(int flag) {
		if (flag == MouseEvent.BUTTON1) {
			switchState();
			((GateModel)getOwnerDocument()).addTraceEvent(getId());
		}
	}
	
	public Boolean isSet() {
		return (out.getFirst().getState()==GateContact.StateType.set);
	}
	
	public String getSetFunction() {
		return "ALWAYS";
	}

	public String getResetFunction() {
		return "ALWAYS";
	}
}
