/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.oon;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.gui.graphic.SelectionRectangle;
import org.openmarkov.core.gui.graphic.VisualArrow;
import org.openmarkov.core.gui.graphic.VisualElement;
import org.openmarkov.core.gui.graphic.VisualLink;
import org.openmarkov.core.gui.graphic.VisualNetwork;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.oon.InstanceLink;
import org.openmarkov.core.oon.InstanceNode;
import org.openmarkov.core.oon.OOBNet;
import org.openmarkov.core.oon.action.AddInstanceLinkEdit;

public class VisualOONetwork extends VisualNetwork
{
    
    
    /**
     * HashMap of visual instances.
     */
    private HashMap<String, VisualInstance> visualInstances = new HashMap<String, VisualInstance>();

    /**
     * List of visual instance links.
     */
    private ArrayList<VisualInstanceLink> visualInstanceLinks = new ArrayList<VisualInstanceLink>();
    
    /**
     * Set of selected links.
     */
    private HashSet<VisualInstance> selectedInstances = new HashSet<VisualInstance>();  
    
    /**
     * Listener to the selection.
     */
    private Set<VisualOOSelectionListener> selectionListeners = new HashSet<VisualOOSelectionListener>();

    private VisualInstance newInstanceLinkSource;    

    
    public VisualOONetwork (ProbNet probNet, EditorPanel editorPanel)
    {
        super (probNet, editorPanel);
    }


    /**
     * Constructs visual elements
     */
    @Override
    protected void constructVisualInfo ()
    {
        super.constructVisualInfo ();
        
        // construct visual instances
        visualInstances.clear();
        for(String instanceName : ((OOBNet)probNet).getInstances().keySet())
        {
            visualInstances.put(instanceName, new VisualInstance(((OOBNet)probNet).getInstances().get(instanceName), visualNodes));
        }   
        // construct visual instance links
        visualInstanceLinks.clear();
        for(InstanceLink link : ((OOBNet)probNet).getInstanceLinks())
        {
            VisualInstance sourceVisualInstance = visualInstances.get(link.getSourceInstance().getName());
            VisualInstance destVisualInstance = visualInstances.get(link.getDestInstance().getName()).getSubInstance(link.getDestSubInstance().getName());
            visualInstanceLinks.add(new VisualInstanceLink(sourceVisualInstance, destVisualInstance));
        }        
    }
    
    /**
     * Paints the instances.
     * 
     * @param g
     *            the graphics context in which to paint.
     */
    protected void paintInstances(Graphics2D g) {

        for (VisualInstance visualInstance : visualInstances.values()) {
            visualInstance.paint(g);
        }

    }
    
    /**
     * Paints the instance links.
     * 
     * @param g
     *            the graphics context in which to paint.
     */
    protected void paintInstanceLinks(Graphics2D g) {

        for (VisualInstanceLink visualInstanceLink : visualInstanceLinks) {
            visualInstanceLink.paint(g);
        }

    }   
    
    
    /**
     * Overwrited 'paint' method to avoid to call it explicitly.
     * 
     * @param g
     *            the graphics context in which to paint.
     */
    @Override
    public void paint(Graphics2D g) {
        paintInstances(g);
        paintInstanceLinks(g);
        super.paint (g);
    }
    
    /**
     * Returns the instance in the given position if there is one, null otherwise
     * @param position
     * @param g
     * @return
     */
    public VisualInstance whatInstanceInPosition (java.awt.geom.Point2D.Double position,
                                                  Graphics2D g)
    {
        VisualInstance instance = null;
        VisualInstance instanceFound = null;
        Iterator<VisualInstance> iterator = visualInstances.values ().iterator ();
        while ((instanceFound == null) && iterator.hasNext ())
        {
            instance = iterator.next ();
            if (instance.pointInsideShape (position, g))
            {
                instanceFound = instance;
            }
        }
        return instanceFound;
    }
    
    /**
     * Checks if is there any selected element in a position.
     * 
     * @param position
     *            position to be checked.
     * @param g
     *            graphics where the network is painted.
     * @return if a selected element is there in the position, returns the
     *         element, else returns null.
     */
    @Override
    public VisualElement getElementInPosition(Point2D.Double position,
                                                Graphics2D g) {

        VisualElement elementSelected = null;

        if ((elementSelected = whatInstanceInPosition(position, g)) == null) {
           elementSelected = super.getElementInPosition (position, g);
        }

        return elementSelected;

    }
    
