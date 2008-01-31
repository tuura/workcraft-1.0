package workcraft.cpog;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import workcraft.DuplicateIdException;
import workcraft.ModelBase;
import workcraft.UnsupportedComponentException;
import workcraft.XmlSerializable;
import workcraft.editor.BasicEditable;
import workcraft.util.Colorf;
import workcraft.util.Mat4x4;
import workcraft.util.Vec2;
import workcraft.visual.LineMode;
import workcraft.visual.Painter;
import workcraft.visual.ShapeMode;
import workcraft.visual.TextAlign;

public class ControlVariable extends BasicEditable implements XmlSerializable
{
	public static final UUID _modeluuid = UUID.fromString("25787b48-9c3d-11dc-8314-0800200c9a66");
	public static final String _displayname = "Control Variable";
	public static final String _hotkey = " x ";
	public static final int _hotkeyvk = KeyEvent.VK_X;		

	private static final Colorf frameColor = new Colorf (0.0f, 0.0f, 0.0f, 1.0f);
	private static final Colorf extFillColor = new Colorf (0.9f, 0.9f, 1.0f, 1.0f);
	private static final Colorf intFillColor = new Colorf (0.9f, 1.0f, 1.0f, 1.0f);
	private static final Colorf selectedColor = new Colorf (0.5f, 0.0f, 0.0f, 1.0f);
	private static final Colorf intColor = new Colorf (0.0f, 0.0f, 0.5f, 1.0f);

	public ControlVariable(BasicEditable parent) throws UnsupportedComponentException
	{
		super(parent);
		boundingBox.setExtents(new Vec2(-0.1f, -0.05f), new Vec2(0.1f, 0.05f));
	}

	@Override
	public void doDraw(Painter p) {
		p.setTransform(transform.getLocalToViewMatrix());

		p.setShapeMode(ShapeMode.FILL_AND_OUTLINE);
		
		boolean internal = true;
		
		p.setLineMode(LineMode.SOLID);
		p.setLineWidth(0.01f);
		
		if (internal)
		{
			p.setFillColor(intFillColor);
			p.setTextColor(intColor);
		}
		else
		{
			p.setFillColor(extFillColor);
			p.setTextColor(frameColor);
		}

		if (selected)
			p.setLineColor(selectedColor);
		else
			if (internal)
				p.setLineColor(intColor);
			else
				p.setLineColor(frameColor);

		p.drawRect(-0.0975f, 0.0475f, 0.0975f, -0.0475f);
		Vec2 v0 = new Vec2(0.0f, -0.025f);
		transform.getLocalToViewMatrix().transform(v0);		
		
		p.drawString("X", v0, 0.08f, TextAlign.CENTER);

		super.doDraw(p);
	}

	public List<String> getEditableProperties() {
		List<String> list = super.getEditableProperties();
		return list;
	}

	@Override
	public BasicEditable getChildAt(Vec2 point) {
		return null;
	}


	@Override
	public void update(Mat4x4 matView) {
		// TODO Auto-generated method stub

	}
	public void fromXmlDom(Element element) throws DuplicateIdException {
		super.fromXmlDom(element);
	}

	public Element toXmlDom(Element parent_element) {
		Element ee = super.toXmlDom(parent_element);
		return ee;
	}

}
