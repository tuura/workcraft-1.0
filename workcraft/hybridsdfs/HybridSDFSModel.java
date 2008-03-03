package workcraft.hybridsdfs;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.JPanel;

import workcraft.DuplicateIdException;
import workcraft.DocumentBase;
import workcraft.ModelValidationException;
import workcraft.InvalidConnectionException;
import workcraft.Document;
import workcraft.UnsupportedComponentException;
import workcraft.WorkCraftServer;
import workcraft.common.DefaultConnection;
import workcraft.common.DefaultSimControls;
import workcraft.counterflow.CFLogic;
import workcraft.counterflow.CFRegister;
import workcraft.editor.BasicEditable;
import workcraft.editor.EditableConnection;
import workcraft.editor.EditorPane;
import workcraft.sdfs.SDFSLogicBase;
import workcraft.sdfs.SDFSModelBase;
import workcraft.sdfs.SDFSNode;
import workcraft.sdfs.SDFSRegisterBase;
import workcraft.spreadtoken.STLogic;
import workcraft.spreadtoken.STRegister;

public class HybridSDFSModel extends SDFSModelBase {
	public static final UUID _modeluuid = UUID.fromString("aab78c50-e6bf-11dc-95ff-0800200c9a66");
	public static final String _displayname = "SDFS (Hybrid)";

	public static final String py_functions = 
		"def e(o):\n" +
		"\treturn o.isEvaluated();\n" +
		"\n" +
		"def r(o):\n" +
		"\treturn o.isReset();\n" +
		"\n" +
		"def m(o):\n" +
		"\treturn o.isMarked();\n" +
		"\n" +
		"def re(o):\n" +
		"\treturn o.isEnabled();\n" +
		"\n"+
		"def lfe(o):\n" +
		"\treturn o.isForwardEvaluated();\n" +
		"\n"+
		"def lbe(o):\n" +
		"\treturn o.isBackwardEvaluated();\n" +
		"\n" +
		"def lfr(o):\n" +
		"\treturn o.isForwardReset();\n" +
		"\n"+
		"def lbr(o):\n" +
		"\treturn o.isBackwardReset();\n" +
		"\n"+
		"def om(o):\n" +
		"\treturn o.isOrMarked();\n" +
		"\n"+
		"def am(o):\n" +
		"\treturn o.isAndMarked();\n" +
		"\n"+
		"def rfe(o):\n" +
		"\treturn o.isForwardEnabled();\n" +
		"\n"+
		"def rbe(o):\n" +
		"\treturn o.isBackwardEnabled();\n" +
		"\n";


	int t_name_cnt = 0;
	int p_name_cnt = 0;

	private boolean loading = false;

	LinkedList<DefaultConnection> connections;


	public HybridSDFSModel() {
		registers = new LinkedList<SDFSRegisterBase>();
		logic = new LinkedList<SDFSLogicBase>();
		connections = new LinkedList<DefaultConnection>();
	}

	public String getNextTransitionID() {
		return "l"+t_name_cnt++;
	}

	public String getNextPlaceID() {
		return "r"+p_name_cnt++;
	}

	public void addComponent(BasicEditable c, boolean auto_name) throws UnsupportedComponentException {
		if (c instanceof STRegister || c instanceof CFRegister || c instanceof ST2CFRegister || c instanceof CF2STRegister) {
			SDFSRegisterBase p = (SDFSRegisterBase)c;
			p.setOwnerDocument(this);
			registers.add(p);
			if (auto_name)
				for (;;) {
					try {
						p.setId(getNextPlaceID());
						break;
					} catch (DuplicateIdException e) {
					}
				}
		}
		else if (c instanceof STLogic || c instanceof CFLogic) {
			SDFSLogicBase t = (SDFSLogicBase)c;
			t.setOwnerDocument(this);
			logic.add(t);
			if (auto_name)
				for (;;) {
					try {
						t.setId(getNextTransitionID());
						break;
					} catch (DuplicateIdException e) {
					}
				}			
		}	else throw new UnsupportedComponentException();

		super.addComponent(c, auto_name);
	}

