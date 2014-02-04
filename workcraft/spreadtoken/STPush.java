package workcraft.spreadtoken;

import java.util.UUID;

import workcraft.UnsupportedComponentException;
import workcraft.editor.BasicEditable;

public class STPush extends DynamicElementBase {
	public static final UUID _modeluuid = UUID.fromString("a57b3350-73d3-11db-9fe1-0800200c9a66");
	public static final UUID[] _modeluuidex = new UUID[] {UUID.fromString("aab78c50-e6bf-11dc-95ff-0800200c9a66")};
	public static final String _displayname = "Push";
	

	public STPush (BasicEditable parent) throws UnsupportedComponentException {
		super (parent);
	}
	
	public void rebuildRuleFunctions() {
		/*	markFunc = expandRule("self re,r-preset m,r-postset !m");
		unmarkFunc = expandRule("self !re,r-preset !m,r-postset m");
		enableFunc = expandRule("self !m,preset:l e,preset:r m");
		disableFunc = expandRule("self m,preset:l r,preset:r !m");*/
	}
}
