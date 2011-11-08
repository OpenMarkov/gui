package openmarkov.core.gui.dialog.comment;


import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.StyledEditorKit;

import openmarkov.core.gui.dialog.comment.hexidec.ekit.EkitCore;
import openmarkov.core.gui.dialog.comment.hexidec.ekit.component.ExtendedHTMLDocument;
import openmarkov.core.gui.dialog.network.CommentListener;
import openmarkov.core.gui.localize.StringResource;
import openmarkov.core.gui.localize.StringResourceLoader;

/**
 * Comment HTML Box Scroll Pane This class encapsulate all the behaviour as a
 * single component so the programmer must not be worried about internal
 * initialization of the components
 * 
 * @author jlgozalo
 * @version 1.0 based on definition made by alberto
 */
public class CommentHTMLScrollPane extends JScrollPane implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8678529566501560594L;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;

	private JTextPane jTextPaneCommentHTML = null;

	private HTMLTextEditor hTMLTextEditor = null;

	private static int HTML_COMMENT_HEIGHT = 10;
	private static int HTML_COMMENT_WIDTH = 30;
	
	/**
	 * Listener to the comment changes.
	 */
	private HashSet<CommentListener> commentListeners =
		new HashSet<CommentListener>();

	private String title = "";
	/**
	 * This method initialises this instance.
	 */
	public CommentHTMLScrollPane() {

		initialize();
	}

	/**
	 * This method initialises this instance.
	 */
	public CommentHTMLScrollPane(final String title) {

		this.title = title;
		initialize();
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(final String title) {
	
		this.title = title;
		if (hTMLTextEditor != null) {
			hTMLTextEditor.setTitle(title);
		}
	}

	/**
	 * This method configures the dialog box.
	 */
	private void initialize() {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		setBorder(BorderFactory.createLineBorder(SystemColor.activeCaption, 2));
		setViewportView(getJTextPaneCommentHTML());
		setSize(HTML_COMMENT_WIDTH, HTML_COMMENT_HEIGHT);
		hTMLTextEditor =
			new HTMLTextEditor(null, "");
		hTMLTextEditor.setTitle(title);
		hTMLTextEditor.setVisible(false);
		

	}

	/**
	 * This method initialises jTextPaneCommentHTML
	 * 
	 * @return the JTextPane with the HTML Comment
	 */
	private JTextPane getJTextPaneCommentHTML() {

		if (jTextPaneCommentHTML == null) {
			jTextPaneCommentHTML = new JTextPane();
			jTextPaneCommentHTML.setEditable(false);
			jTextPaneCommentHTML.setSize(new Dimension(HTML_COMMENT_WIDTH,
				HTML_COMMENT_HEIGHT));
			//only to put an initial value just in case
			jTextPaneCommentHTML.setText(dialogStringResource
				.getString("CommentHTMLScrollPane.jTextPaneCommentHTML.Text"));
			jTextPaneCommentHTML.addMouseListener(doubleClickSelector);
			jTextPaneCommentHTML.addMouseListener(this);
		}
		return jTextPaneCommentHTML;
	}

	/**
	 * This method set the text of jTextPaneCommentHTML
	 * 
	 * @param text
	 *            the text to put in the comment
	 */
	public void setCommentHTMLTextPaneText(String text) {

		if (jTextPaneCommentHTML == null) {
			// do nothing
		} else {
			//creates the HTML object
			EkitCore ekitCoreEditorHTML =
				new EkitCore(null, null, text, null, null, true,
					false, true, true, null, null, false, false, true, false,
					EkitCore.TOOLBAR_DEFAULT_SINGLE);
			try {
				jTextPaneCommentHTML
					.setEditorKit(
							(StyledEditorKit) ekitCoreEditorHTML.gethtmlKit());
				jTextPaneCommentHTML.setDocument(
						ekitCoreEditorHTML.getExtendedHtmlDoc());
				jTextPaneCommentHTML.setText(text);
			} catch (IllegalArgumentException ex) {
			}
           

		}
	}

	/**
	 * This method get the text of jTextPaneCommentHTML
	 * 
	 * @return the text of the comment
	 */
	public String getCommentText() {

		String text = "";

		if (jTextPaneCommentHTML == null) {
			// do nothing
		} else {
			text = jTextPaneCommentHTML.getText();
		}
		return text;
	}

	/**
	 * public method to set the document hTMLTextEditor in the JTextPane
	 * 
	 * @param doc -
	 *            the HTML document to put in the hTMLTextEditor
	 */
	public void setCommentHTMLText(ExtendedHTMLDocument doc) {

    	jTextPaneCommentHTML.setEditorKit(getHTMLTextEditor()
			.getExtendedHTMLEditorKit());
		jTextPaneCommentHTML.setDocument(doc);
		jTextPaneCommentHTML.setText(hTMLTextEditor.getCommentText());
	}

	/**
	 * initialize the HTMLTextEditor component
	 */
	private HTMLTextEditor getHTMLTextEditor () {
		if (hTMLTextEditor == null) {
			hTMLTextEditor =
				new HTMLTextEditor(null, jTextPaneCommentHTML.getText());
			hTMLTextEditor.setTitle(title);
			hTMLTextEditor.setVisible(true);
		}
		return hTMLTextEditor;
		
	}
	
	/**
	 * Double Click Selector for the HTML Comment area
	 */
	private MouseListener doubleClickSelector = new MouseAdapter() {

		public void mouseClicked(MouseEvent e) {

			
		}
	};
	
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getClickCount() == 2) {
			try {
				
				hTMLTextEditor =
					new HTMLTextEditor(null, jTextPaneCommentHTML.getText());
				hTMLTextEditor.setTitle(title);
				hTMLTextEditor.setVisible(true);
				if (hTMLTextEditor.getOkButtonStatus()){
					jTextPaneCommentHTML
						.setEditorKit((StyledEditorKit) hTMLTextEditor
						.getExtendedHTMLEditorKit());
					jTextPaneCommentHTML.setDocument(hTMLTextEditor
							.getEextendedHTMLDocument());
					jTextPaneCommentHTML.setText(hTMLTextEditor
							.getCommentText());
					notifyCommentChanged();
				}
				} catch (IllegalArgumentException ex) {
			}

		}
	}

	private void notifyCommentChanged() {
		for (CommentListener listener : commentListeners) {
			listener.commentHasChanged();
		}
				
		
	}

	
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void addCommentListener(CommentListener newCommentListener){
		commentListeners.add(newCommentListener);
	}
}
