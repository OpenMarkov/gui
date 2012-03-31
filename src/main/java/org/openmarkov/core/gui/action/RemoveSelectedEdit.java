/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.action;

import java.util.ArrayList;

import org.openmarkov.core.action.CRemoveProbNodeEdit;
import org.openmarkov.core.action.CompoundPNEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.graphic.VisualLink;
import org.openmarkov.core.gui.graphic.VisualNetwork;
import org.openmarkov.core.gui.graphic.VisualNode;

@SuppressWarnings("serial")
/**
 * Compound edit that removes selected nodes and links
 * @author Iñigo
 *
 */
public class RemoveSelectedEdit extends CompoundPNEdit
{
    private ArrayList<VisualNode> nodesToRemove;
    private ArrayList<VisualLink> linksToRemove;


    /**
     * 
     * Constructor for RemoveSelectedEdit.
     * @param visualNetwork
     */
    public RemoveSelectedEdit (VisualNetwork visualNetwork)
    {
        super (visualNetwork.getNetwork ());
        this.nodesToRemove = visualNetwork.getSelectedNodes ();
        this.linksToRemove = union (visualNetwork.getSelectedLinks (),
                                    visualNetwork.getLinksOfNodes (this.nodesToRemove));
    }    

    @Override
    public void generateEdits ()
        throws NotEnoughMemoryException,
        NonProjectablePotentialException,
        WrongCriterionException
    {
        for (VisualLink link : linksToRemove) {
            try {
				edits.add (new RemoveLinkEdit (probNet,
				                                  probNet.getVariable(link.getSourceNode ().getProbNode ().getName ()),
				                                  probNet.getVariable(link.getDestinationNode ().getProbNode ().getName ()),
				                                  true));
			} catch (ProbNodeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
        for (VisualNode node : nodesToRemove) {
            edits.add ( new CRemoveProbNodeEdit( probNet, node.getProbNode ()));
        }
    }
    
    /**
     * This method makes an union operation on two lists of links.
     * 
     * @param list1
     *            first list.
     * @param list2
     *            second list.
     * @return a list that is the result of an union operation of two lists of
     *         links.
     */
    private ArrayList<VisualLink> union(ArrayList<VisualLink> list1,
            ArrayList<VisualLink> list2) {

        ArrayList<VisualLink> result = new ArrayList<VisualLink>();

        result.addAll(list1);
        for (VisualLink o : list2) {
            if (!result.contains(o)) {
                result.add(o);
            }
        }

        return result;

    }    
    
}
