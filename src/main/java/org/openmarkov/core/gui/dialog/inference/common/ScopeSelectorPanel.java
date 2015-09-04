package org.openmarkov.core.gui.dialog.inference.common;

import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.gui.dialog.inference.common.ScopeType;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jorge on 01/07/2015.
 */
public class ScopeSelectorPanel extends JPanel {

    private StringDatabase stringDatabase = StringDatabase.getUniqueInstance();

    private JPanel scopeTypePanel;
    private ButtonGroup scopeTypeSelector;
    private JPanel decisionSelectorPanel;
    private JPanel mainPanel;
    private ProbNet probNet;
    private Variable decisionSelected;
    private JRadioButton globalRadioButton;
    private JRadioButton decisionRadioButton;

    HashMap<JComboBox<String>, Variable> selectedScenario;
    private JPanel decisionScenarioPanel;
    private ScopeType scopeType;
    private JComboBox<String> decisionSelector;

    private List<Finding> selectedFindings;

    public ScopeSelectorPanel(ProbNet probNet){
        super();
        this.probNet = probNet;
        selectedFindings = new ArrayList<>();
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(new TitledBorder(stringDatabase.getString("ScopeSelector.Title")));
        this.add(getMainPanel());
        this.setVisible(true);
    }

    public JPanel getMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(getScopeTypePanel());

        mainPanel.add(getDecisionSelectorPanel());

        List<Node> decisionNodes = probNet.getNodes(NodeType.DECISION);
        if(decisionNodes == null || decisionNodes.size() < 1){
            for (Component component : scopeTypePanel.getComponents()) {
                component.setEnabled(false);
            }
        }


