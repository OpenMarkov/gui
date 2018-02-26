/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.component;

import org.openmarkov.core.model.network.Node;

public interface TableMethods {

	public int getPotentialIndex(int row, int column, Node node);

	public int calculateFirstEditableRow(Node properties);

	public int calculateLastEditableRow(Node properties);

}
