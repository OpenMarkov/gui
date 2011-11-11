package org.openmarkov.core.gui.main;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openmarkov.core.exception.CanNotWriteNetworkToFileException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.NotRecognisedNetworkFileExtensionException;
import org.openmarkov.core.gui.dialog.about.AboutBox;
import org.openmarkov.core.gui.dialog.configuration.PreferencesEditorDialog;
import org.openmarkov.core.gui.dialog.language.ChangeLanguageDialog;
import org.openmarkov.core.gui.edition.EditionState;
import org.openmarkov.core.gui.edition.NetworkPanel;
import org.openmarkov.core.gui.help.HelpViewer;
import org.openmarkov.core.gui.io.FileChooser;
import org.openmarkov.core.gui.io.NetsIO;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.mdi.FrameContentPanel;
import org.openmarkov.core.gui.mdi.MDIListener;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.network.PropertyNames;
import org.openmarkov.core.gui.utils.LastOpenFiles;
import org.openmarkov.core.gui.utils.OpenMarkovPreferences;
import org.openmarkov.core.gui.utils.Util;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.BayesianNetworkType;




/**
 * This class receives the main events of the application and helps the class
 * MainMenu to carry out this task.
 * 
 * @author jmendoza
 * @version 1.0 - jmendoza - initial version
 * @version 1.1 - jlgozalo - Modify/Add modifiers to methods and create
 *          zoomChangeValue variable
 * @version 1.2 - jlgozalo - Modify the OpenNetwork to set the name of the file
 *          to the Network and add AboutBox and Help actions
 * @version 1.3 - jlgozalo - Store the last open directory and file in the user
 *          Preferences
 * @version 1.4 - jlgozalo - remove calls to System.out and System.err replacing 
 * 			by calls to MessageWindow streams
 */
