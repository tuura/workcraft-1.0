package workcraft.cpog;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import workcraft.common.DefaultConnection;
import workcraft.editor.BasicEditable;
import workcraft.util.Colorf;
import workcraft.util.Vec2;
import workcraft.visual.Painter;
import workcraft.visual.TextAlign;

public class Arc extends DefaultConnection
{
	private static Colorf connectionColor = new Colorf (0.0f, 0.0f, 0.0f, 1.0f);
	private static Colorf passiveConnectionColor = new Colorf(0.6f, 0.6f, 0.6f, 1.0f);
	
	private static Colorf conditionColor = new Colorf (0.7f, 0.1f, 0.0f, 1.0f);
	
	private String condition;
	
	private Boolean active = true;
	
	
	public Boolean isActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
		if (active)
		{
			colorOverride = connectionColor;
		}
		else
		{
			colorOverride = passiveConnectionColor;
		}
	}

	public Arc()
	{
		this(null, null);
	}

	public Arc(BasicEditable first, BasicEditable second)
	{
		super(first, second);
		colorOverride = connectionColor;
		setCondition("1");
		setActive(true);
	}

	public String getCondition()
	{
		return condition;
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
	}
	
	public List<String> getEditableProperties()
	{
		List<String> list = super.getEditableProperties();

		list.add("str,Condition,getCondition,setCondition");
		list.add("bool,Active,isActive,setActive");

		return list;
	}
	
	public void draw(Painter p)
	{
		super.draw(p);
		
		if (!condition.equals("1"))
		{
			updateStretch();
			p.pushTransform();
			p.setIdentityTransform();
	
			Vec2 v = getPointOnConnection(0.5f);
			
			v.setY(v.getY() + 0.03f);		
	
			p.setTextColor(conditionColor);
			p.drawString(condition, v, 0.05f, TextAlign.CENTER);
			
			p.popTransform();
		}
	}
	
	public Element toXmlDom(Element parent_element)
	{
		Element ee = super.toXmlDom(parent_element);
		Document d = ee.getOwnerDocument();
		Element ppe = d.createElement("condition");
		ppe.setAttribute("text", getCondition());
		ee.appendChild(ppe);
		return ee;
	}

	public void fromXmlDom(Element element)
	{
		NodeList nl = element.getElementsByTagName("condition");
		Element ne = (Element) nl.item(0);
		setCondition(ne.getAttribute("text"));
		super.fromXmlDom(element);
	}	
}
