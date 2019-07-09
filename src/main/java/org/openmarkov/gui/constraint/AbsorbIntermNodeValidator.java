/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.constraint;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;

import java.util.List;

/******
 * This class validates the necessary requirements of a node to allow absorbing intermediate parents.
 * Then returns true or false depending on the case. True means the menu item will be enabled and false that it
 * will be disabled.
 * TODO: Rewrite after modifying it.
 *
 * @author iagoparís - summer 2019
 *
 */
public class AbsorbIntermNodeValidator {
    // The node is of decision or chance and has only a utility child
    public static boolean validate(Node node) {

        // TODO: Put the code to validate here
        return true;

    }
}