        mainPanel.add(getDecisionScenarioPanel());
        return mainPanel;
    }

    public JPanel getScopeTypePanel() {
        scopeTypePanel = new JPanel();
        scopeTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel scopeLabel = new JLabel(stringDatabase.getString("ScopeSelector.Type"));
        scopeTypePanel.add(scopeLabel);

        JPanel scopeTypeSelectorPanel = new JPanel();
        scopeTypeSelectorPanel.setLayout(new FlowLayout());

        scopeTypeSelector = new ButtonGroup();
        for (ScopeType scopeTypeEnum : ScopeType.values()) {
            JRadioButton selectedScopeType = new JRadioButton(stringDatabase.getString(scopeTypeEnum.toString()));
            scopeTypeSelector.add(selectedScopeType);

//            scopeTypeSelector.addItem(stringDatabase.getString(scopeTypeEnum.toString()));

            selectedScopeType.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JRadioButton scopeSelector = (JRadioButton) e.getSource();
                    if (scopeSelector.getText().equals(stringDatabase.getString(ScopeType.GLOBAL.toString()))) {
                        setScopeType(ScopeType.GLOBAL);
                        decisionSelected = null;

                        if (decisionSelectorPanel != null) {
                            for (Component component : decisionSelectorPanel.getComponents()) {
                                component.setEnabled(false);
                            }
                        }
                    } else {
                        setScopeType(ScopeType.DECISION);
                        if (decisionSelector != null) {
                            try {
                                decisionSelected = probNet.getVariable(decisionSelector.getSelectedItem().toString());
                            } catch (NodeNotFoundException e1) {
                                e1.printStackTrace();
                            }
                        }
                        if (decisionSelectorPanel != null) {
                            for (Component component : decisionSelectorPanel.getComponents()) {
                                component.setEnabled(true);
                            }
                        }
                    }
                    refreshScenario();
                }
            });
            if(scopeTypeEnum.equals(ScopeType.GLOBAL)){
                globalRadioButton = selectedScopeType;
                scopeTypeSelectorPanel.add(globalRadioButton);
            } else if (scopeTypeEnum.equals(ScopeType.DECISION)){
                decisionRadioButton = selectedScopeType;
                scopeTypeSelectorPanel.add(decisionRadioButton);
            }
        }
        scopeTypePanel.add(scopeTypeSelectorPanel);


        boolean couldBeGlobal = true;
        boolean couldBeDecision = true;

        if(probNet.getNodes(NodeType.DECISION).size() == 0){
            couldBeDecision = false;
        }

        if (!couldBeDecision || !couldBeGlobal){
            if(couldBeGlobal){
                //scopeTypeSelector.setSelectedItem(stringDatabase.getString(ScopeType.GLOBAL.toString()));
                globalRadioButton.setSelected(true);
                setScopeType(ScopeType.GLOBAL);
            } else {
                //scopeTypeSelector.setSelectedItem(stringDatabase.getString(ScopeType.DECISION.toString()));
                decisionRadioButton.setSelected(true);
                setScopeType(ScopeType.DECISION);
            }
            for (Component component : scopeTypePanel.getComponents()) {
                component.setEnabled(false);
            }
        } else {
//            scopeTypeSelector.setSelectedItem(stringDatabase.getString(ScopeType.GLOBAL.toString()));
            globalRadioButton.setSelected(true);
            setScopeType(ScopeType.GLOBAL);

        }

        return scopeTypePanel;
    }

    public JPanel getDecisionSelectorPanel(){

        decisionSelectorPanel = new JPanel();
        decisionSelectorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        decisionSelectorPanel.add(new JLabel(stringDatabase.getString("ScopeSelector.DecisionSelector")));

        decisionSelector = new JComboBox<>();
        for(Node node : probNet.getNodes(NodeType.DECISION)){
            decisionSelector.addItem(node.getName());
        }
        decisionSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemSelected = (String) ((JComboBox) e.getSource()).getSelectedItem();
                try {
                    setDecisionSelected(probNet.getVariable(itemSelected));
                } catch (NodeNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });
        decisionSelector.setSelectedIndex(0);
        decisionSelectorPanel.add(decisionSelector);

        if(scopeType.equals(ScopeType.GLOBAL)){
            setDecisionSelected(null);
            for(Component component : decisionSelectorPanel.getComponents()){
                component.setEnabled(false);
            }
        } else {
            try {
                decisionSelected = probNet.getVariable(decisionSelector.getSelectedItem().toString());
            } catch (NodeNotFoundException e) {
                e.printStackTrace();
            }
        }

        return decisionSelectorPanel;
    }

    public JPanel getDecisionScenarioPanel(){
        decisionScenarioPanel = new JPanel();
        decisionScenarioPanel.setLayout(new BoxLayout(decisionScenarioPanel, BoxLayout.PAGE_AXIS));
        decisionScenarioPanel.setBorder(new TitledBorder(stringDatabase.getString("ScopeSelector.Scenario")));

        if(decisionSelected != null && scopeType == ScopeType.DECISION){
            selectedScenario = new HashMap<>();
            this.selectedFindings = new ArrayList<>();

            List<Variable> informationalPredecessors = ProbNetOperations.getInformationalPredecessors(probNet, decisionSelected);
            for (Variable variable : informationalPredecessors) {
                JPanel informationalPredecessorPanel = new JPanel();
                informationalPredecessorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                if (!variable.equals(decisionSelected)) {
                    informationalPredecessorPanel.add(new JLabel(variable.getName()));
                    JComboBox<String> stateSelector = new JComboBox<>();
                    for(State state : variable.getStates()){
                        stateSelector.addItem(state.getName());
                    }
                    selectedScenario.put(stateSelector, variable);

                    stateSelector.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JComboBox<String> stateSelector = (JComboBox<String>) e.getSource();
                            String selectedStateString = stateSelector.getSelectedItem().toString();
                            Variable selectedVariable = selectedScenario.get(stateSelector);
                            try {
                                State selectedState = selectedVariable.getState(selectedStateString);
                                updateSelectedScenario();
                            } catch (InvalidStateException e1) {
                                e1.printStackTrace();
                            }

                        }
                    });

                    informationalPredecessorPanel.add(stateSelector);
                }
                decisionScenarioPanel.add(informationalPredecessorPanel);
            }
            updateSelectedScenario();
        }

        return decisionScenarioPanel;
    }

    private void updateSelectedScenario() {
        List<Finding> selectedFindings = new ArrayList<>();

        for(JComboBox<String> comboBox : selectedScenario.keySet()){
            Variable variable = selectedScenario.get(comboBox);
            try {
                Finding finding = new Finding(variable, variable.getState(comboBox.getSelectedItem().toString()));
                selectedFindings.add(finding);
            } catch (InvalidStateException e) {
                e.printStackTrace();
            }
        }

        this.selectedFindings = selectedFindings;
    }

    private void refreshScenario() {
        mainPanel.setVisible(false);
        if(decisionScenarioPanel != null) {
            mainPanel.remove(decisionScenarioPanel);
            mainPanel.add(getDecisionScenarioPanel());
        }
        mainPanel.setVisible(true);
    }

    public void setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public void setDecisionSelected(Variable decisionSelected) {
        this.decisionSelected = decisionSelected;
        refreshScenario();
    }

    public Variable getDecisionSelected() {
        return decisionSelected;
    }

    public List<Finding> getSelectedFindings() {
        return selectedFindings;
    }
}