package workcraft.editor;
import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;

import workcraft.DocumentOpenException;
import workcraft.Document;

public interface Editor {
	public void save();
	public void saveAs();
	public void open();
	public Document load(String path) throws DocumentOpenException;
	public void setDocument(Document document);
	public List<BasicEditable> getSelection();
	public Document getDocument();
	public String getFileName();
	public String getLastDirectory();
	public Component getSimControls();
	public void refresh();
}