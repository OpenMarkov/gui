package org.openmarkov.core.gui.network;

import org.openmarkov.core.model.network.ProbNet;


/**
 * This interface is used by a class that wants to know when a network is
 * changed.
 * 
 * @author jmendoza
 */
public interface NetworkChangeListener {

	/**
	 * This method notifies that a network has been changed.
	 * 
	 * @param network
	 *            changed network.
	 */
	public void networkChanged(ProbNet network);
}
