package openmarkov.core.gui.dialog.common;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;


/**
 * This class implements a dialog that has a vertical button panel in the right
 * side of the window.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class RightPanelButtonDialog extends DialogBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7080409336122861616L;

	/**
	 * Content pane.
	 */
	private JPanel jContentPane = null;

	/**
	 * Panel where the rest of the components, except the buttons of the
	 * vertical line are placed.
	 */
	private JPanel componentsPanel = null;

	/**
	 * Panel that contains the button panel.
	 */
	private JPanel rightPanel = null;

	/**
	 * Panel that contains the buttons.
	 */
	private JPanel buttonsPanel = null;

	/**
	 * Constructor that invokes the superclass' constructor and initialises the
	 * instance.
	 * 
	 * @param owner
	 *            window that owns the dialog box.
	 */
	public RightPanelButtonDialog(Window owner) {

		super(owner);

		initialize();

	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		setSize(new Dimension(150, 100));
		setResizable(false);
		setModal(true);
		setContentPane(getJContentPane());

	}

	/**
	 * This method initialises jContentPane.
	 * 
	 * @return a new content panel.
	 */
	private JPanel getJContentPane() {

		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(),
				BoxLayout.X_AXIS));
			jContentPane.add(getComponentsPanel());
			jContentPane.add(getRightPanel());
		}

		return jContentPane;

	}

	/**
	 * This method initialises componentsPanel.
	 * 
	 * @return a new components panel.
	 */
	protected JPanel getComponentsPanel() {

		if (componentsPanel == null) {
			componentsPanel = new JPanel();
		}

		return componentsPanel;

	}

	/**
	 * This method initialises rightPanel.
	 * 
	 * @return a new right panel.
	 */
	private JPanel getRightPanel() {

		if (rightPanel == null) {
			rightPanel = new JPanel();
			rightPanel.setLayout(new BorderLayout());
			rightPanel.add(getButtonsPanel(), BorderLayout.NORTH);
		}

		return rightPanel;

	}

	/**
	 * This method initialises buttonsPanel.
	 * 
	 * @return a new panel that contains the buttons.
	 */
	protected JPanel getButtonsPanel() {

		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new GridLayout(0, 1, 5, 5));
			buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}

		return buttonsPanel;

	}

	/**
	 * This method adds a new button to the buttons panel and a space of 10
	 * units to the right of this button.
	 * 
	 * @param button
	 *            button that will be added to the panel.
	 */
	protected void addButtonToButtonsPanel(JButton button) {

		buttonsPanel.add(button);

	}
}
