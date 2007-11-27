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

	public class SimThread extends Thread {
		public void run() {
			while (true) {
				try {
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
	
	private boolean loading;

	LinkedList<EditableCPOGVertex> vertices;
	LinkedList<LabelledConnection> connections;

	public CPOGModel()
	{
		vertices = new LinkedList<EditableCPOGVertex>();
		connections = new LinkedList<LabelledConnection>();
	}

	public String getNextVertexID()
	{
		return "v"+v_name_cnt++;
	}

	public void addComponent(BasicEditable c, boolean auto_name) throws UnsupportedComponentException
	{
		if (c instanceof EditableCPOGVertex) {
			EditableCPOGVertex p = (EditableCPOGVertex)c;
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
		} else throw new UnsupportedComponentException();
		
		super.addComponent(c, auto_name);
	}

	public void removeComponent(BasicEditable c) throws UnsupportedComponentException
	{
		super.removeComponent(c);
		if (c instanceof EditableCPOGVertex)
		{
			EditableCPOGVertex v = (EditableCPOGVertex)c;
			for (EditableCPOGVertex t : v.getIn()) t.removeOut(v);				
			for (EditableCPOGVertex t : v.getOut())	t.removeIn(v);

			vertices.remove(c);
		} else throw new UnsupportedComponentException();
		
		super.removeComponent(c);
	}

	public EditableConnection createConnection(BasicEditable first, BasicEditable second) throws InvalidConnectionException
	{
		if (!(first instanceof EditableCPOGVertex) || !(second instanceof EditableCPOGVertex))
			throw new InvalidConnectionException ("Invalid connection.");
		
		EditableCPOGVertex p, q;

		p = (EditableCPOGVertex)first;
		q = (EditableCPOGVertex)second;
		LabelledConnection con = new LabelledConnection(p, q);
		if (p.addOut(con) && q.addIn(con))
		{
			connections.add(con);
			return con;
		}
		return null;
	}

	public void removeConnection(EditableConnection con) throws UnsupportedComponentException
	{
		EditableCPOGVertex p,q;
	
		p = (EditableCPOGVertex)con.getFirst();
		q = (EditableCPOGVertex)con.getSecond();
		
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