    /**
     * Sets the selection state of an element.
     * 
     * @param element
     *            element to be selected/deselected.
     * @param selected
     *            new selection state.
     */
    @Override
    protected void setSelectedElement(VisualElement element, boolean selected) {

        if (selected != element.isSelected()) {
            if (element instanceof VisualNode) {
                if (selected) {
                    selectedNodes.add((VisualNode) element);
                } else {
                    selectedNodes.remove(element);
                }
            } else if (element instanceof VisualLink){
                if (selected) {
                    selectedLinks.add((VisualLink) element);
                } else {
                    selectedLinks.remove(element);
                }
            } else {
                if (selected) {
                    selectedInstances.add((VisualInstance) element);
                } else {
                    selectedInstances.remove(element);
                }
            }
            notifyObjectsSelected();
            element.setSelected(selected);
        }
    }
    
    /**
     * Notifies to the registered selection listener how many nodes and links
     * are selected, and which are the especific selected nodes. 
     * Also notifies this situation to the menu assistant.
     */
    private void notifyObjectsSelected() {

        for (VisualOOSelectionListener listener : selectionListeners) {
            listener.objectsSelected(
                getSelectedNodes(), getSelectedLinks(), getSelectedInstances());
        }
    }    
    
    /**
     * Sets the selection state of an instance.
     * 
     * @param node
     *            node to be selected/deselected.
     * @param selected
     *            new selection state.
     */
    public void setSelectedInstance(VisualInstance instance, boolean selected) {

        setSelectedElement(instance, selected);
    }
    
    /**
     * Sets the selection state of a node identified by its name.
     * 
     * @param name
     *            name of the node to be selected/deselected.
     * @param selected
     *            new selection state.
     */
    public void setSelectedInstance(String name, boolean selected) {

        boolean found = false;
        int i = 0;

        while (!found && i < visualInstances.size()) {
            if (visualInstances.get(i).getName().equals(name)) {
                setSelectedElement(visualInstances.get(i), selected);
                found = true;
            } else {
                i++;
            }
        }
    }    
    
    /**
     * Selects all instances.
     * 
     * @param selected
     *            new selection state.
     */
    public void setSelectedAllInstances(boolean selected) {

        for (VisualInstance instance : visualInstances.values()) {
            setSelectedElement(instance, selected);
        }

        if(!selected)
        {
            selectedInstances.clear();
        }
        
    }   
    
    /**
     * Selects all nodes and links.
     * 
     * @param selected
     *            new selection state.
     */
    @Override
    public void setSelectedAllObjects(boolean selected) {

        super.setSelectedAllObjects (selected);
        setSelectedAllInstances(selected);

    }    
    
    /**
     * Move the selected nodes an amount in both axis.
     * 
     * @param diffX
     *            X-axis movement.
     * @param diffY
     *            Y-axis movement.
     */
    public void moveSelectedInstances(double diffX, double diffY) {

        for (VisualInstance instance : visualInstances.values()) {
            if (instance.isSelected()) {
                instance.move(diffX, diffY);
                if(g2!=null)
                {
                    instance.paint((Graphics2D) g2);
                }
            }
        }
    }   
    
    /**
     * Selects the nodes and links that are inside the selection rectangle and deselects
     * the ones that are outside.
     * 
     * @param selection
     *            object that manages the selection.
     */
    @Override
    public void selectElementsInsideSelection(SelectionRectangle selection) {

        setSelectedAllNodes (false);
        setSelectedAllLinks (false);
        // Select nodes
        ArrayList<VisualNode> selectedVisualNodes = new ArrayList<VisualNode> (); 
        for (VisualNode node : visualNodes) {
            if(selection.containsNode(node) && !(node.getProbNode() instanceof InstanceNode)){
                setSelectedElement(node, true);
                selectedVisualNodes.add (node);
            }
        }
        // Select links
        for(VisualLink selectedLink: getLinksOfNodes (selectedVisualNodes, true))
        {
            setSelectedElement(selectedLink, true);
        }
        
        for (VisualInstance instance : visualInstances.values()) {
            if (selection.containsRectangle (instance.getCoordinateX (),
                                             instance.getCoordinateY (), 
                                             instance.getCoordinateX () + instance.getWidth (),
                                             instance.getCoordinateY () + instance.getHeight ()))
            {
                setSelectedElement (instance, true);
            }
        }       
    }
    
