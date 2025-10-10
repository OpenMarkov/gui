/* Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.action;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import java.util.LinkedHashMap;

/**
 * {@code OtherPropertyEdit} is a simple edit that allow modify the additional properties of
 * one node.
 *
 * @author agoni
 */

public class OtherPropertyEdit extends SimplePNEdit {
	/**
	 *
	 */
	private static final long serialVersionUID = 4325259909756103849L;

	/**
	 * The last properties before the edition
	 */
	private LinkedHashMap<String, String> oldProperties;
	/**
	 * The properties after the the edition
	 */
	private LinkedHashMap<String, String> newProperties;
	/**
	 * The new property
	 */
	private String[] newProperty;
	/**
	 * The action to carry out
	 */
	private String otherPropertyAction;
	/**
	 * the index (in the table) associated to the property to edit
	 */
	private int propertyIndex;
	/**
	 * The node that the property belongs to
	 */
	private Node node = null;
	/**
	 * The network that the property belongs to
	 */
	//private ProbNet probNet = null;
	
	//
	///**
	// * The last property before the edition
	// */
	//private String[] oldProperty = new String[] {};
	////private Object[] oldProperty = new Object[] {};
	///**
	// * The modified property
	// */
	//private String[] modifiedProperty;
	///**
	// * index of the property selected in the view
	// */
	//private int selectedPropertyIndex;	
	//
	////private String[] newData;	
	//
	////private String[] oldData;	

	/**
	 * Creates a new {@code OtherPropertyEdit} to carry out the specified
	 * action on the specified property.
	 *
	 * @param node        the node that will be edited.
	 * @param otherPropertyAction the action to carry out
	 * @param propertyIndex  the index (in the table) associated to the property to edit
	 * @param newName     a new string for the property edited if the action is ADD.
	 */
	public OtherPropertyEdit(Node node, String otherPropertyAction, int propertyIndex, String[] newData) {
		super(node.getProbNet());
		this.node = node;
		//this.probNet = node.getProbNet();
		oldProperties = node.getOtherProperties();
		this.propertyIndex = propertyIndex;
		this.newProperty = (otherPropertyAction == "ADD") ? newData : null;
		this.otherPropertyAction = otherPropertyAction;
	}
	
	/**
	 * Creates a new {@code OtherPropertyEdit} to carry out the specified
	 * action on the specified property.
	 *
	 * @param probNet        network.
	 * @param otherPropertyAction the action to carry out
	 * @param propertyIndex  the index (in the table) associated to the property to edit
	 * @param newName     a new string for the property edited if the action is ADD.
	 */
	public OtherPropertyEdit(ProbNet probNet, String otherPropertyAction, int propertyIndex, String[] newData) {
		super(probNet);
		this.probNet = probNet;
		oldProperties = probNet.getOtherProperties();
		this.propertyIndex = propertyIndex;
		this.newProperty = (otherPropertyAction == "ADD") ? newData : null;
		this.otherPropertyAction = otherPropertyAction;
	}
	
	@Override public void doEdit() {
		switch (otherPropertyAction) {
		case "ADD":
			// Copy other properties
			newProperties = new LinkedHashMap<String, String>();
			for (String key : oldProperties.keySet()) {
				newProperties.put(key, oldProperties.get(key));
			}
			
			// Add new property
			newProperties.put(newProperty[0], newProperty[1]);
			
			if (node != null) {
				// Update other properties in node
				node.setOtherProperties(newProperties);
			} else if (probNet != null) {
				// Update other properties in network
				probNet.setOtherProperties(newProperties);
			}
			
			break;
		case "REMOVE":
			// Copy other properties
			newProperties = new LinkedHashMap<String, String>();
			for (String key : oldProperties.keySet()) {
				newProperties.put(key, oldProperties.get(key));
			}
			
			// Remove selected property
			String key = oldProperties.keySet().toArray()[propertyIndex].toString();
			newProperties.remove(key);
			
			if (node != null) {
				// Update other properties in node
				node.setOtherProperties(newProperties);
			} else if (probNet != null) {
				// Update other properties in network
				probNet.setOtherProperties(newProperties);
			}
			
			break;
		case "RENAME":
			// Get original key of selected property
			String oldKey = oldProperties.keySet().toArray()[propertyIndex].toString();
			
			// Copy other properties
			newProperties = new LinkedHashMap<String, String>();
			for (String key1 : oldProperties.keySet()) {
				if (key1 == oldKey) {
					newProperties.put(newProperty[0], newProperty[1]);
				} else {
					newProperties.put(key1, oldProperties.get(key1));
				}
			}
			
			if (node != null) {
				// Update other properties in node
				node.setOtherProperties(newProperties);
			} else if (probNet != null) {
				// Update other properties in network
				probNet.setOtherProperties(newProperties);
			}
			break;
		case "DOWN":
			// Get original key of selected property
			String oldKey1 = oldProperties.keySet().toArray()[propertyIndex].toString();
			String oldKey2 = oldProperties.keySet().toArray()[propertyIndex+1].toString();
			
			// Copy other properties
			newProperties = new LinkedHashMap<String, String>();
			for (String key1 : oldProperties.keySet()) {
				if (key1 == oldKey1) {
					newProperties.put(oldKey2, oldProperties.get(oldKey2));
				} else if (key1 == oldKey2) {
					newProperties.put(oldKey1, oldProperties.get(oldKey1));
				} else {
					newProperties.put(key1, oldProperties.get(key1));
				}
			}
						
			if (node != null) {
				// Update other properties in node
				node.setOtherProperties(newProperties);
			} else if (probNet != null) {
				// Update other properties in network
				probNet.setOtherProperties(newProperties);
			}
			break;
		case "UP":
			// Get original key of selected property
			oldKey1 = oldProperties.keySet().toArray()[propertyIndex-1].toString();
			oldKey2 = oldProperties.keySet().toArray()[propertyIndex].toString();
						
			// Copy other properties
			newProperties = new LinkedHashMap<String, String>();
			for (String key1 : oldProperties.keySet()) {
				if (key1 == oldKey1) {
					newProperties.put(oldKey2, oldProperties.get(oldKey2));
				} else if (key1 == oldKey2) {
					newProperties.put(oldKey1, oldProperties.get(oldKey1));
				} else {
					newProperties.put(key1, oldProperties.get(key1));
				}
			}
									
			if (node != null) {
				// Update other properties in node
				node.setOtherProperties(newProperties);
			} else if (probNet != null) {
				// Update other properties in network
				probNet.setOtherProperties(newProperties);
			}
			break;
		}
	}
	
	@Override public void doEdit(ProbNet probNet) throws DoEditException.ConstraintViolated {
        this.checkConstraintsWillBeMet();
		PNEdit.startEdit(this, probNet);
		this.doEdit();
		PNEdit.endEdit(this);
	}
	
	@Override public void undo() {
		super.undo();
		if (node != null) {
			node.setOtherProperties(oldProperties);
		} else if (probNet != null) {
			probNet.setOtherProperties(oldProperties);
		}
	}

	// TODO redo() implementation

	public Node getNode() {
		return node;
	}

}
