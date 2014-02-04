package workcraft.gate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import workcraft.DocumentOpenException;
import workcraft.Tool;
import workcraft.ToolType;
import workcraft.WorkCraftServer;
import workcraft.common.DefaultSimControls;
import workcraft.common.ExternalProcess;
import workcraft.common.MPSATOutputParser;
import workcraft.common.PetriNetMapper;
import workcraft.counterflow.CFModel;
import workcraft.editor.Editor;
import workcraft.gate.GateModel;
import workcraft.petri.PetriDotGSaver;
import workcraft.petri.PetriModel;
import workcraft.petri.ReadArcsComplexityReduction;
import workcraft.spreadtoken.STModel;
import workcraft.stg.STGModel;

public class GateSpeedIndependenceCheck implements Tool {
	public static final String _modeluuid = "6f704a28-e691-11db-8314-0800200c9a66";
	public static final String _displayname = "Check for speed-independence (PUNF/MPSAT)";

	public boolean isModelSupported(UUID modelUuid) {
		return false;
	}

	public void init(WorkCraftServer server) {
	}

	public void run(Editor editor, WorkCraftServer server) {
		PetriNetMapper mapper = (PetriNetMapper)server.getToolInstance(PetriNetMapper.class);
		if (mapper == null) {
			JOptionPane.showMessageDialog(null, "This tool requires Petri Net Mapper tool, which was not loaded", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		PetriDotGSaver saver = (PetriDotGSaver)server.getToolInstance(PetriDotGSaver.class);
		if (saver == null) {
			JOptionPane.showMessageDialog(null, "This tool requires Petri Net .g export tool, which was not loaded", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		ReadArcsComplexityReduction rd =(ReadArcsComplexityReduction)server.getToolInstance(ReadArcsComplexityReduction.class);  
		if (saver == null) {
			JOptionPane.showMessageDialog(null, "This tool requires Petri Net read arcs complexity reduction tool, which was not loaded", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JFrame frame = (JFrame)server.python.get("_main_frame", JFrame.class);
		ExternalProcess p = new ExternalProcess(frame);

		GateModel doc = (GateModel)editor.getDocument();
		PetriModel schematicNet = mapper.map(server, doc);

		File env = new File(doc.getActiveInterfacePath());
		
		if (env.exists()) {
			STGModel iface;
			try {
				iface = (STGModel)editor.load(env.getAbsolutePath());
				schematicNet.applyInterface(iface);
			} catch (DocumentOpenException e) {
				JOptionPane.showMessageDialog(frame, "The environment STG file \"" + env.getPath() +"\" could not be opened ("+e.getMessage()+"). The interface will not be applied.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(frame, "The environment STG file \"" + env.getPath() +"\" does not exist. The interface will not be applied.", "Warning", JOptionPane.WARNING_MESSAGE);
		}
		
		PetriModel reducedNet = rd.reduce(schematicNet);
		
		reducedNet.buildSemimodularityCheckClauses();

		try {
			saver.writeFile("tmp/_net_.g", reducedNet);
			p.run(new String[] {"util/punf", "-s", "-t", "-p", "tmp/_net_.g"}, ".", "Unfolding report", true);
			p.run(new String[] {"util/mpsat", "-D", "-f", "tmp/_net_.mci"}, ".", "Model-checking report (deadlock)", true);
			File f = new File("tmp/_net_.g");
			f.delete();

			String badTrace = MPSATOutputParser.parseSchematicNetTrace(p.getOutput());

			if (badTrace != null) {
				if (JOptionPane.showConfirmDialog(null, "The system has a deadlock. Do you wish to load the event trace that leads to the deadlock?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
					((DefaultSimControls)editor.getSimControls()).setTrace(badTrace);
				return;
			}
			
			String formula = "";
			
			for (String clause: reducedNet.buildSemimodularityCheckClauses()) {
				if (formula.length()>0)
					formula+="|";
				formula+=clause;
			}

			PrintWriter out;
			out = new PrintWriter(new FileWriter("tmp/_smodch"));
			out.print(formula);
			out.close();

			p.run(new String[] {"util/mpsat", "-F", "-d", "@tmp/_smodch","tmp/_net_.mci"}, ".", "Model-checking report (hazards)", true);

			f = new File("tmp/_smodchi");
			f.delete();
			f = new File("tmp/_net_.mci");
			f.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public void deinit(WorkCraftServer server) {
		// TODO Auto-generated method stub

	}

	public ToolType getToolType() {
		return ToolType.GENERAL;
	}
}