	public void removeComponent(BasicEditable c) throws UnsupportedComponentException {
		super.removeComponent(c);
		if (c instanceof STRegister || c instanceof CFRegister || c instanceof CF2STRegister || c instanceof ST2CFRegister) {
			SDFSRegisterBase p = (SDFSRegisterBase)c;
			registers.remove(p);
		}
		else if (c instanceof STLogic || c instanceof CFLogic) {
			SDFSLogicBase t = (SDFSLogicBase)c;
			logic.remove(t);
		}	else throw new UnsupportedComponentException();	

		super.removeComponent(c);
	}

	public EditableConnection createConnection(BasicEditable first, BasicEditable second) throws InvalidConnectionException {
		if (first == second)
			throw new InvalidConnectionException ("Can't connect to self!");
		/*if (
				(first instanceof EditableSTRegister && second instanceof EditableSTRegister)
		) throw new InvalidConnectionException ("Invalid connection (direct place-to-place connections not allowed)");*/
		STRegister p,pp;
		STLogic t, tt;
		ST2CFRegister k;
		CF2STRegister l;
		CFRegister q,qq;
		CFLogic s, ss;

		if (first instanceof STRegister) {
			if (second instanceof STLogic) {
				p = (STRegister)first;
				t = (STLogic)second;
				DefaultConnection con = new DefaultConnection(p, t);
				if (p.addOut(con) && t.addIn(con)) {
					connections.add(con);
					return con;
				}
			} else if (second instanceof STRegister){
				p = (STRegister)first;
				pp = (STRegister)second;
				DefaultConnection con = new DefaultConnection(p, pp);
				if (p.addOut(con) && pp.addIn(con)) {
					connections.add(con);
					return con;
				}
			} else if (second instanceof ST2CFRegister){
				p = (STRegister)first;
				k = (ST2CFRegister)second;
				DefaultConnection con = new DefaultConnection(p, k);
				if (p.addOut(con) && k.addIn(con)) {
					connections.add(con);
					return con;
				}
			}

		} 
		else if (first instanceof STLogic){
			if (second instanceof STRegister) {
				p = (STRegister)second;
				t = (STLogic)first;
				DefaultConnection con = new DefaultConnection(t, p);
				if (p.addIn(con) && t.addOut(con)) {
					connections.add(con);
					return con;
				}
			} else if (second instanceof STLogic ) {
				t = (STLogic)first;
				tt = (STLogic)second;
				DefaultConnection conn = new DefaultConnection(t, tt);
				if (tt.addIn(conn) && t.addOut(conn)) {
					connections.add(conn);
					return conn;
				}
			} else if (second instanceof ST2CFRegister){
				t = (STLogic)first;
				k = (ST2CFRegister)second;
				DefaultConnection con = new DefaultConnection(t, k);
				if (t.addOut(con) && k.addIn(con)) {
					connections.add(con);
					return con;
				}
			}
		} else if (first instanceof CFRegister) {
			if (second instanceof CFLogic) {
				q = (CFRegister)first;
				s = (CFLogic)second;
				DefaultConnection con = new DefaultConnection(q, s);
				if (q.addOut(con) && s.addIn(con)) {
					connections.add(con);
					return con;
				}
			} else if (second instanceof CFRegister){
				q = (CFRegister)first;
				qq = (CFRegister)second;
				DefaultConnection con = new DefaultConnection(q, qq);
				if (q.addOut(con) && qq.addIn(con)) {
					connections.add(con);
					return con;
				}
			}  	else if (second instanceof CF2STRegister){
				q = (CFRegister)first;
				l = (CF2STRegister)second;
				DefaultConnection con = new DefaultConnection(q, l);
				if (q.addOut(con) && l.addIn(con)) {
					connections.add(con);
					return con;
				}
			} 
		} 	else if (first instanceof CFLogic){
			if (second instanceof CFRegister) {
				q = (CFRegister)second;
				s = (CFLogic)first;
				DefaultConnection con = new DefaultConnection(s, q);
				if (q.addIn(con) && s.addOut(con)) {
					connections.add(con);
					return con;
				}
			} else if (second instanceof CFLogic ) {
				s = (CFLogic)first;
				ss = (CFLogic)second;
				DefaultConnection conn = new DefaultConnection(s, ss);
				if (ss.addIn(conn) && s.addOut(conn)) {
					connections.add(conn);
					return conn;
				}
			} else if (second instanceof CF2STRegister){
				s = (CFLogic)first;
				l = (CF2STRegister)second;
				DefaultConnection con = new DefaultConnection(s, l);
				if (s.addOut(con) && l.addIn(con)) {
					connections.add(con);
					return con;
				}
			} 
		} else if (first instanceof ST2CFRegister) {
			if (second instanceof CFLogic) {
				k = (ST2CFRegister)first;
				s = (CFLogic)second;
				DefaultConnection con = new DefaultConnection(k, s);
				if (k.addOut(con) && s.addIn(con)) {
					connections.add(con);
					return con;
				}
			} else if (second instanceof CFRegister) {
				k = (ST2CFRegister)first;
				q = (CFRegister)second;
				DefaultConnection con = new DefaultConnection(k, q);
				if (k.addOut(con) && q.addIn(con)) {
					connections.add(con);
					return con;
				}
			} else if (second instanceof CF2STRegister) {
				k = (ST2CFRegister)first;
				l = (CF2STRegister)second;
				DefaultConnection con = new DefaultConnection(k, l);
				if (k.addOut(con) && l.addIn(con)) {
					connections.add(con);
					return con;

				}
			}
		} else if (first instanceof CF2STRegister) {
			if (second instanceof STLogic) {
				l = (CF2STRegister)first;
				t = (STLogic)second;
				DefaultConnection con = new DefaultConnection(l, t);
				if (l.addOut(con) && t.addIn(con)) {
					connections.add(con);
					return con;
				}
			} else if (second instanceof STRegister) {
				l = (CF2STRegister)first;
				p = (STRegister)second;
				DefaultConnection con = new DefaultConnection(l, p);
				if (l.addOut(con) && p.addIn(con)) {
					connections.add(con);
					return con;
				}
			} else if (second instanceof ST2CFRegister) {
				l = (CF2STRegister)first;
				k = (ST2CFRegister)second;
				DefaultConnection con = new DefaultConnection(l, k);
				if (l.addOut(con) && k.addIn(con)) {
					connections.add(con);
					return con;

				}
			}
		}



		throw new InvalidConnectionException("Cannot connect "+first.getClass().getSimpleName() + " to " + second.getClass().getSimpleName());
	}

