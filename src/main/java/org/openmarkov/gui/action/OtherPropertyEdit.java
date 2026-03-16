/* Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.action;

import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * {@code OtherPropertyEdit} is a simple edit that allow modify the additional properties of
 * one node.
 *
 * @author agoni
 */

public class OtherPropertyEdit extends PNEdit {

	private static final long serialVersionUID = 4325259909756103849L;
	
	private LinkedHashMap<String, String> oldProperties;

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
		this.oldProperties = node.getOtherProperties();
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
	 */
	public OtherPropertyEdit(ProbNet probNet, String otherPropertyAction, int propertyIndex, String[] newData) {
		super(probNet);
		this.probNet = probNet;
		this.oldProperties = probNet.getOtherProperties();
		this.propertyIndex = propertyIndex;
		this.newProperty = (otherPropertyAction == "ADD") ? newData : null;
		this.otherPropertyAction = otherPropertyAction;
	}
	
	@Override protected void doEdit() {
		switch (this.otherPropertyAction) {
			case "ADD" -> {
				// Copy other properties
				this.newProperties = new LinkedHashMap<>();
				this.newProperties.putAll(this.oldProperties);
				// Add new property
				this.newProperties.put(this.newProperty[0], this.newProperty[1]);
			}
			case "REMOVE" -> {
				// Copy other properties
				this.newProperties = new LinkedHashMap<>();
				this.newProperties.putAll(this.oldProperties);
				// Remove selected property
				String key = this.oldProperties.keySet().toArray()[this.propertyIndex].toString();
				this.newProperties.remove(key);
			}
			case "RENAME" -> {
				// Get original key of selected property
				String oldKey = this.oldProperties.keySet().toArray()[this.propertyIndex].toString();
				// Copy other properties
				this.newProperties = new LinkedHashMap<>();
				for (Map.Entry<String, String> entry : this.oldProperties.entrySet()) {
					if (Objects.equals(entry.getKey(), oldKey)) {
						this.newProperties.put(this.newProperty[0], this.newProperty[1]);
					} else {
						this.newProperties.put(entry.getKey(), entry.getValue());
					}
				}
			}
			case "DOWN" -> {
				// Get original key of selected property
				String oldKey1 = this.oldProperties.keySet().toArray()[this.propertyIndex].toString();
				String oldKey2 = this.oldProperties.keySet().toArray()[this.propertyIndex + 1].toString();
				
				// Copy other properties
				this.newProperties = new LinkedHashMap<>();
				for (Map.Entry<String, String> entry : this.oldProperties.entrySet()) {
					if (entry.getKey() == oldKey1) {
						this.newProperties.put(oldKey2, this.oldProperties.get(oldKey2));
					} else if (entry.getKey() == oldKey2) {
						this.newProperties.put(oldKey1, this.oldProperties.get(oldKey1));
					} else {
						this.newProperties.put(entry.getKey(), entry.getValue());
					}
				}
			}
			case "UP" -> {
				// Get original key of selected property
				String oldKey1 = this.oldProperties.keySet().toArray()[this.propertyIndex - 1].toString();
				String oldKey2 = this.oldProperties.keySet().toArray()[this.propertyIndex].toString();
				// Copy other properties
				this.newProperties = new LinkedHashMap<>();
				for (String key1 : this.oldProperties.keySet()) {
					if (key1 == oldKey1) {
						this.newProperties.put(oldKey2, this.oldProperties.get(oldKey2));
					} else if (key1 == oldKey2) {
						this.newProperties.put(oldKey1, this.oldProperties.get(oldKey1));
					} else {
						this.newProperties.put(key1, this.oldProperties.get(key1));
					}
				}
			}
		}
		
		setProperties(this.newProperties);
	}
	
	
	@Override public void undo() {
		setProperties(this.oldProperties);
	}
	
	@Override public void redo() {
		setProperties(this.newProperties);
	}
	
	private void setProperties(LinkedHashMap<String, String> newProperties) {
		if (this.node != null) {
			// Update other properties in node
			this.node.setOtherProperties(newProperties);
		} else if (this.probNet != null) {
			// Update other properties in network
			this.probNet.setOtherProperties(newProperties);
		}
	}

}
