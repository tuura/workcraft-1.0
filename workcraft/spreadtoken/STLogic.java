package workcraft.spreadtoken;

import java.awt.event.KeyEvent;
import java.util.UUID;

import workcraft.Document;
import workcraft.UnsupportedComponentException;
import workcraft.editor.BasicEditable;
import workcraft.sdfs.SDFSLogic1Way;

public class STLogic extends SDFSLogic1Way {
	public STLogic(BasicEditable parent) throws UnsupportedComponentException {
		super(parent);
	}

	public static final UUID _modeluuid = UUID.fromString("a57b3350-73d3-11db-9fe1-0800200c9a66");
	public static final UUID[] _modeluuidex = new UUID[] {UUID.fromString("aab78c50-e6bf-11dc-95ff-0800200c9a66")};
	public static final String _displayname = "Spreadtoken Logic";

	@Override
	public void rebuildRuleFunctions() {
		resetFunc = expandRule("preset:l r,preset:r !m");
		evalFunc = expandRule("preset:l e,preset:r m");
	}
}