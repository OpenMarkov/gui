package openmarkov.core.gui.clipboard;


import java.awt.datatransfer.DataFlavor;


/**
 * This class provides the information about the data of a node that can be
 * transferred.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class ContentDataFlavor extends DataFlavor {

	/**
	 * Constructs an instance of the data flavor that describes the adittionalProperties
	 * of a node.
	 */
	public ContentDataFlavor() {

		super(ClipboardContent.class, ClipboardContent.class.getName());

	}
}
