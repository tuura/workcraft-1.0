package workcraft.stg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import workcraft.Tool;
import workcraft.ToolType;
import workcraft.WorkCraftServer;
import workcraft.common.ExternalProcess;
import workcraft.common.MPSATOutputParser;
import workcraft.editor.BasicEditable;
import workcraft.editor.Editor;
import workcraft.petri.EditablePetriTransition;

public class STGSelectByID implements Tool {

	public static final String _modeluuid = "10418180-D733-11DC-A679-A32656D89593";
	public static final String _displayname = "Select by ID";

	public void deinit(WorkCraftServer server) {
		// TODO Auto-generated method stub

	}

	public void init(WorkCraftServer server) {
		// TODO Auto-generated method stub

	}

	public boolean isModelSupported(UUID modelUuid) {
		// TODO Auto-generated method stub
		return false;
	}


	public void run(Editor editor, WorkCraftServer server) {
		STGModel doc = (STGModel) editor.getDocument();

		String idname=JOptionPane.showInputDialog(null, "Enter ID", "Error", JOptionPane.QUESTION_MESSAGE);
		
		LinkedList<BasicEditable> comps	= new LinkedList<BasicEditable>();
		doc.getComponents(comps);

		for (BasicEditable be: comps) {
			if (be.getId().equals(idname)) {
				be.selected=true;
			}
		}
	}

	public ToolType getToolType() {
		return ToolType.GENERAL;
	}

}
