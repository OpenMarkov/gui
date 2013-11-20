
package org.openmarkov.core.gui.dialog.node;

import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class ReorderVariablesDialog extends OkCancelHorizontalDialog
{
    private JPanel                variablesCombinationPanel;
    private ProbNode              probNode;
    private ReorderVariablesPanel reorderVariablesPanel;

    public ReorderVariablesDialog (Window owner, ProbNode probNode)
    {
        super (owner);
        this.probNode = probNode;
        probNode.getProbNet ().getPNESupport ().setWithUndo (true);
        probNode.getProbNet ().getPNESupport ().openParenthesis ();
        initialize ();
        setLocationRelativeTo (owner);
        setName ("ReorderVariablesDialog");
        // setMinimumSize(new Dimension( 100, 100 ));
        setResizable (true);
        pack ();
    }

    private void initialize ()
    {
        setTitle (stringDatabase.getString ("NodePotentialReorderVariables.Title.Label"));
        configureComponentsPanel ();
        pack ();
    }

    private void configureComponentsPanel ()
    {
        // getComponentsPanel().setLayout(new BorderLayout(5, 5));
        // getComponentsPanel().add( getVariablesCombinationPanel(),
        // BorderLayout.CENTER );
        getComponentsPanel ().add (getReorderVariablesPanel ());
    }

    protected JPanel getVariablesCombinationPanel ()
    {
        if (variablesCombinationPanel == null)
        {
            variablesCombinationPanel = new VariablesCombinationPanel (probNode);
            // dissociateStatesCheckBoxPanel.setLayout( new FlowLayout() );
            variablesCombinationPanel.setName ("variablesCombinationPanel");
        }
        return variablesCombinationPanel;
    }

    public ReorderVariablesPanel getReorderVariablesPanel ()
    {
        if (reorderVariablesPanel == null)
        {
            reorderVariablesPanel = new ReorderVariablesPanel (probNode);
            reorderVariablesPanel.setName ("networkAgentsPanel");
            reorderVariablesPanel.setBorder (new EmptyBorder (0, 0, 0, 0));
        }
        return reorderVariablesPanel;
    }

    public int requestValues ()
    {
    	getReorderVariablesPanel ();
        setVisible (true);
        return selectedButton;
    }

    /**
     * This method carries out the actions when the user press the Ok button
     * before hide the dialog.
     * @return true if the dialog box can be closed.
     */
    protected boolean doOkClickBeforeHide ()
    {
        probNode.getProbNet ().getPNESupport ().closeParenthesis ();
        return true;
    }

    /**
     * This method carries out the actions when the user press the Cancel button
     * before hide the dialog.
     */
    protected void doCancelClickBeforeHide ()
    {
        probNode.getProbNet ().getPNESupport ().closeParenthesis ();
        // TODO PNESupport must support more depth levels parenthesis
        // As current performance edits from ReorderVariablesPanel only be
        // undone when cancel
        // NodesPropertiesDialog
        for (int i = getReorderVariablesPanel ().getEdits ().size () - 1; i >= 0; i--)
        {
            getReorderVariablesPanel ().getEdits ().get (i).undo ();
        }
    }
}