	public void removeConnection(EditableConnection con) throws UnsupportedComponentException {
		SDFSNode first = (SDFSNode)con.getFirst();		
		SDFSNode second = (SDFSNode)con.getSecond();
		first.removeOut(second);
		second.removeIn(first);
		connections.remove(con);
	}

	public List<EditableConnection> getConnections() {
		return (List<EditableConnection>)((List)connections);		
	}

	public void validate() throws ModelValidationException {
		ModelValidationException errors =  new ModelValidationException();
		boolean good = true;
		for (SDFSLogicBase node : logic) {
			if (node.getIn().isEmpty() || node.getOut().isEmpty()) {
				errors.addError(STLogic._displayname+" ["+node.getId()+"]: combinational logic with empty preset or postset is not allowed");
				good = false;
			}
		}
		if (!good)
			throw errors;
	}

	public JPanel getSimulationControls() {
		if (panelSimControls == null) {
			panelSimControls = new DefaultSimControls(_modeluuid.toString());
		}
		return panelSimControls;
	}

	public void bind(WorkCraftServer server) {
		super.bind(server);
		server.python.exec(py_functions);
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
		for (BasicEditable n: logic)
			out.add(n);
		for (BasicEditable n: registers)
			out.add(n);

	}

	public List<SDFSRegisterBase> getRegisters() {
		return registers;
	}

	public List<SDFSLogicBase> getLogic() {
		return logic;
	}


}