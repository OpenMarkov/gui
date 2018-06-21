package org.openmarkov.gui.constraint;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;

import java.util.List;

public class PruneNodeValidator {
    // The node is of decision or chance and has only a utility child
    public static boolean validate(Node node) {
        boolean isDecisionOrChance = node.getNodeType() == NodeType.CHANCE || node.getNodeType() == NodeType.DECISION;
        List<Node> children = node.getChildren();

        try {
            // TODO NodeType.UTILITY still working?
            return (isDecisionOrChance && children.size() <= 1 && children.get(0).getNodeType() == NodeType.UTILITY);
        } catch (IndexOutOfBoundsException e) {
            // The exception means that the node has no children, so it can be pruned if is a chance one or a decision one.
            return isDecisionOrChance;
        }
    }
}
