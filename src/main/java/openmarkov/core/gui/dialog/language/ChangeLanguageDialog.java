package openmarkov.core.gui.dialog.language;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import openmarkov.core.gui.loader.element.OpenMarkovLogoIcon;
import openmarkov.core.gui.localize.Languages;
import openmarkov.core.gui.localize.LocaleChangeEvent;
import openmarkov.core.gui.localize.StringResource;
import openmarkov.core.gui.localize.StringResourceLoader;
import openmarkov.core.gui.localize.StringResourceLocaleChangeListener;
import openmarkov.core.gui.utils.OpenMarkovPreferences;



/**
 * Change Language Dialog to set the language of the application
 * 
 * @author jlgozalo
 * @version 1.0 26 Jun 2009
 */
public class ChangeLanguageDialog extends JDialog implements
				StringResourceLocaleChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * singleton
	 */
	private static ChangeLanguageDialog changeLanguageDialog = null;

	/**
	 * components of the dialog
	 */
	private JComboBox jComboBoxLanguages;
	private JLabel jLabelLanguageChoice;
	private JTextArea jTextAreaInstructions;
	private JButton jButtonAccept;
	private JButton jButtonCancel;
	private JButton jButtonApply;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource = null;
	/**
	 * to store temporally the old language to set
	 */
	private String oldLanguage;
	
	
	private Logger logger;

	/**
	 * singleton for ChangeLanguageDialog
	 * 
	 * @return a ChangeLanguageDialog dialog
	 */
	public static ChangeLanguageDialog getUniqueInstance() {

		return getUniqueInstance( null );
	}

	/**
	 * singleton for ChangeLanguageDialog
	 * 
	 * @param parent
	 *            the parent for the ChangeLanguageDialog frame
	 * @return a ChangeLanguageDialog dialog
	 */
	public static ChangeLanguageDialog getUniqueInstance(JFrame parent) {

		if (changeLanguageDialog == null) { // singleton
			changeLanguageDialog = new ChangeLanguageDialog( parent );
		}
		changeLanguageDialog.setVisible( true );
		return changeLanguageDialog;
	}

	/**
	 * constructor on a parent JFrame
	 * 
	 * @param parent
	 */
	public ChangeLanguageDialog(JFrame parent) {

		super( parent, "", true );
		setName( "ChangeLanguageDialog" );
		this.logger = Logger.getLogger(ChangeLanguageDialog.class);
		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		this.oldLanguage =
			StringResourceLoader.getUniqueInstance().getLanguage();
		StringResourceLoader.getUniqueInstance()
			.addStringResourceLocaleChangeListener( this );
		try {
			this.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
			initialize();
			this.setVisible( true );
			changeLanguageDialog = this;
		} catch (Exception e) {
			//ExceptionsHandler.handleException( e, null, true );
			logger.fatal(e);
		}
	}

	/**
	 * initialize the dialog
	 * 
	 * @throws Exception
	 */
	private void initialize() throws Exception {

		final GroupLayout groupLayout = new GroupLayout( getContentPane() );
		groupLayout.setHorizontalGroup( groupLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING ).addGroup(
			groupLayout.createSequentialGroup()
				.addGroup(
					groupLayout.createParallelGroup(
						GroupLayout.Alignment.LEADING ).addGroup(
						GroupLayout.Alignment.TRAILING,
						groupLayout.createSequentialGroup().addContainerGap()
							.addComponent(
								getJLabelLanguageChoice(),
								GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE )
							.addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED )
							.addComponent(
								getJComboBoxLanguages(),
								GroupLayout.PREFERRED_SIZE, 234,
								GroupLayout.PREFERRED_SIZE ) ).addGroup(
						groupLayout.createSequentialGroup().addGap(
							102, 102, 102 ).addComponent( getJButtonAccept() )
							.addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED )
							.addComponent(
								getJButtonCancel(), GroupLayout.PREFERRED_SIZE,
								96, GroupLayout.PREFERRED_SIZE )
							.addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED )
							.addComponent( getJButtonApply() ) )
						.addGroup(
							groupLayout.createSequentialGroup()
								.addContainerGap().addComponent(
									getJTextAreaInstructions(),
									GroupLayout.DEFAULT_SIZE, 447,
									Short.MAX_VALUE ) ) ).addContainerGap() ) );
		groupLayout.setVerticalGroup( groupLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING )
			.addGroup(
				groupLayout.createSequentialGroup().addContainerGap().addGroup(
					groupLayout.createParallelGroup(
						GroupLayout.Alignment.LEADING, false ).addComponent(
						getJLabelLanguageChoice(), GroupLayout.DEFAULT_SIZE,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
						.addComponent( getJComboBoxLanguages() ) ).addGap(
					14, 14, 14 ).addComponent(
					getJTextAreaInstructions(), GroupLayout.PREFERRED_SIZE, 91,
					GroupLayout.PREFERRED_SIZE )
					.addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED, 79,
						Short.MAX_VALUE ).addGroup(
						groupLayout.createParallelGroup(
							GroupLayout.Alignment.BASELINE ).addComponent(
							getJButtonAccept() ).addComponent(
							getJButtonCancel(), GroupLayout.PREFERRED_SIZE, 25,
							GroupLayout.PREFERRED_SIZE ).addComponent(
							getJButtonApply() ) ).addContainerGap() ) );
		groupLayout.linkSize(
			javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {
				getJButtonAccept(), getJButtonCancel(), getJButtonApply() } );
		groupLayout.linkSize(
			javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {
				getJButtonAccept(), getJButtonCancel(), getJButtonApply() } );
		getContentPane().setLayout( groupLayout );
		setTitle( dialogStringResource
			.getString( "ChangeLanguageDialog.Title.Text" ) );
		setModal( true );
		setIconImage( OpenMarkovLogoIcon.getUniqueInstance()
			.getOpenMarkovLogoIconImage16() );

		pack();

	}

	/**
	 * @return label to choice language combobox
	 */
	protected JLabel getJLabelLanguageChoice() {

		if (jLabelLanguageChoice == null) {
			jLabelLanguageChoice = new JLabel();
			jLabelLanguageChoice
				.setName( "ChangeLanguageDialog.jLabelLanguageChoice" );
			jLabelLanguageChoice.setText( "a Label" );
			jLabelLanguageChoice.setText( dialogStringResource
				.getString( "ChangeLanguageDialog.jLabelLanguageChoice.Text" ) );
		}
		return jLabelLanguageChoice;
	}

	/**
	 * @return a combo box to choice language
	 */
	protected JComboBox getJComboBoxLanguages() {

		if (jComboBoxLanguages == null) {
			jComboBoxLanguages = new JComboBox( Languages.getListStrings() );
			jComboBoxLanguages
				.setName( "ChangeLanguageDialog.jComboBoxLanguages" );
			jComboBoxLanguages.setEditable( false );
			// jComboBoxLanguages.setMaximumRowCount(2); // two languages by now
			// jComboBoxLanguages.setSelectedIndex(findLanguageIndex(this.oldLanguage));
			if (this.oldLanguage.equals( "es" )) {
				jComboBoxLanguages.setSelectedIndex( 1 );
			} else if (this.oldLanguage.equals( "en" )) {
				jComboBoxLanguages.setSelectedIndex( 0 );
			} else {
				// future languages
			}

			// special behaviour to handle i18n
			StringResourceLoader.getUniqueInstance()
				.addStringResourceLocaleChangeListener(
					new StringResourceLocaleChangeListener() {

						public void processStringResourceLocaleChange(
																		final LocaleChangeEvent event) {

							int prevSelectedIndex =
								jComboBoxLanguages.getSelectedIndex();
							jComboBoxLanguages.removeAllItems();
							Languages.resetStringResource();
							for (String item : Languages.getListStrings()) {
								jComboBoxLanguages.addItem( item );
							}
							jComboBoxLanguages
								.setSelectedIndex( prevSelectedIndex );

						}
					} );

		}
		return jComboBoxLanguages;
	}

	/**
	 * @return button accept
	 */
	protected JButton getJButtonAccept() {

		if (jButtonAccept == null) {
			jButtonAccept = new JButton();
			jButtonAccept.addActionListener( new ActionListener() {

				public void actionPerformed(final ActionEvent e) {

					String newLanguage =
						Languages.getShortNameByIndex( jComboBoxLanguages
							.getSelectedIndex() );
					if (!newLanguage.equals( oldLanguage )) {
						StringResourceLoader.getUniqueInstance()
							.processLocaleChange(
								new LocaleChangeEvent( this, newLanguage ) );
						oldLanguage = newLanguage;
						OpenMarkovPreferences.set(
							OpenMarkovPreferences.PREFERENCE_LANGUAGE, newLanguage,
							OpenMarkovPreferences.OPENMARKOV_LANGUAGES );
					}
					// next line must be re-written to use some Event method
					// to notify visibility to false instead calling getParent()
					jButtonAccept.getParent().getParent().getParent()
						.getParent().setVisible( false );
				}
			} );
			jButtonAccept.setName( "ChangeLanguageDialog.jButtonAccept" );
			jButtonAccept.setText( "OK Button" );
			jButtonAccept.setText( dialogStringResource
				.getString( "ChangeLanguageDialog.jButtonAccept.Text" ) );
		}
		return jButtonAccept;
	}

	/**
	 * @return button cancel
	 */
	protected JButton getJButtonCancel() {

		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.addActionListener( new ActionListener() {

				public void actionPerformed(final ActionEvent e) {

					String newLanguage =
						StringResourceLoader.getUniqueInstance().getLanguage();
					if (!newLanguage.equals( oldLanguage )) {
						StringResourceLoader.setLanguage(oldLanguage );
					}
					// next line must be re-written to use some Event method
					// to notify visibility to false instead calling getParent()
					jButtonCancel.getParent().getParent().getParent()
						.getParent().setVisible( false );
				}
			} );
			jButtonCancel.setName( "ChangeLanguageDialog.jButtonCancel" );
			jButtonCancel.setText( "Cancel Button" );
			jButtonCancel.setText( dialogStringResource
				.getString( "ChangeLanguageDialog.jButtonCancel.Text" ) );
		}
		return jButtonCancel;
	}

	/**
	 * @return button apply
	 */
	protected JButton getJButtonApply() {

		if (jButtonApply == null) {
			jButtonApply = new JButton();
			jButtonApply.addActionListener( new ActionListener() {

				public void actionPerformed(final ActionEvent arg0) {

					String newLanguage =
						Languages.getShortNameByIndex( jComboBoxLanguages
							.getSelectedIndex() );
					if (!newLanguage.equals( oldLanguage )) {
						StringResourceLoader.getUniqueInstance()
							.processLocaleChange(
								new LocaleChangeEvent( this, newLanguage ) );
						oldLanguage = newLanguage;
						OpenMarkovPreferences.set(
							OpenMarkovPreferences.PREFERENCE_LANGUAGE, newLanguage,
							OpenMarkovPreferences.OPENMARKOV_LANGUAGES );
					}
				}
			} );
			jButtonApply.setName( "ChangeLanguageDialog.jButtonApply" );
			jButtonApply.setText( "Apply Button" );
			jButtonApply.setText( dialogStringResource
				.getString( "ChangeLanguageDialog.jButtonApply.Text" ) );
		}
		return jButtonApply;
	}

	/**
	 * @return a text area with instructions
	 */
	protected JTextArea getJTextAreaInstructions() {

		if (jTextAreaInstructions == null) {
			jTextAreaInstructions = new JTextArea();
			jTextAreaInstructions.setLineWrap( true );
			jTextAreaInstructions.setWrapStyleWord( true );
			jTextAreaInstructions.setEditable( false );
			jTextAreaInstructions
				.setName( "ChangeLanguageDialog.jTextAreaInstructions" );
			jTextAreaInstructions.setText( "jTextAreaInstructions" );
			jTextAreaInstructions
				.setText( dialogStringResource
					.getString( "ChangeLanguageDialog.jTextAreaInstructions.Text" ) );
		}
		return jTextAreaInstructions;
	}

	/**
	 * process a change in the String Resource Locale, settings all the labels
	 * menus, and strings in the application to the new selected language
	 */
	public void processStringResourceLocaleChange(LocaleChangeEvent event) {

		this.oldLanguage =
			StringResourceLoader.getUniqueInstance().getLanguage();
		StringResourceLoader.getUniqueInstance().allComponentsUpdateSetText(
			this );
		StringResourceLoader.getUniqueInstance().allComponentsUpdateSetText(
			this.getParent() );
		repaint();
	}
	
	/**
	 * Find a language index in the combo box list of the dialog
	 * @param language - the language to find the position in the combo box
	 * @return the index of the language in the combo box
	 */
	protected int findLanguageIndex (String language) {
		
		int index = 0;
		for (int i=0;i<jComboBoxLanguages.getItemCount();i++,index++){
			if (jComboBoxLanguages.getItemAt( index ).equals( language)) {
				break;
			}
		}
		return index;
	}

}
