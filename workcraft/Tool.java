package workcraft;

import java.util.UUID;
import workcraft.editor.Editor;

public interface Tool {
	public void init(Framework server);
	public void deinit(Framework server);
	public void run(Editor editor, Framework server);
	public ToolType getToolType();
	public boolean isModelSupported(UUID modelUuid);
}