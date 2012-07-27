/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.edition.mode;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.core.model.network.Variable;

public abstract class NodeEditionMode extends EditionMode
{
    private NodeType nodeType;

    public NodeEditionMode (EditorPanel editorPanel, ProbNet probNet, NodeType nodeType)
    {
        super (editorPanel, probNet);
        this.nodeType = nodeType;
    }

    @Override
    public void mousePressed (MouseEvent e, Point2D.Double position, Graphics2D g)
    {
        if (SwingUtilities.isLeftMouseButton (e))
        {
            if (Utilities.noMouseModifiers (e))
            {
                if (visualNetwork.getElementInPosition (position, g) == null)
                {
                    probNet.getPNESupport ().setWithUndo (true);
                    HashSet<String> existingNames = new HashSet<String> ();
                    for (ProbNode node : probNet.getProbNodes ())
                    {
                        String name = node.getName ();
                        if (name.contains ("["))
                        {
                            String[] nameParts = name.split (" \\[");
                            existingNames.add (nameParts[0]);
                        }
                        else
                        {
                            existingNames.add (node.getName ());
                        }
                    }
                    String nodeName = Utilities.getNextNodeName (nodeType, existingNames);
                    State states[] = DefaultStates.getStatesNodeType (nodeType,
                                                                      probNet.getDefaultStates ());
                    for (int i = 0; i < states.length; i++)
                    {
                        states[i] = new State (GUIDefaultStates.getString (states[i].getName ()));
                    }
                    Variable variable = new Variable (nodeName, states);
                    if (probNet.onlyTemporal ())
                    {
                        // default value
                        variable.setBaseName (nodeName);
                        variable.setName (nodeName + "[" + 0 + "]");
                        variable.setTimeSlice (0);
                    }
                    ArrayList<StringWithProperties> decisionCriteria = probNet.getDecisionCriteria ();
                    if (nodeType == NodeType.UTILITY && decisionCriteria != null)
                    {
                        variable.setDecisionCriteria (decisionCriteria.get (0));
                    }
                    AddProbNodeEdit addProbNodeEdit = new AddProbNodeEdit (probNet, variable,
                                                                           nodeType, position);
                    try
                    {
                        probNet.doEdit (addProbNodeEdit);
                    }
                    catch (Exception e1)
                    {
                        System.err.println (e1.toString () + " 1");
                        e1.printStackTrace ();
                        JOptionPane.showMessageDialog (this.editorPanel,
                                                       e1.toString (),
                                                       "Error creating node",
                                                       JOptionPane.ERROR_MESSAGE);
                    }
                    editorPanel.adjustPanelDimension ();
                    editorPanel.repaint ();
                }
            }
        }
    }

    @Override
    public void mouseReleased (MouseEvent e, Point2D.Double cursorPosition, Graphics2D g)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseDragged (MouseEvent e,  Point2D.Double position, double diffX, double diffY, Graphics2D g)
    {
        // TODO Auto-generated method stub
    }
}
