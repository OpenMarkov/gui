package org.openmarkov.gui.util;

import org.openmarkov.core.dt.DecisionTreeBranch;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.model.network.NodeType;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TreeNodeToDot {

    private final String C_decisionColor = "#cfe3fd";
    private final String C_chanceColor = "#fbf999";
    private final String C_utilityColor = "#d0e6b2";

    private class DotNode {
        private String label;
        private int number;
        private NodeType type;


        public DotNode(int number, String label, NodeType type) {
            this.label = label;
            this.number = number;
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public NodeType getType() {
            return type;
        }

        public void setType(NodeType type) {
            this.type = type;
        }

        private String getShape() {
            if (this.type.equals(NodeType.CHANCE)) {
                return "shape = \"oval\", color=\"" + C_chanceColor + "\"";
            } else if (this.type.equals(NodeType.DECISION)) {
                return "shape = \"box\", color=\"" + C_decisionColor + "\"";
            } else {
                return "shape = \"hexagon\", color=\"" + C_utilityColor + "\"";
            }
        }

        @Override
        public String toString() {
            return this.number + " [label=\"" + this.label + "\", " + getShape() + "];";
        }
    }

    private class DotLink {
        private DotNode sourceNode;
        private DotNode destinationNode;
        private String label;

        public DotLink(DotNode sourceNode, DotNode destinationNode, String label) {
            this.sourceNode = sourceNode;
            this.destinationNode = destinationNode;
            this.label = label;
        }

        public DotNode getSourceNode() {
            return sourceNode;
        }

        public void setSourceNode(DotNode sourceNode) {
            this.sourceNode = sourceNode;
        }

        public DotNode getDestinationNode() {
            return destinationNode;
        }

        public void setDestinationNode(DotNode destinationNode) {
            this.destinationNode = destinationNode;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return this.sourceNode.getNumber() + " -> " + this.destinationNode.getNumber() + " [label=\"" + this.label + "\"];";
        }
    }

    List<DotNode> dotNodes = new ArrayList<>();
    List<DotLink> dotLinks = new ArrayList<>();

    private int numNode = 0;
    private DecimalFormat df = new DecimalFormat();
    private int graphDPI;

    public TreeNodeToDot() {
        graphDPI = 300;
        setNumDecimals(4);
    }

    public void paintDTNode(DecisionTreeNode treeNode) {
        List<DecisionTreeNode> children = new ArrayList<>();
        children.add(treeNode);

        DotNode sourceNode = new DotNode(numNode, treeNode.getVariable().getName(), treeNode.getNodeType());
        numNode += 1;
        dotNodes.add(sourceNode);

        parseTreeNode(sourceNode, treeNode);

        StringBuilder graph = new StringBuilder();
        graph.append("digraph G {" + "\n");
        graph.append("\tgraph [dpi = " + graphDPI + "];\n");
        graph.append("\trankdir=LR;\n");
        graph.append("\tnode [style=\"filled\"]; \n");
        for (DotNode node : dotNodes) {
            graph.append("\t" + node.toString() + "\n");
        }

        for (DotLink dotLink : dotLinks) {
            graph.append("\t" + dotLink + "\n");
        }
        graph.append("}");

        System.out.println(graph.toString());

        JFileChooser chooser = new JFileChooser();
        int retrival = chooser.showSaveDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".gv")) {
                fw.write(graph.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseTreeNode(DotNode sourceNode, DecisionTreeNode treeNode) {

        // Analyze the branches of that node
        for (DecisionTreeElement elements : treeNode.getChildren()) {
            DecisionTreeBranch branch = (DecisionTreeBranch) elements;
            String linkLabel = null;
            if (treeNode.getNodeType().equals(NodeType.CHANCE)) {
                linkLabel = branch.getBranchState().getName() + " / P=" + df.format(branch.getBranchProbability()) +
                        " / U=" + df.format(branch.getUtility());
            } else if (treeNode.getNodeType().equals(NodeType.DECISION)) {
                linkLabel = branch.getBranchState().getName() + " / U=" + df.format(branch.getUtility());
            } else if (treeNode.getNodeType().equals(NodeType.UTILITY)) {
                // Code reachable only when SV-nodes
                linkLabel = branch.getBranchState().getName() + " / P=" + df.format(branch.getBranchProbability()) +
                        " / U=" + df.format(branch.getUtility());
            }

            DecisionTreeNode childNode = branch.getChild();
            DotNode destinationNode = new DotNode(numNode, childNode.getVariable().getName(), childNode.getNodeType());
            numNode += 1;
            dotNodes.add(destinationNode);
            dotLinks.add(new DotLink(sourceNode, destinationNode, linkLabel));

            parseTreeNode(destinationNode, childNode);
        }

    }

    public void setNumDecimals(int numDecimals) {
        df.setMaximumFractionDigits(numDecimals);
    }

    public void setGraphDPI(int graphDPI) {
        this.graphDPI = graphDPI;
    }
}
