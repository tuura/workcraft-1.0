package workcraft.counterflow;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import workcraft.Tool;
import workcraft.ToolType;
import workcraft.WorkCraftServer;
import workcraft.counterflow.CFModel;
import workcraft.editor.BasicEditable;
import workcraft.editor.Editor;
import workcraft.gate.GateModel;
import workcraft.petri.PetriDotGSaver;
import workcraft.petri.PetriModel;
import workcraft.petri.ReadArcsComplexityReduction;
import workcraft.spreadtoken.STModel;

public class ClearRuleEditFlags implements Tool {
	public static final String _modeluuid = "9df82f00-7aec-11db-9fe1-0800200c9a66";
	public static final String _displayname = "Reset rules";

	public boolean isModelSupported(UUID modelUuid) {
		return false;
	}

	public void init(WorkCraftServer server) {
		// TODO Auto-generated method stub
	}

	public void run(Editor editor, WorkCraftServer server) {
		LinkedList<BasicEditable> lst = new LinkedList<BasicEditable>();
		editor.getDocument().getComponents(lst);
		for (BasicEditable e:lst) {
			
			if (e.selected && e instanceof CFLogic) {
				((CFLogic)e).clearFuncEditedFlags();
				((CFLogic)e).rebuildRuleFunctions();
			}
		}
	}

	public void deinit(WorkCraftServer server) {
		// TODO Auto-generated method stub

	}

	public ToolType getToolType() {
		return ToolType.GENERAL;
	}
}