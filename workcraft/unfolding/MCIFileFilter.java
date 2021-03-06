package workcraft.unfolding;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MCIFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		if (f.getName().endsWith(".mci"))
			return true;
		return false;
	}

	@Override
	public String getDescription() {
		return "Finite Petri net prefix (*.mci)";
	}

}