package org.openmarkov.gui.constraint;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;

import java.util.List;

public class AbsorbNodeValidator {
    // The node is of decision or chance and has only a utility child
    public static boolean validate(Node node) {
        boolean isDecisionOrChance = node.getNodeType() == NodeType.CHANCE || node.getNodeType() == NodeType.DECISION;
        List<Node> children = node.getChildren();

        // If no children, second expression is false and so third expression is not evaluated,
        // avoiding IndexOutOfBound on children.get(0).
        return (isDecisionOrChance && children.size() == 1 && children.get(0).getNodeType() == NodeType.UTILITY);

    }
}
