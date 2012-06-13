/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.dialog.common;

import java.text.MessageFormat;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public abstract class PotentialPanel extends JPanel
{
	/**
	 * If true, values inside the panel will not be editable
	 */
	private boolean readOnly;
	private CommentHTMLScrollPane commentHTMLScrollPaneNodeProbsComment;
	private JTextArea jTextAreaLabelNodeDefinitionComment;

    /**
     * Fill the panel with the data from the node
     * @param probNode
     */
    public abstract void setData (ProbNode probNode);
    
    /**
     * Modify the node according to the changes entered by the user in the panel
     * @throws NotEnoughMemoryException
     */
    public abstract void saveChanges() throws NotEnoughMemoryException;

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	
	/**
	 * This method initializes commentHTMLScrollPaneNodeDefinitionComment
	 * 
	 * @return a new comment HTML scroll pane.
	 */
	protected CommentHTMLScrollPane getCommentHTMLScrollPaneNodeDefinitionComment() {

		if (commentHTMLScrollPaneNodeProbsComment == null) {
			commentHTMLScrollPaneNodeProbsComment = new CommentHTMLScrollPane();
			commentHTMLScrollPaneNodeProbsComment.setName( 
					"commentHTMLScrollPaneNodeProbsComment" );
		}
		return commentHTMLScrollPaneNodeProbsComment;
	}
	
    
}
