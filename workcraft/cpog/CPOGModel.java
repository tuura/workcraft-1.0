package workcraft.cpog;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.JPanel;

import workcraft.DuplicateIdException;
import workcraft.InvalidConnectionException;
import workcraft.Model;
import workcraft.ModelBase;
import workcraft.UnsupportedComponentException;
import workcraft.WorkCraftServer;
import workcraft.common.DefaultConnection;
import workcraft.common.DefaultSimControls;
import workcraft.common.LabelledConnection;
import workcraft.editor.BasicEditable;
import workcraft.editor.EditableConnection;
import workcraft.editor.EditorPane;
import workcraft.graph.GraphModel.SimThread;

public class CPOGModel extends ModelBase
{
	public static final UUID _modeluuid = UUID.fromString("25787b48-9c3d-11dc-8314-0800200c9a66");
	public static final String _displayname = "Conditional Partial Order Graph";

	public class SimThread extends Thread
	{
		public void run()
		{
			while (true)
			{
				try
				{
					sleep( (long)(100 / panelSimControls.getSpeedFactor()));
					simStep();
					server.execPython("_redraw()");
				} catch (InterruptedException e) { break; }
			}
		}
	}

	SimThread sim_thread = null;
	public static DefaultSimControls panelSimControls = null;

	int v_name_cnt = 0;
	int x_name_cnt = 0;
	
	private boolean loading;

	LinkedList<Vertex> vertices;
	LinkedList<ControlVariable> variables;
	LinkedList<DefaultConnection> connections;

	public CPOGModel()
	{
		vertices = new LinkedList<Vertex>();
		connections = new LinkedList<DefaultConnection>();
		variables = new LinkedList<ControlVariable>();
	}

	public String getNextVertexID()
	{
		return "v"+v_name_cnt++;
	}

	public String getNextCVID()
	{
		return "x"+x_name_cnt++;
	}

	public void addComponent(BasicEditable c, boolean auto_name) throws UnsupportedComponentException
	{
		if (c instanceof Vertex) {
			Vertex p = (Vertex)c;
			vertices.add(p);
			p.setOwnerDocument(this);
			if (auto_name)
				for (;;)
				{
					try
					{
						p.setId(getNextVertexID());
						break;
					} catch (DuplicateIdException e) {}
				}
		}
		else 
		if (c instanceof ControlVariable) {
			ControlVariable p = (ControlVariable)c;
			variables.add(p);
			p.setOwnerDocument(this);
			if (auto_name)
				for (;;)
				{
					try
					{
						p.setId(getNextCVID());
						break;
					} catch (DuplicateIdException e) {}
				}
		} else throw new UnsupportedComponentException();
		
		super.addComponent(c, auto_name);
	}

	public void removeComponent(BasicEditable c) throws UnsupportedComponentException
	{
		super.removeComponent(c);
		if (c instanceof Vertex)
		{
			Vertex v = (Vertex)c;
			for (Vertex t : v.getIn()) t.removeOut(v);				
			for (Vertex t : v.getOut())	t.removeIn(v);
			for (ControlVariable t : v.getVars()) t.setControlVertex(null);

			vertices.remove(c);
		}
		else
		if (c instanceof ControlVariable)
		{
			ControlVariable v = (ControlVariable)c;

			if (v.getControlVertex() != null) v.getControlVertex().removeVar(v);
			
			variables.remove(c);
		} else throw new UnsupportedComponentException();
				
	}

	public EditableConnection createConnection(BasicEditable first, BasicEditable second) throws InvalidConnectionException
	{
		if (first instanceof Vertex && second instanceof Vertex)
		{
			Vertex p, q;
	
			p = (Vertex)first;
			q = (Vertex)second;
			
			Arc con = new Arc(p, q);
			if (p.addOut(con) && q.addIn(con))
			{
				connections.add(con);
				return con;
			}
			return null;
		}
		else
		if (first instanceof Vertex && second instanceof ControlVariable)
		{
			Vertex p;
			ControlVariable q;
	
			p = (Vertex)first;
			q = (ControlVariable)second;
			
			if (q.getControlVertex() != null) throw new InvalidConnectionException ("Variable can have at most one control vertex.");
			
			DefaultConnection con = new DefaultConnection(p, q);			
			con.drawArrow = false;
			
			if (p.addVar(con) && q.addVertex(con))
			{
				connections.add(con);
				q.setControlVertex(p);
				return con;
			}
			return null;
		}
		else
		if (second instanceof Vertex && first instanceof ControlVariable)
		{
			Vertex p;
			ControlVariable q;
	
			p = (Vertex)second;
			q = (ControlVariable)first;
			
			if (q.getControlVertex() != null) throw new InvalidConnectionException ("Variable can have at most one control vertex.");
			
			DefaultConnection con = new DefaultConnection(p, q);			
			con.drawArrow = false;
			
			if (p.addVar(con) && q.addVertex(con))
			{
				connections.add(con);
				q.setControlVertex(p);
				return con;
			}
			return null;
		}
		else
		throw new InvalidConnectionException ("Invalid connection.");

	}

	public void removeConnection(EditableConnection con) throws UnsupportedComponentException
	{
		if (con.getSecond() instanceof ControlVariable)
		{
			Vertex p = (Vertex)con.getFirst();
			ControlVariable q = (ControlVariable)con.getSecond();
			p.removeVar(q);
			q.setControlVertex(null);
			connections.remove(con);
			return;
		}
		
		Vertex p,q;
	
		p = (Vertex)con.getFirst();
		q = (Vertex)con.getSecond();
		
		p.removeOut(q);
		q.removeIn(p);
		connections.remove(con);
	}

	public void simReset()
	{
	}

	public void simBegin()
	{
		if (sim_thread==null) {
			simReset();
			sim_thread = new SimThread();
			sim_thread.start();
		}
	}

	public void simStep()
	{	
	}

	public boolean simIsRunning()
	{
		return (sim_thread != null);
	}

	public void simFinish()
	{
		if (sim_thread!=null)
		{
			sim_thread.interrupt();
			sim_thread = null;
		}
	}

	public List<EditableConnection> getConnections()
	{
		return (List<EditableConnection>)((List)connections);		
	}

	public void validate()
	{
	}

	public JPanel getSimulationControls()
	{
		if (panelSimControls == null) {
			panelSimControls = new DefaultSimControls(_modeluuid.toString());
		}
		return panelSimControls;
	}

	public void bind(WorkCraftServer server) {
		this.server = server;
		server.python.set("_document", this);
	}

	public EditorPane getEditor() {
		return editor;
	}

	public void setEditor(EditorPane editor) {
		this.editor = editor;
	}

	public WorkCraftServer getServer() {
		return server;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	public boolean isLoading() {
		return loading;
	}

	public void getComponents(List<BasicEditable> out) {
		for (BasicEditable n: vertices)
			out.add(n);
	}
}