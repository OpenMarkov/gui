/**
 * 
 */
package openmarkov.core.gui.dialog.configuration;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;


/**
 * PreferenceTreeNode is defining the node behaviours in a Preferences Tree
 * @author jlgozalo
 * @version 1.0
 * 28 Aug 2009
 *
 */
	public class PreferenceTreeNode extends DefaultMutableTreeNode {

		Preferences pref;
		String nodeName;
		String[] childrenNames;

		public PreferenceTreeNode(Preferences pref) throws BackingStoreException {

			this.pref = pref;
			childrenNames = pref.childrenNames();
		}

		public Preferences getPrefObject() {

			return pref;
		}

		public boolean isLeaf() {

			return ((childrenNames == null) || (childrenNames.length == 0));
		}

		public int getChildCount() {

			return childrenNames.length;
		}

		public TreeNode getChildAt(int childIndex) {

			if (childIndex < childrenNames.length) {
				try {
					PreferenceTreeNode child =
						new PreferenceTreeNode(pref.node(childrenNames[childIndex]));
					return child;
				} catch (BackingStoreException e) {
					e.printStackTrace();
					return new DefaultMutableTreeNode("Problem Child!");
				}
			}
			return null;
		}

		public String toString() {

			String name = pref.name();
			if ((name == null) || ("".equals(name))) { // if root node
				name = "System Preferences";
				if (pref.isUserNode())
					name = "User Preferences";
			}
			return name;
		}
	}

