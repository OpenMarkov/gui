package org.openmarkov.gui.constraint;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;

import java.util.List;

public class AbsorbNodeValidator {
    // The node is of decision or chance and has only a utility child
    public static boolean validate(Node node) {

        // Test if decision or chance
        boolean isDecisionOrChance = node.getNodeType() == NodeType.CHANCE || node.getNodeType() == NodeType.DECISION;
        if (!isDecisionOrChance) {
            return false;
        }

        // Test if only utility children and no grandchildren
        List<Node> children = node.getChildren();
        switch (children.size()) {
            case 0:
                return false;
            default:
                for (Node child : children) {
                    if (child.getNodeType() != NodeType.UTILITY || child.getChildren().size() > 0) {
                        return false;
                    }
                }
                // Only utility children, no grandchildren
                return true;

        }

    }
}