public class MainPanelListenerAssistant extends WindowAdapter implements
				ActionListener, MDIListener, PropertyNames {

	/**
	 * Main panel which this object helps.
	 */
	private MainPanel mainPanel = null;

	/**
	 * last open files instance
	 */
	private LastOpenFiles lastOpenFiles = new LastOpenFiles();
	/**
	 * Messages string resource.
	 */
	private StringResource stringResource;

	/**
	 * Counter incremented each time a network frame is created.
	 */
	private static int frameIndex = 1;

	/**
	 * Value for the Zoom increment/decrement
	 */
	private static final double zoomChangeValue = 0.2;
	

	/**
	 * Constructor that save the references to the objects that this class
	 * needs.
	 * 
	 * @param mainPanel -
	 *            main panel which this listener helps.
	 */
	public MainPanelListenerAssistant(MainPanel mainPanel) {

		this.mainPanel = mainPanel;
		this.mainPanel.setName( mainPanel.getName() );
		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMessages();

	}

	/**
	 * Invoked when a window is in the process of being closed.
	 * 
	 * @param e -
	 *            event information.
	 */
	@Override
	@SuppressWarnings("unused")
	public void windowClosing(WindowEvent e) {

		closeApplication();

	}

	/**
	 * This method listens to the user actions on the main menu.
	 * 
	 * @param e
	 *            menu event information.
	 */
	public void actionPerformed(ActionEvent e) {

		String actionCommand = e.getActionCommand();
		if (actionCommand.equals( ActionCommands.NEW_NETWORK )) {
			createNewNetwork();
		} else if (actionCommand.equals( ActionCommands.OPEN_NETWORK )) {
			openNetwork();
		} else if (actionCommand.equals( ActionCommands.OPEN_LAST_1_FILE )) {
			openNetwork( lastOpenFiles.getFileNameAt( 1 ) );
		} else if (actionCommand.equals( ActionCommands.OPEN_LAST_2_FILE )) {
			openNetwork( lastOpenFiles.getFileNameAt( 2 ) );
		} else if (actionCommand.equals( ActionCommands.OPEN_LAST_3_FILE )) {
			openNetwork( lastOpenFiles.getFileNameAt( 3 ) );
		} else if (actionCommand.equals( ActionCommands.OPEN_LAST_4_FILE )) {
			openNetwork( lastOpenFiles.getFileNameAt( 4 ) );
		} else if (actionCommand.equals( ActionCommands.OPEN_LAST_5_FILE )) {
			openNetwork( lastOpenFiles.getFileNameAt( 5 ) );
		} else if (actionCommand.equals( ActionCommands.SAVE_NETWORK )) {
			saveNetwork( getCurrentNetworkPanel() );
		} else if (actionCommand.equals( ActionCommands.SAVE_OPEN_NETWORK )) {
			saveOpenNetwork(getCurrentNetworkPanel());
		} else if (actionCommand.equals( ActionCommands.SAVEAS_NETWORK )) {
			saveNetworkAs( getCurrentNetworkPanel() );
		} else if (actionCommand.equals( ActionCommands.CLOSE_NETWORK )) {
			closeActualNetwork();
		} else if (actionCommand.equals( ActionCommands.NETWORK_PROPERTIES )) {
			getCurrentNetworkPanel().changeNetworkProperties();
		} else if (actionCommand.equals( ActionCommands.EXIT_APPLICATION )) {
			closeApplication();
		} else if (actionCommand.equals( ActionCommands.CLIPBOARD_COPY )) {
			//Clipboard disable
			//getCurrentNetworkPanel().exportToClipboard( false );
		} else if (actionCommand.equals( ActionCommands.CLIPBOARD_CUT )) {
			//getCurrentNetworkPanel().exportToClipboard( true );
		} else if (actionCommand.equals( ActionCommands.CLIPBOARD_PASTE )) {
			//getCurrentNetworkPanel().pasteFromClipboard();
		} else if (actionCommand.equals( ActionCommands.UNDO )) {
			undo();
		} else if (actionCommand.equals( ActionCommands.REDO )) {
			redo();
		} else if (actionCommand.equals( ActionCommands.SELECT_ALL )) {
			getCurrentNetworkPanel().selectAllObjects();
		} else if (actionCommand.equals( ActionCommands.OBJECT_REMOVAL )) {
			getCurrentNetworkPanel().removeSelectedObjects();
		} else if (actionCommand.equals( ActionCommands.OBJECT_SELECTION )) {
			activateEditionState( EditionState.SELECTION );
		} else if (actionCommand.equals( ActionCommands.CHANCE_CREATION )) {
			activateEditionState( EditionState.CHANCE );
		} else if (actionCommand.equals( ActionCommands.DECISION_CREATION )) {
			activateEditionState( EditionState.DECISION );
		} else if (actionCommand.equals( ActionCommands.UTILITY_CREATION )) {
			activateEditionState( EditionState.UTILITY );
		} else if (actionCommand.equals( ActionCommands.LINK_CREATION )) {
			activateEditionState( EditionState.LINK );
		} else if (actionCommand.equals( ActionCommands.CHANGE_WORKING_MODE )) {
			setNewWorkingMode();
		} else if (actionCommand.equals( ActionCommands.CHANGE_TO_INFERENCE_MODE )) {
			setNewWorkingMode();
		} else if (actionCommand.equals( ActionCommands.CHANGE_TO_EDITION_MODE )) {
			setNewWorkingMode();
		} else if (actionCommand.equals( ActionCommands.SET_NEW_EXPANSION_THRESHOLD )) {
			setNewExpansionThreshold((Double)e.getSource());
		} else if (actionCommand.equals( ActionCommands.CREATE_NEW_EVIDENCE_CASE )) {
			evidenceCasesNavigationOption("CREATE_NEW_EVIDENCE_CASE");
		} else if (actionCommand.equals( ActionCommands.GO_TO_FIRST_EVIDENCE_CASE )) {
			evidenceCasesNavigationOption("GO_TO_FIRST_EVIDENCE_CASE");
		} else if (actionCommand.equals( ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE )) {
			evidenceCasesNavigationOption("GO_TO_PREVIOUS_EVIDENCE_CASE");
		} else if (actionCommand.equals( ActionCommands.GO_TO_NEXT_EVIDENCE_CASE )) {
			evidenceCasesNavigationOption("GO_TO_NEXT_EVIDENCE_CASE");
		} else if (actionCommand.equals( ActionCommands.GO_TO_LAST_EVIDENCE_CASE )) {
			evidenceCasesNavigationOption("GO_TO_LAST_EVIDENCE_CASE");
		} else if (actionCommand.equals( ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES )) {
			evidenceCasesNavigationOption("CLEAR_OUT_ALL_EVIDENCE_CASES");
		} else if (actionCommand.equals( ActionCommands.PROPAGATE_EVIDENCE )) {
			getCurrentNetworkPanel().propagateEvidence(mainPanel.getMainPanelMenuAssistant());
		} else if (actionCommand.equals( ActionCommands.NODE_PROPERTIES )) {
			getCurrentNetworkPanel().changeNodeProperties();
		} else if (actionCommand.equals( ActionCommands.CHANGE_POTENTIAL )) {
			getCurrentNetworkPanel().changePotentialValues();
		} else if (actionCommand.equals( ActionCommands.TEST )) {
			//getCurrentNetworkPanel().changeNodeTable();	
			try {
				createExpandeNetwork(getCurrentNetworkPanel().probNet);
			} catch (NotEvaluableNetworkException e1) {
				// TODO Enviar mensaje
/*				JOptionPane.showMessageDialog(
						Util.getOwner(getCurrentNetworkPanel().getRootPane()), e1.getMessage(), 
						"Network not evaluable", MessageType.ERROR);*/
			} catch (NotEnoughMemoryException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		} else if (actionCommand.equals( ActionCommands.NODE_EXPANSION )) {
			getCurrentNetworkPanel().expandNode();	
		} else if (actionCommand.equals( ActionCommands.NODE_CONTRACTION )) {
			getCurrentNetworkPanel().contractNode();
		} else if (actionCommand.equals( ActionCommands.NODE_ADD_FINDING )) {
			getCurrentNetworkPanel().addFinding();
		} else if (actionCommand.equals( ActionCommands.NODE_REMOVE_FINDING )) {
			getCurrentNetworkPanel().removeFinding();
		} else if (actionCommand.equals( ActionCommands.NODE_REMOVE_ALL_FINDINGS )) {
			getCurrentNetworkPanel().removeAllFindings();
		} else if (actionCommand.equals( ActionCommands.BYTITLE_NODES )) {
			activateByTitle( true );
		} else if (actionCommand.equals( ActionCommands.BYNAME_NODES )) {
			activateByTitle( false );
		} else if (actionCommand.equals( ActionCommands.ZOOM_IN )) {
			incrementZoomNetwork( getCurrentNetworkPanel() );
		} else if (actionCommand.equals( ActionCommands.ZOOM_OUT )) {
			decrementZoomNetwork( getCurrentNetworkPanel() );
		} else if (actionCommand.equals( ActionCommands.ZOOM_OTHER )) {
			setZoom( true, getCurrentNetworkPanel(), 0 );
		} else if (ActionCommands.isZoomActionCommand( actionCommand )) {
			setZoom( false, getCurrentNetworkPanel(), ActionCommands
				.getValueZoomActionCommand( actionCommand ) );
		} else if (actionCommand.equals( ActionCommands.MESSAGE_WINDOW )) {
			showMessageWindow();
		} else if (actionCommand.equals( ActionCommands.LEARNING )) {
			//learning();
		} else if (actionCommand.equals( ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC )) {
			getCurrentNetworkPanel().showCostEffectivenessDialog(false);
		} else if (actionCommand.equals( ActionCommands.SENSITIVITY_ANALYSIS )) {
			getCurrentNetworkPanel().showCostEffectivenessDialog(true);
		} else if (actionCommand.equals( ActionCommands.CONFIGURATION )) {
			showUserConfigurationDialog();
		} else if (actionCommand.equals( ActionCommands.INFERENCE_OPTIONS )) {
			setInferenceOptions();
		} else if (actionCommand.equals( ActionCommands.HELP_CHANGE_LANGUAGE )) {
			showLanguageChangeDialog();
		} else if (actionCommand.equals( ActionCommands.HELP_HELP )) {
			showHelp();
		} else if (actionCommand.equals( ActionCommands.HELP_ABOUT )) {
			showAbout();
		} 
	}

	private void createExpandeNetwork(ProbNet probNet) 
	throws NotEvaluableNetworkException, NotEnoughMemoryException {
	  /*  VarEliminationSMM simpleMarkovEvaluation = 
	    	new VarEliminationSMM(probNet, 15, null, 500.0);
	 
		createNewFrame2(simpleMarkovEvaluation.getExtendedNet());*/
		
	}

	private void showUncertainValuesDialog() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Create a Java Help viewer
	 * 
	 * @return helpViewer a window to display help
	 */
	private static HelpViewer showHelp() {

		return HelpViewer.getUniqueInstance();

	}

	/**
	 * Create a Frame for a Change Language dialog
	 * 
	 * @return a change language dialog to allow language change
	 */
	private ChangeLanguageDialog showLanguageChangeDialog() {

		return ChangeLanguageDialog
			.getUniqueInstance( mainPanel.getMainFrame() );

	}

	/**
	 * Create a Frame for the User Configuration dialog
	 * 
	 * @return a UserConfiguration dialog
	 */
	private PreferencesEditorDialog showUserConfigurationDialog() {

		return new PreferencesEditorDialog( mainPanel.getMainFrame() );

	}

	/**
	 * Create a Frame for About information
	 * 
	 * @return aboutBox the AboutBox dialog
	 */
	private AboutBox showAbout() {

		return new AboutBox( mainPanel.getMainFrame() );
	}

	/**
	 * Create an instance of <code>openmarkov.learning.gui.NewLearningGUI</code>
	 * 
	 * @return LearningGUI
	 */
	/*private LearningGUI learning() {

		//return LearningGUI.getUniqueInstance(mainPanel.getMainFrame());

	}*/


	/**
	 * Returns the current network panel of the current frame.
	 * 
	 * @return the current network panel.
	 */
	public NetworkPanel getCurrentNetworkPanel() {
		if (mainPanel.getMdi().getOpenFramesNumber() > 0) {
			return (NetworkPanel) mainPanel.getMdi().getCurrentPanel();
		} else {
			return null;
		}
	}

	/**
	 * Returns a value indicating if the network can be closed. If the network
	 * has not been saved, this method offers to the users the possibility of
	 * save it. If the user answers 'yes', the network is saved and can be
	 * closed. If the user answers 'no', the network isn't saved and can be
	 * closed. If the user answers 'cancel', the network can't be closed.
	 * 
	 * @param networkPanel
	 *            network panel to be checked.
	 * @return true, if the network can be closed; otherwise, false.
	 */
	private boolean networkCanBeClosed(NetworkPanel networkPanel) {

		int response = 0;

		if (networkPanel.getModified()) {
			response =
				JOptionPane
					.showConfirmDialog(
						Util.getOwner( mainPanel ), stringResource.getString(
							"NetworkNotSaved.Text.Label", networkPanel
								.getTitle() ), stringResource
							.getString( "NetworkNotSaved.Title.Label" ),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE );
			switch (response) {
			case JOptionPane.YES_OPTION: {
				return saveNetwork( networkPanel );
			}
			case JOptionPane.NO_OPTION: {
				return true;
			}
			default: {
				return false;
			}
			}
		}

		return true;

	}

	/**
	 * This method executes when a network frame is going to be closed.
	 * 
	 * @param contentPanel
	 *            content panel of the frame that is trying to be closed.
	 * @return true, if the frame that contents the panel can be closed;
	 *         otherwise, false.
	 */
	public boolean frameClosing(FrameContentPanel contentPanel) {

		return networkCanBeClosed( (NetworkPanel) contentPanel );

	}

	/**
	 * This method executes when a frame has been closed.
	 * 
	 * @param contentPanel
	 *            content panel of the frame that has been closed.
	 */
	@SuppressWarnings("unused")
	public void frameClosed(FrameContentPanel contentPanel) {

		if (mainPanel.getMdi().getOpenFramesNumber() == 0) {
			mainPanel.setToolBarPanel(NetworkPanel.EDITION_WORKING_MODE);
			mainPanel.getMainPanelMenuAssistant()
				.updateOptionsAllNetworkClosed();
		}

	}

	/**
	 * This method executes when a network frame has been selected.
	 * 
	 * @param contentPanel
	 *            content panel of the frame that has been selected.
	 */
	public void frameSelected(FrameContentPanel contentPanel) {

		mainPanel.getMainPanelMenuAssistant().updateOptionsNetworkDependent(
			(NetworkPanel) contentPanel );
		mainPanel.getExistingInferenceToolBar().
				setCurrentEvidenceCaseName(getCurrentNetworkPanel().getCurrentCase(), 
						getCurrentNetworkPanel().isPropagationActive());
		
	}

	/**
	 * Saves a network in a file and makes the rest of actions in the
	 * environment (menus, messages, etc.).
	 * 
	 * @param networkPanel
	 *            network panel which contains the network to be saved.
	 * @param fileName
	 *            file where save the network.
	 * @return true if the network could be saved; otherwise, false.
	 */
	private boolean saveNetworkActions(NetworkPanel networkPanel,
										String fileName) {

		boolean result = false;

		mainPanel.getMessageWindow().getNormalMessageStream().println( stringResource
			.getString( "SavingNetwork.Text.Label" )
			+ " " + fileName );
		try {
			NetsIO.saveNetworkFile(networkPanel.getProbNet(), fileName);
			//networkPanel.getNetwork().backupProbNet.saveToFile( fileName );
			networkPanel.setModified( false );
			networkPanel.setNetworkFile( fileName );
			mainPanel.getMainPanelMenuAssistant().updateOptionsNetworkSaved();
			lastOpenFiles.setLastFileName( fileName );
			OpenMarkovPreferences.set(
				OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
				getDirectoryFileName( fileName ),
				OpenMarkovPreferences.OPENMARKOV_DIRECTORIES );
			mainPanel.getMessageWindow().getNormalMessageStream().println( stringResource
				.getString( "NetworkSaved.Text.Label" ) );
			mainPanel.getMainMenu().rechargeLastOpenFiles();

			result = true;
		} catch (NotRecognisedNetworkFileExtensionException e) {
			JOptionPane.showMessageDialog(
				Util.getOwner( mainPanel ), stringResource
					.getString( "CanNotRecognisedFileExtension.Text.Label" ),
				stringResource.getString( "ErrorWindow.Title.Label" ),
				JOptionPane.ERROR_MESSAGE );

		} catch (CanNotWriteNetworkToFileException e) {
			JOptionPane.showMessageDialog(
				Util.getOwner( mainPanel ), stringResource
					.getString( "ErrorSavingNetwork.Text.Label" ),
				stringResource.getString( "ErrorWindow.Title.Label" ),
				JOptionPane.ERROR_MESSAGE );

		} catch (Exception e) {
			JOptionPane.showMessageDialog(
					Util.getOwner( mainPanel ), stringResource
						.getString( "Generic I/O error" ),
					stringResource.getString( "ErrorWindow.Title.Label" ),
					JOptionPane.ERROR_MESSAGE );
		}

		return result;
	}

	/**
	 * Save a network. First it requests the file in which save the network and
	 * then saves the network.
	 * 
	 * @param networkPanel
	 *            network panel that contains the network to be saved.
	 * @return true if the network has been saved; otherwise, false.
	 */
	private boolean saveNetwork(NetworkPanel networkPanel) {

		String fileName = networkPanel.getNetworkFile();

		return (fileName != null) ? saveNetworkActions( networkPanel, fileName )
			: saveNetworkAs( networkPanel );

	}
	
	/**
	 * Save a network. First it requests the file in which save the network and
	 * then saves the network.
	 * 
	 * @param networkPanel
	 *            network panel that contains the network to be saved.
	 * @return true if the network has been saved; otherwise, false.
	 */
	private void saveOpenNetwork(NetworkPanel networkPanel) {
		String fileName = networkPanel.getNetworkFile();
		try {
			File inFile = new File(fileName);
			String newFileName = toBakExtension(networkPanel.getNetworkFile());
			
			File outFile = new File(newFileName);
					
			FileInputStream in = new FileInputStream(inFile);
			FileOutputStream out = new FileOutputStream(outFile);
 
			int c;
			while( (c = in.read() ) != -1)
				out.write(c);
 			in.close();
			out.close();
			
		} catch(IOException e) {
			mainPanel.getMessageWindow().getNormalMessageStream().
			println( stringResource.getString( 
					"NetworkBackupError.Text.Label" ) );
		}
		mainPanel.getMessageWindow().getNormalMessageStream().
		println( stringResource.getString( 
				"NetworkBackup.Text.Label" ) );
		saveNetwork(networkPanel);
		closeActualNetwork();
		openNetwork(fileName);
		
	}

	private String toBakExtension(String nameFile) {
		String newName;
		int index = nameFile.lastIndexOf(".");
		if ( index > 0 ){
			newName = nameFile.substring(0, index);
		}
		else 
			newName = nameFile; 
		
		return newName + ".bak";
	}

	/**
	 * Save a network in a different file. First it requests the file in which
	 * save the network and then saves the network.
	 * 
	 * @param networkPanel
	 *            network panel that contains the network to be saved.
	 * @return true if the network has been saved; otherwise, false.
	 */
	private boolean saveNetworkAs(NetworkPanel networkPanel) {

		String fileName = networkPanel.getNetworkFile();

		fileName =
			requestNetworkFileToSave( (fileName != null) ? fileName
				: networkPanel.getProbNet().getName() );
		if (fileName != null) {
			networkPanel.setNetworkFile( fileName );
			networkPanel.getProbNet().setName(getShortNetworkName( fileName ));
		}

		return (fileName != null) ? saveNetworkActions( networkPanel, fileName )
			: false;

	}

	/**
	 * It asks the user to choose a file by means of a save-file dialog box.
	 * 
	 * @param suggestedFileName
	 *            name of the file where the net can be saved as default.
	 * @return complete path of the file, or null if the user selects cancel.
	 */
	private String requestNetworkFileToSave(String suggestedFileName) {

		FileChooser fileChooser = new FileChooser();

		fileChooser.setDialogTitle( stringResource
			.getString( "SaveNetwork.Title.Label" ) );
		File currentDirectory =
			new File( OpenMarkovPreferences.get(
				OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
				OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, "." ) );
		fileChooser.setCurrentDirectory( currentDirectory );
		fileChooser.setPGMXFilter();
		fileChooser.setSelectedFile( new File( suggestedFileName ) );

		return (fileChooser.showSaveDialog( Util.getOwner( mainPanel ) ) == 
			JFileChooser.APPROVE_OPTION)
			? fileChooser.getSelectedFile().getAbsolutePath() : null;

	}

	/**
	 * Creates a new network in the workspace. First, it requests the adittionalProperties
	 * of the new network and, if the user accepts the dialog box, a new network
	 * is created.
	 * @wbp.parser.entryPoint
	 */
	private void createNewNetwork() {

		ProbNet probNet=null;
		PartitionedInterval partitionedInterval = null;
		probNet = new ProbNet(BayesianNetworkType.getUniqueInstance ());
		probNet.setName(new String (stringResource.
			getString( "InternalFrame.Title.Label" )+
				" " + frameIndex));
		
		probNet.getPNESupport().setWithUndo(false);
		
		if (NetworkPanel.requestNetworkProperties(probNet,
			Util.getOwner( mainPanel ),true )) {
				probNet.getPNESupport().setWithUndo(true);
				createNewFrame2(probNet);
				frameIndex++;
				//mainPanelMenuAssistant is added as listener to probNet
				//for menus updated purposes.
				probNet.getPNESupport().addUndoableEditListener(
						mainPanel.getMainPanelMenuAssistant());
		}
		
	}

	/**
	 * Creates a new frame in the workspace, suppling the network to be painted
	 * into the frame.
	 * 
	 * @param network
	 *            network to be painted into the frame
	 * @return the network panel that is created.
	 */
	//changed by mpalacios
	//called only in learnButtonActionPerformed method in learningGui
	public NetworkPanel createNewFrame(ProbNet probNet) {

		NetworkPanel networkPanel = null;

		try {
			networkPanel = new NetworkPanel(probNet, mainPanel);
			
			mainPanel.getMdi().createNewFrame( networkPanel );
			networkPanel.setPopupMenuFactory( mainPanel.getPopupMenuFactory() );
			//mpalacios mainPanel listen to networkPanel.
			//networkPanel.addEditionListener( mainPanel
			//	.getMainPanelMenuAssistant() );
			networkPanel.addSelectionListener( mainPanel
				.getMainPanelMenuAssistant() );
			mainPanel.getMainPanelMenuAssistant().updateOptionsNewNetworkOpen();
			mainPanel.getMainPanelMenuAssistant()
				.updateOptionsNetworkDependent( networkPanel );
			mainPanel.getExistingInferenceToolBar().
					setCurrentEvidenceCaseName(getCurrentNetworkPanel().getCurrentCase(), 
							getCurrentNetworkPanel().isPropagationActive());
		} catch (UnsupportedOperationException e) {
			JOptionPane.showMessageDialog(
				Util.getOwner( mainPanel ), e.getMessage(), stringResource
					.getString( "ErrorWindow.Title.Label" ),
				JOptionPane.ERROR_MESSAGE );
		}

		return networkPanel;

	}
	public NetworkPanel createNewFrame2(ProbNet probNet) {

		NetworkPanel networkPanel = null;

		try {
			networkPanel = new NetworkPanel( probNet, mainPanel);
			mainPanel.getMdi().createNewFrame( networkPanel );
			networkPanel.setPopupMenuFactory( mainPanel.getPopupMenuFactory() );
			//networkPanel.addEditionListener( mainPanel
				//.getMainPanelMenuAssistant() );
			networkPanel.addSelectionListener( mainPanel
				.getMainPanelMenuAssistant() );
			mainPanel.getMainPanelMenuAssistant().updateOptionsNewNetworkOpen();
			mainPanel.getMainPanelMenuAssistant()
				.updateOptionsNetworkDependent( networkPanel );
			mainPanel.getExistingInferenceToolBar().
					setCurrentEvidenceCaseName(getCurrentNetworkPanel().getCurrentCase(),
							getCurrentNetworkPanel().isPropagationActive());
		} catch (UnsupportedOperationException e) {
			JOptionPane.showMessageDialog(
				Util.getOwner( mainPanel ), e.getMessage(), stringResource
					.getString( "ErrorWindow.Title.Label" ),
				JOptionPane.ERROR_MESSAGE );
		}

		return networkPanel;

	}
	

	/**
	 * Open a network.
	 */
	private void openNetwork() {

		openNetwork( "" );
	}

	/**
	 * Open a existing network in a new network frame. If it is not a recently
	 * closed network (registered in the menu), it requests the file which
	 * contains the network and then opens a new network frame.
	 * 
	 * @param fileName -
	 *            for the network
	 */
	private void openNetwork(String fileName) {
		if (fileName.equals( "" )) {
			fileName = requestNetworkFileToOpen();
		}
		ProbNet fileContent = null;
		NetworkPanel networkPanel = null;

		if (fileName != null) {
			try {
				mainPanel.getMessageWindow().getNormalMessageStream().println( 
						stringResource.getString( "LoadingNetwork.Text.Label" )
					    + " " + fileName );
				
				fileContent = NetsIO.openNetworkFile(fileName );
				fileContent.getPNESupport().addUndoableEditListener(mainPanel.
						getMainPanelMenuAssistant());	
				fileContent.getPNESupport().setWithUndo(true);
				fileContent.setName(getShortNetworkName( fileName ));
				networkPanel = createNewFrame2( fileContent );
				networkPanel.setNetworkFile( fileName );
				lastOpenFiles.setLastFileName( fileName );
				OpenMarkovPreferences.set( OpenMarkovPreferences.
						LAST_OPEN_DIRECTORY, getDirectoryFileName( fileName ),
						OpenMarkovPreferences.OPENMARKOV_DIRECTORIES );
				mainPanel.getMessageWindow().getNormalMessageStream().
					println( stringResource.getString( 
							"NetworkLoaded.Text.Label" ) );
				mainPanel.getMainMenu().rechargeLastOpenFiles();
			} catch (Exception e) {
				mainPanel.getMessageWindow().getErrorMessageStream().println( 
						e.getMessage() );
				JOptionPane.showMessageDialog( Util.getOwner( mainPanel ), 
						stringResource.getString( 
								"ErrorLoadingNetwork.Text.Label" ),
						stringResource.getString( "ErrorWindow.Title.Label" ),
						JOptionPane.ERROR_MESSAGE );
			}
		}

	}

	/**
	 * It asks the user to choose a file by means of a open-file dialog box.
	 * 
	 * @return complete path of the file, or null if the user selects cancel.
	 */
	private String requestNetworkFileToOpen() {

		FileChooser fileChooser = new FileChooser();

		fileChooser.setDialogTitle( stringResource
			.getString( "OpenNetwork.Title.Label" ) );
		File currentDirectory =
			new File( OpenMarkovPreferences.get(
				OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
				OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, "." ) );
		fileChooser.setCurrentDirectory( currentDirectory );
		fileChooser.setPGMXFilter();
		String fileName =
			(fileChooser.showOpenDialog( Util.getOwner( mainPanel ) ) == JFileChooser.APPROVE_OPTION)
				? fileChooser.getSelectedFile().getAbsolutePath() : null;

		return fileName;

	}

	/**
	 * Closes the actual network frame.
	 * 
	 * @return true if the network has been closed; otherwise, false.
	 */
	private boolean closeActualNetwork() {

		if (networkCanBeClosed( getCurrentNetworkPanel() )) {
			mainPanel.getMdi().closeActualFrame();
			if (mainPanel.getMdi().getOpenFramesNumber() == 0) {
				mainPanel.setToolBarPanel(NetworkPanel.EDITION_WORKING_MODE);
				mainPanel.getMainPanelMenuAssistant()
					.updateOptionsAllNetworkClosed();
			}

			return true;
		}

		return false;

	}

	/**
	 * Process that executes when the user is trying to close the application.
	 */
	private void closeApplication() {

		boolean allClosed = true;

		while (allClosed && (mainPanel.getMdi().getOpenFramesNumber() > 0)) {
			allClosed = closeActualNetwork();
		}
		if (allClosed) {
			System.exit( 0 );
		}

	}

	/**
	 * This method undoes the last operation on the actual network.
	 */
	private void undo() {

		try {
			undoRedo( true );
			
		} catch (CannotUndoException e) {
			JOptionPane.showMessageDialog(
				Util.getOwner( mainPanel ), stringResource
					.getString( "CannotUndo.Text.Label" ), stringResource
					.getString( "ErrorWindow.Title.Label" ),
				JOptionPane.ERROR_MESSAGE );
		}

	}

	/**
	 * This method re-does the last undone operation on the actual network.
	 */
	private void redo() {

		try {
			undoRedo( false );
		} catch (CannotRedoException e) {
			JOptionPane.showMessageDialog(
				Util.getOwner( mainPanel ), stringResource
					.getString( "CannotRedo.Text.Label" ), stringResource
					.getString( "ErrorWindow.Title.Label" ),
				JOptionPane.ERROR_MESSAGE );
		}

	}

	/**
	 * This method undoes or re-does an operation on the actual network.
	 * 
	 * @param undoOperation -
	 *            if true, an undo must be performed; if false, a redo will be
	 *            performed.
	 * @throws CannotUndoException - if undo can't be performed.
	 * @throws CannotRedoException - if redo can't be performed.
	 */
	private void undoRedo(boolean undoOperation)  throws CannotUndoException, CannotRedoException {

		NetworkPanel networkPanel = null;

		networkPanel = getCurrentNetworkPanel();
		if (undoOperation) {
			   
				networkPanel.undo();
				networkPanel.repaint();
			} else {
				networkPanel.redo();
				networkPanel.repaint();
			}
		}

	/**
	 * This method activates an edition option for the actual network.
	 * 
	 * @param newState
	 *            new edition state to set.
	 */
	private void activateEditionState(EditionState newState) {

		NetworkPanel networkPanel = null;

		networkPanel = getCurrentNetworkPanel();
		networkPanel.setEditionState( newState );
		mainPanel.getMainPanelMenuAssistant().setEditionOption(
			newState, networkPanel.isThereDataStored() );

	}

	/**
	 * This method establishes the network working mode (edition or inference).
	 * 
	 * @param newWorkingMode
	 *            new working mode to set in the network.
	 */
	private void setNewWorkingMode() {
		int currentWorkingMode = getCurrentNetworkPanel().getWorkingMode();
		int newWorkingMode;
		if (currentWorkingMode == NetworkPanel.EDITION_WORKING_MODE) {
			newWorkingMode = NetworkPanel.INFERENCE_WORKING_MODE;
		} else {
			newWorkingMode = NetworkPanel.EDITION_WORKING_MODE;
		}
		mainPanel.setToolBarPanel(newWorkingMode);
		mainPanel.changeWorkingModeButton(newWorkingMode);
		getCurrentNetworkPanel().setWorkingMode(newWorkingMode);
		activateEditionState(EditionState.SELECTION);
		getCurrentNetworkPanel().setSelectedAllObjects(false);
		mainPanel.getMainPanelMenuAssistant().updateOptionsNewWorkingMode(newWorkingMode,
				getCurrentNetworkPanel());
		if (newWorkingMode == NetworkPanel.INFERENCE_WORKING_MODE) {
			getCurrentNetworkPanel().updateIndividualProbabilities();
			mainPanel.getExistingInferenceToolBar().
					setCurrentEvidenceCaseName(getCurrentNetworkPanel().getCurrentCase(),
							getCurrentNetworkPanel().isPropagationActive());
		} else {
			//getCurrentNetworkPanel().removeAllFindings(); //Suppressed the elimination of findings on returning to Edition Mode
		}
		getCurrentNetworkPanel().updateNodesExpansionState(newWorkingMode);
	}
	
	/**
	 * This method establishes the new expansion threshold of the network.
	 * 
	 * @param newValue
	 *            new value for expansion threshold
	 */
	private void setNewExpansionThreshold(Double newValue) {
		getCurrentNetworkPanel().setExpansionThreshold(newValue);
		activateEditionState(EditionState.SELECTION);
		getCurrentNetworkPanel().setSelectedAllNodes(false);
		mainPanel.getMainPanelMenuAssistant().
				updateOptionsNewWorkingMode(NetworkPanel.INFERENCE_WORKING_MODE, getCurrentNetworkPanel());
		getCurrentNetworkPanel().updateNodesExpansionState(NetworkPanel.INFERENCE_WORKING_MODE);
	}
	
	/**
	 * This method responds to the navigation among the evidence cases option 
	 * selected by the user.
	 * 
	 * @param command
	 *            the Action Command corresponding to the selected option
	 */
	private void evidenceCasesNavigationOption(String command) {
		if (command.equals("CREATE_NEW_EVIDENCE_CASE")) {
			getCurrentNetworkPanel().createNewEvidenceCase();
		} else if (command.equals("GO_TO_FIRST_EVIDENCE_CASE")) {
			getCurrentNetworkPanel().goToFirstEvidenceCase();
		} else if (command.equals("GO_TO_PREVIOUS_EVIDENCE_CASE")) {
			getCurrentNetworkPanel().goToPreviousEvidenceCase();
		} else if (command.equals("GO_TO_NEXT_EVIDENCE_CASE")) {
			getCurrentNetworkPanel().goToNextEvidenceCase();
		} else if (command.equals("GO_TO_LAST_EVIDENCE_CASE")) {
			getCurrentNetworkPanel().goToLastEvidenceCase();
		} else if (command.equals("CLEAR_OUT_ALL_EVIDENCE_CASES")) {
			getCurrentNetworkPanel().clearOutAllEvidenceCases();
		}		
		mainPanel.getMainPanelMenuAssistant().
				updateOptionsEvidenceCasesNavigation(getCurrentNetworkPanel());
	}
	
	/**
	 * This method sets the inference options.
	 */
	private void setInferenceOptions() {
		getCurrentNetworkPanel().setInferenceOptions();
		mainPanel.getMainPanelMenuAssistant().
			updateOptionsEvidenceCasesNavigation(getCurrentNetworkPanel());
		mainPanel.getMainPanelMenuAssistant().
			updateOptionsPropagationTypeDependent(getCurrentNetworkPanel());
	}
	
	/**
	 * Sets the mode of painting the nodes.
	 * 
	 * @param byTitle
	 *            if true, then the texts that appear into the nodes will be
	 *            their titles; if false, these texts will be their name.
	 */
	private void activateByTitle(boolean byTitle) {

		NetworkPanel actualNetwork = null;

		actualNetwork = getCurrentNetworkPanel();
		if (actualNetwork.getByTitle() != byTitle) {
			actualNetwork.setByTitle( byTitle );
			mainPanel.getMainPanelMenuAssistant().setByTitle( byTitle );
		}

	}

	/**
	 * This method restores (if minimized) and shows the message window.
	 */
	private void showMessageWindow() {

		mainPanel.getMessageWindow().setExtendedState( Frame.NORMAL );
		mainPanel.getMessageWindow().setVisible( true );

	}

	/**
	 * This method increments the zoom of the actual network.
	 * 
	 * @param network
	 *            network whose zoom will be changed.
	 */
	private void incrementZoomNetwork(NetworkPanel network) {

		setZoom( false, network, network.getZoom() + zoomChangeValue );

	}

	/**
	 * This method decrements the zoom of the actual network.
	 * 
	 * @param network
	 *            network whose zoom will be changed.
	 */
	private void decrementZoomNetwork(NetworkPanel network) {

		setZoom( false, network, network.getZoom() - zoomChangeValue );

	}

	/**
	 * Sets the zoom of the actual network and updates the menu and the toolbar.
	 * 
	 * @param dialogBox
	 *            if true, the parameter 'value' is ignored and this value is
	 *            requested to user.
	 * @param networkPanel
	 *            network whose zoom will be changed.
	 * @param value
	 *            new zoom value.
	 */
	private void setZoom(boolean dialogBox, NetworkPanel networkPanel,
							double value) {

		double newZoom = 0.0;

		if (dialogBox) {
			networkPanel.requestZoomToUser( Util.getOwner( mainPanel ) );
		} else {
			networkPanel.setZoom( value );
		}
		newZoom = networkPanel.getZoom();
		mainPanel.getMainPanelMenuAssistant().setZoom( newZoom );

	}

	/**
	 * commodity method to provide a short name for the network file name
	 * @param fileName - name of the file to obtain the short name
	 * @return the short name of the file
	 */
	private static String getShortNetworkName(String fileName) {

		String shortFileName = null;
		int i = fileName.lastIndexOf( "\\" );
		if ((i > 0) && (i < (fileName.length() - 1))) {
			shortFileName = fileName.substring( i + 1 ).toLowerCase();
		}

		return shortFileName;

	}

	/**
	 * commodity method to provide the path directory for the network file name
	 * @param fileName - name of the file to obtain the short name
	 * @return the directory of the file
	 */
	private static String getDirectoryFileName(String fileName) {

		String directoryFileName = null;
		int i = fileName.lastIndexOf( "\\" );
		if ((i > 0) && (i < (fileName.length() - 1))) {
			directoryFileName = fileName.substring( 0, i ).toLowerCase();
		}
		return directoryFileName;
	}

}