    /**
     * Returns the list of nodes belonging to the selected instances
     * 
     * @return
     */
    public ArrayList<VisualNode> getVisualNodesOfSelectedInstances() {
        ArrayList<VisualNode> visualNodes = new ArrayList<VisualNode>();

        for (VisualInstance instance : visualInstances.values()) {
            if (instance.isSelected()) {
                visualNodes.addAll(instance.getVisualNodes());
            }
        }
        return visualNodes;
    }    
    
    /**
     * This method returns a list containing the selected instances.
     * 
     * @return a list containing the selected instances.
     */
    public ArrayList<VisualInstance> getSelectedInstances() {

        return new ArrayList<VisualInstance>(selectedInstances);

    }       
    
    /**
     * 
     */
    @Override
    public void addToSelection (java.awt.geom.Point2D.Double cursorPosition, Graphics2D g)
    {
        VisualNode node = null;
        VisualLink link = null;
        VisualInstance instance = null;
        
        if ((instance = whatInstanceInPosition (cursorPosition, g)) != null)
        {
            setSelectedInstance (instance, !instance.isSelected ());
        }
        else if ((node = whatNodeInPosition (cursorPosition, g)) != null)
        {
            setSelectedNode (node, !node.isSelected ());
        }
        else if ((link = whatLinkInPosition (cursorPosition, g)) != null)
        {
            setSelectedLink (link, !link.isSelected ());
        }
    }
    
    /**
     * Cleans selection and sets it to whatever is in the cursorPosition
     * @param cursorPosition
     * @param g
     * @return element in the position given, null if none
     */
    public VisualElement select (java.awt.geom.Point2D.Double cursorPosition, Graphics2D g)
    {
        VisualInstance instance = null;
        VisualElement selectedElement = null;
        if ((instance = whatInstanceInPosition (cursorPosition, g)) != null)
        {
            setSelectedAllObjects (false);
            setSelectedInstance (instance, true);
            selectedElement = instance;
        }
        else
        {
            selectedElement = super.selectElementInPosition (cursorPosition, g);
        }
        return selectedElement;
    }
    
    /**
     * Starts link creation
     * @param cursorPosition
     * @param g
     */
    @Override
    public void startLinkCreation (java.awt.geom.Point2D.Double cursorPosition, Graphics2D g)
    {
        VisualInstance instance = null;
        
        if ((instance = whatInstanceInPosition (cursorPosition, g)) != null)
        {
            newLink = new VisualArrow (new Point2D.Double (instance.getCenter ().getX (),
                                                           instance.getCenter ().getY ()),
                                       cursorPosition);
            newInstanceLinkSource = instance;
        }
        else
        {
            super.startLinkCreation (cursorPosition, g);
        }  
    }    
    
    /**
     * Move the selected elements an amount in both axis.
     * @param diffX X-axis movement.
     * @param diffY Y-axis movement.
     */
    public void moveSelectedElements (double diffX, double diffY)
    {
        moveSelectedNodes (diffX, diffY);
        moveSelectedInstances (diffX, diffY);
    }  
    
    /**
     * Fills the array of information of the selected nodes and their actual
     * state.
     * 
     * @return list where are the moved nodes information.
     */
    public List<VisualNode> fillVisualNodesSelected() {

        List<VisualNode> movedNodes = super.fillVisualNodesSelected ();

        movedNodes.addAll (getVisualNodesOfSelectedInstances());
        return movedNodes;

    }    
    
    public void markSelectedInstancesAsInput() {
        for (VisualInstance instance : getSelectedInstances()) {
            instance.setInput(!instance.isInput());
        }
    }

    @Override
    public PNEdit finishLinkCreation (java.awt.geom.Point2D.Double point, Graphics2D g)
    {
        PNEdit linkEdit = null;
        if (newLink != null) {
            newLink = null;
            VisualInstance newInstanceLinkDestination = null;
            
            if ((newInstanceLinkDestination = whatInstanceInPosition(point, g)) != null
                    && newInstanceLinkSource != null) {
                VisualInstance inputParameter = newInstanceLinkDestination
                        .whatParameterInPosition(point, g);
                if (inputParameter != null
                        && inputParameter
                                .getInstance()
                                .getClassNet()
                                .getName()
                                .equals(newInstanceLinkSource.getInstance()
                                        .getClassNet().getName())) {
                    linkEdit = new AddInstanceLinkEdit(probNet,
                            newInstanceLinkSource.getInstance(),
                            newInstanceLinkDestination.getInstance(),
                            inputParameter.getInstance());
                }
            } else {
                super.finishLinkCreation (point, g);
            }
        } 
        return linkEdit;
    }
   
}
