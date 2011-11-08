package openmarkov.core.gui.development.dialog;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import openmarkov.core.gui.OpenMarkov;
import openmarkov.core.gui.utils.Util;


/**
 * Class to show an About Box window for the OpenMarkov Development Environment
 * 
 * @author jlgozalo
 * @version 1.0 17/03/2009
 */
public class AboutBoxDevEnv extends JDialog implements ActionListener {

	private AboutBoxDevEnv anAboutBox = null;
	static final String product = "Development Environment for OpenMarkov";
	static final String version = "Version 1.0";
	static final String copyright = " (c) UNED - 2008-2009";
	static final String copyright2 = "All rights reserved";
	static final String authors = "J.L.Gozalo";
	static final String advertisement =
		"Please visit OpenMarkov Web pages for the latest update";
	static final String trademark = "OpenMarkov is a registered product";
	static final String openMarkovLogoImage =
		Util.getResourcesPath() + "\\images\\wizard_hat_green.jpeg";
	static final String lineSeparatorImage =
			Util.getResourcesPath() + "\\images\\lineSeparator.jpg";
	BorderLayout borderLayoutAboutBox = new BorderLayout();
	JPanel jPanelAboutText = new JPanel();
	JPanel jPanelAboutButton = new JPanel();
	ImageIcon openMarkovLogo = new ImageIcon();
	ImageIcon lineSeparator = new ImageIcon();
	JLabel jLabelLogo = new JLabel();
	JLabel jLabelProduct = new JLabel();
	JLabel jLabelVersion = new JLabel();
	JLabel jLabelCopyright = new JLabel();
	JLabel jLabelCopyright2 = new JLabel();
	JLabel jLabelAuthors = new JLabel();
	JLabel jLabelLineSeparators = new JLabel();
	JLabel jLabelAdvertisement = new JLabel();
	JLabel jLabelTrademark = new JLabel();
	JButton jButtonOK = new JButton();
	GridLayout gridLayoutText = new GridLayout();
	FlowLayout flowLayoutButtons = new FlowLayout();

	/**
	 * singleton for AboutBoxDevEnv
	 * 
	 * @return anAboutBox dialog
	 */
	public AboutBoxDevEnv getUniqueInstance() {

		return getUniqueInstance(null);
	}

	/**
	 * singleton for AboutBoxDevEnv
	 * 
	 * @param parent
	 *            the parent for the AboutBoxDevEnv frame
	 * @return anAboutBox dialog
	 */
	public AboutBoxDevEnv getUniqueInstance(JFrame parent) {

		if (anAboutBox == null) { // singleton
			new AboutBoxDevEnv(parent);
		} else { // it is already created and not visible
			this.setVisible(true);
		}
		return anAboutBox;
	}

	/**
	 * constructor on a parent JFrame
	 * 
	 * @param parent
	 */
	public AboutBoxDevEnv(JFrame parent) {

		super(parent, "About OpenMarkov Dev Environemnt v.1.0", true);
		try {
			this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
			jbInit();
			this.setVisible(true);
			anAboutBox = this;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * constructor on a open window
	 */
	public AboutBoxDevEnv() {

		super((JFrame) null, "About OpenMarkov Environemnt v.1.0", true);
		try {
			this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
			jbInit();
			this.setVisible(true);
			anAboutBox = this;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Component initialization.
	 * 
	 * @throws Exception
	 *             if any problem on components initialization
	 */
	private void jbInit() throws Exception {

		try {
			// look for the images to show in the box
			openMarkovLogo =
				new ImageIcon(OpenMarkov.class.getResource(openMarkovLogoImage));
			lineSeparator =
				new ImageIcon(OpenMarkov.class
					.getResource(lineSeparatorImage));
			// put the title of the box
			this.setTitle(product);
			// mark the layout and the size for the About box
			this.getContentPane().setLayout(borderLayoutAboutBox);
			this.setSize(openMarkovLogo.getIconWidth() + 300, openMarkovLogo
				.getIconHeight() + 200);
			// set the logo and add to the top of the box
			jLabelLogo.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelLogo.setHorizontalTextPosition(SwingConstants.CENTER);
			jLabelLogo.setIcon(openMarkovLogo);
			jLabelLogo.setText("");
			this.getContentPane().add(jLabelLogo, java.awt.BorderLayout.NORTH);
			// set the items and add to the panel and then to the box
			setTextInLabelAligned(jLabelProduct, product, JLabel.CENTER);
			setTextInLabelAligned(jLabelVersion, version, JLabel.CENTER);
			jLabelVersion.setName("jLabelVersion");
			setTextInLabelAligned(jLabelCopyright, copyright, JLabel.CENTER);
			setTextInLabelAligned(jLabelCopyright2, copyright2, JLabel.CENTER);
			setTextInLabelAligned(jLabelLineSeparators, "", JLabel.CENTER);
			jLabelLineSeparators.setIcon(lineSeparator);
			setTextInLabelAligned(
				jLabelAdvertisement, advertisement, JLabel.LEFT);
			setTextInLabelAligned(jLabelTrademark, trademark, JLabel.LEFT);
			jPanelAboutText.setLayout(gridLayoutText);
			gridLayoutText.setColumns(1);
			gridLayoutText.setRows(8);
			jPanelAboutText.add(jLabelProduct);
			jPanelAboutText.add(jLabelVersion);
			jLabelVersion.setText(version);
			jPanelAboutText.add(jLabelCopyright);
			jPanelAboutText.add(jLabelCopyright2);
			jPanelAboutText.add(jLabelAuthors);
			jPanelAboutText.add(jLabelLineSeparators);
			jPanelAboutText.add(jLabelAdvertisement);
			jPanelAboutText.add(jLabelTrademark);
			this.getContentPane().add(
				jPanelAboutText, java.awt.BorderLayout.CENTER);
			// set the OK button and action associated
			jButtonOK.setText("OK");
			jButtonOK.addActionListener(this);
			jPanelAboutButton.setLayout(flowLayoutButtons);
			jPanelAboutButton.add(jButtonOK);
			this.getContentPane().add(
				jPanelAboutButton, java.awt.BorderLayout.SOUTH);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			throw e;
		}
	}

	/**
	 * Close the dialog on a button event. Really, this action only hides the
	 * dialog to be reused if required.
	 * 
	 * @param actionEvent
	 *            ActionEvent
	 */
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getActionCommand().equals("OK")) {
			// this.dispose();
			this.setVisible(false);
		}
	}

	private static void setTextInLabelAligned(JLabel theLabel, String theText,
												int alignment) {

		theLabel.setHorizontalAlignment(alignment);
		theLabel.setText(theText);
	}
}
