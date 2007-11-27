package workcraft.editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



public class TransferableDocumentFragment implements Transferable {
	public static DataFlavor DOCUMENT_FRAGMENT_FLAVOR = new DataFlavor(org.w3c.dom.Document.class, "XML");

	DataFlavor flavors[] = { DOCUMENT_FRAGMENT_FLAVOR, DataFlavor.stringFlavor };
	org.w3c.dom.Document doc;

	public TransferableDocumentFragment(org.w3c.dom.Document doc) {
		this.doc = doc;
	}

	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (
				flavor.getRepresentationClass() == org.w3c.dom.Document.class ||
				flavor == DataFlavor.stringFlavor				
		);
	}
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (isDataFlavorSupported(flavor)) {
			if (flavor == DataFlavor.stringFlavor)
			{
				try
				{
					TransformerFactory tFactory = TransformerFactory.newInstance();
					Transformer transformer = tFactory.newTransformer();

					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(baos);

					
					transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					transformer.transform(source, result);
					
					return (baos.toString("utf-8"));
				} catch (TransformerException e) {
					e.printStackTrace();
					return null;
				}
			} else 
				return doc;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}	
}