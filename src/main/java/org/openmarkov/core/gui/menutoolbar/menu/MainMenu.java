package org.openmarkov.core.gui.menutoolbar.menu;


import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.help.CSH;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.openmarkov.core.gui.component.LastRecentFilesMenuItem;
import org.openmarkov.core.gui.configuration.LastOpenFiles;
import org.openmarkov.core.gui.help.HelpViewer;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasic;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasicImpl;
import org.openmarkov.core.gui.menutoolbar.common.ZoomMenuToolBar;




/**
 * Class that manages the main menubar. It configures the default main menubar
 * and chages it according to the state of the application.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Add Help menus, HelpViewer, suppress from view the Menu
 *          Nodes View and set all the names for the different menus and menu
 *          items to ensure i18N Add getLastOpenFiles() method to the menu
 * @version 1.2 asaez Add Inference menus
 */
public class MainMenu extends JMenuBar implements MenuToolBarBasic,
				ZoomMenuToolBar {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 8267763502728836096L;

	/**
	 * Constant that defines the menu 'File'.
	 */
	public static final String FILE_MENU = "File";

	/**
	 * Constant that defines the item 'File - New'.
	 */
	public static final String FILE_NEW_MENUITEM = FILE_MENU + ".New";

	/**
	 * Constant that defines the item 'File - Open'.
	 */
	public static final String FILE_OPEN_MENUITEM = FILE_MENU + ".Open";

	/**
	 * 
	 * Constant that defines the item 'File - Save'.
	 */
	public static final String FILE_SAVE_MENUITEM = FILE_MENU + ".Save";
	
	/**
	 * Constant that defines the item 'File - Save'.
	 */
	public static final String FILE_SAVE_OPEN_MENUITEM = FILE_MENU + ".SaveOpen";

	/**
	 * Constant that defines the item 'File - Save as'.
	 */
	public static final String FILE_SAVEAS_MENUITEM = FILE_MENU + ".SaveAs";

	/**
	 * Constant that defines the item 'File - Close'.
	 */
	public static final String FILE_CLOSE_MENUITEM = FILE_MENU + ".Close";

	/**
	 * Constant that defines the item 'File - Network adittionalProperties'.
	 */
	public static final String FILE_NETWORKPROPERTIES_MENUITEM =
		FILE_MENU + ".NetworkProperties";

	/**
	 * Constant that defines the item 'File - Exit'.
	 */
	public static final String FILE_EXIT_MENUITEM = FILE_MENU + ".Exit";

	/**
	 * Constant that defines the menu 'Edit'.
	 */
	public static final String EDIT_MENU = "Edit";

	/**
	 * Constant that defines the item 'Edit - Cut'.
	 */
	public static final String EDIT_CUT_MENUITEM = EDIT_MENU + ".Cut";

	/**
	 * Constant that defines the item 'Edit - Copy'.
	 */
	public static final String EDIT_COPY_MENUITEM = EDIT_MENU + ".Copy";

	/**
	 * Constant that defines the item 'Edit - Paste'.
	 */
	public static final String EDIT_PASTE_MENUITEM = EDIT_MENU + ".Paste";

	/**
	 * Constant that defines the item 'Edit - Remove'.
	 */
	public static final String EDIT_REMOVE_MENUITEM = EDIT_MENU + ".Remove";

	/**
	 * Constant that defines the item 'Edit - Undo'.
	 */
	public static final String EDIT_UNDO_MENUITEM = EDIT_MENU + ".Undo";

	/**
	 * Constant that defines the item 'Edit - Redo'.
	 */
	public static final String EDIT_REDO_MENUITEM = EDIT_MENU + ".Redo";

	/**
	 * Constant that defines the item 'Edit - Select all'.
	 */
	public static final String EDIT_SELECTALL_MENUITEM =
		EDIT_MENU + ".SelectAll";

	/**
	 * Constant that defines the item 'Edit - Object selection'.
	 */
	public static final String EDIT_OBJECTSELECTION_MENUITEM =
		EDIT_MENU + ".ObjectSelection";

	/**
	 * Constant that defines the item 'Edit - Chance nodes creation'.
	 */
	public static final String EDIT_CHANCECREATION_MENUITEM =
		EDIT_MENU + ".ChanceCreation";

	/**
	 * Constant that defines the item 'Edit - Decision nodes creation'.
	 */
	public static final String EDIT_DECISIONCREATION_MENUITEM =
		EDIT_MENU + ".DecisionCreation";

	/**
	 * Constant that defines the item 'Edit - Utility nodes creation'.
	 */
	public static final String EDIT_UTILITYCREATION_MENUITEM =
		EDIT_MENU + ".UtilityCreation";

	/**
	 * Constant that defines the item 'Edit - Links creation'.
	 */
	public static final String EDIT_LINKCREATION_MENUITEM =
		EDIT_MENU + ".LinkCreation";

	/**
	 * Constant that defines the item 'Edit - Node adittionalProperties'.
	 */
	public static final String EDIT_NODEPROPERTIES_MENUITEM =
		EDIT_MENU + ".NodeProperties";
		
	/**
	 * Constant that defines the item 'Edit - Node table'.
	 */
	public static final String EDIT_NODERELATION_MENUITEM =
		EDIT_MENU + ".NodePotential";
	
	/**
	 * Constant that defines the item 'Edit - Test'.
	 */
	public static final String EDIT_NODETEST_MENUITEM =
		EDIT_MENU + ".NodeTest";
	
	/**
	 * Constant that defines the item 'Edit - Link adittionalProperties'.
	 */
	public static final String EDIT_LINKPROPERTIES_MENUITEM =
		EDIT_MENU + ".LinkProperties";

	/**
	 * Constant that defines the item 'Edit - Switch to Inference mode'.
	 */
	public static final String EDIT_SWITCH_TO_INFERENCE_MODE_MENUITEM =
		EDIT_MENU + ".SwitchToInference";
	
	/**
	 * Constant that defines the menu 'Inference'.
	 */
	public static final String INFERENCE_MENU = "Inference";
	
	/**
	 * Constant that defines the item 'Inference - Switch to Edition mode'.
	 */
	public static final String INFERENCE_SWITCH_TO_EDITION_MODE_MENUITEM = 
		INFERENCE_MENU + ".SwitchToEdition";
	
	/**
	 * Constant that defines the item 'Inference - Inference Options '.
	 */
	public static final String INFERENCE_OPTIONS_MENUITEM = 
		INFERENCE_MENU + ".Options";
	
	/**
	 * Constant that defines the item 'Inference - Create New Evidence Case'.
	 */
	public static final String INFERENCE_CREATE_NEW_EVIDENCE_CASE_MENUITEM = 
		INFERENCE_MENU + ".CreateNewEvidenceCase";

	/**
	 * Constant that defines the item 'Inference - Go To First Evidence Case'.
	 */
	public static final String INFERENCE_GO_TO_FIRST_EVIDENCE_CASE_MENUITEM = 
		INFERENCE_MENU + ".GoToFirstEvidenceCase";
	
	/**
	 * Constant that defines the item 'Inference - Go To Previous Evidence Case'.
	 */
	public static final String INFERENCE_GO_TO_PREVIOUS_EVIDENCE_CASE_MENUITEM = 
		INFERENCE_MENU + ".GoToPreviousEvidenceCase";
	
	/**
	 * Constant that defines the item 'Inference - Go To Next Evidence Case'.
	 */
	public static final String INFERENCE_GO_TO_NEXT_EVIDENCE_CASE_MENUITEM = 
		INFERENCE_MENU + ".GoToNextEvidenceCase";
	
	/**
	 * Constant that defines the item 'Inference - Go To Last Evidence Case'.
	 */
	public static final String INFERENCE_GO_TO_LAST_EVIDENCE_CASE_MENUITEM = 
		INFERENCE_MENU + ".GoToLastEvidenceCase";
	
	/**
	 * Constant that defines the item 'Inference - Clear Out All Evidence Cases'.
	 */
	public static final String INFERENCE_CLEAR_OUT_ALL_EVIDENCE_CASES_MENUITEM = 
		INFERENCE_MENU + ".ClearOutAllEvidenceCases";	

	/**
	 * Constant that defines the item 'Inference - Propagate Evidence'.
	 */
	public static final String INFERENCE_PROPAGATE_EVIDENCE_MENUITEM = 
		INFERENCE_MENU + ".PropagateEvidence";
	
	/**
	 * Constant that defines the item 'Inference - Expand Node'.
	 */
	public static final String INFERENCE_EXPAND_NODE_MENUITEM =
		INFERENCE_MENU + ".Expansion";
	
	/**
	 * Constant that defines the item 'Inference - Contract Node'.
	 */
	public static final String INFERENCE_CONTRACT_NODE_MENUITEM =
		INFERENCE_MENU + ".Contraction";
	
	/**
	 * Constant that defines the item 'Inference - AddFinding'.
	 */
	public static final String INFERENCE_ADD_FINDING_MENUITEM = 
		INFERENCE_MENU + ".AddFinding";
	
	/**
	 * Constant that defines the item 'Inference - RemoveFinding'.
	 */
	public static final String INFERENCE_REMOVE_FINDING_MENUITEM = 
		INFERENCE_MENU + ".RemoveFinding";
	
	/**
	 * Constant that defines the item 'Inference - RemoveFinding'.
	 */
	public static final String INFERENCE_REMOVE_ALL_FINDINGS_MENUITEM = 
		INFERENCE_MENU + ".RemoveAllFindings";

	/**
	 * Constant that defines the menu 'View'.
	 */
	public static final String VIEW_MENU = "View";

	/**
	 * Constant that defines the item 'View - Nodes'.
	 */
	public static final String VIEW_NODES_MENU = VIEW_MENU + ".Nodes";

	/**
	 * Constant that defines the item 'View - Nodes - By name'.
	 */
	public static final String VIEW_NODES_BYNAME_MENUITEM =
		VIEW_NODES_MENU + ".ByName";

	/**
	 * Constant that defines the item 'View - Nodes - By title'.
	 */
	public static final String VIEW_NODES_BYTITLE_MENUITEM =
		VIEW_NODES_MENU + ".ByTitle";

	/**
	 * Constant that defines the item 'View - Zoom'.
	 */
	public static final String VIEW_ZOOM_MENU = VIEW_MENU + ".Zoom";

	/**
	 * Constant that defines the item 'View - Zoom - Zoom in'.
	 */
	public static final String VIEW_ZOOM_IN_MENUITEM =
		VIEW_ZOOM_MENU + ".ZoomIn";

	/**
	 * Constant that defines the item 'View - Zoom - Zoom out'.
	 */
	public static final String VIEW_ZOOM_OUT_MENUITEM =
		VIEW_ZOOM_MENU + ".ZoomOut";

	/**
	 * Constant that defines the item 'View - Zoom - 500%'.
	 */
	public static final String VIEW_ZOOM_500_MENUITEM = VIEW_ZOOM_MENU + ".500";

	/**
	 * Constant that defines the item 'View - Zoom - 200%'.
	 */
	public static final String VIEW_ZOOM_200_MENUITEM = VIEW_ZOOM_MENU + ".200";

	/**
	 * Constant that defines the item 'View - Zoom - 150%'.
	 */
	public static final String VIEW_ZOOM_150_MENUITEM = VIEW_ZOOM_MENU + ".150";

	/**
	 * Constant that defines the item 'View - Zoom - 100%'.
	 */
	public static final String VIEW_ZOOM_100_MENUITEM = VIEW_ZOOM_MENU + ".100";

	/**
	 * Constant that defines the item 'View - Zoom - 75%'.
	 */
	public static final String VIEW_ZOOM_75_MENUITEM = VIEW_ZOOM_MENU + ".75";

	/**
	 * Constant that defines the item 'View - Zoom - 50%'.
	 */
	public static final String VIEW_ZOOM_50_MENUITEM = VIEW_ZOOM_MENU + ".50";

	/**
	 * Constant that defines the item 'View - Zoom - 25%'.
	 */
	public static final String VIEW_ZOOM_25_MENUITEM = VIEW_ZOOM_MENU + ".25";

	/**
	 * Constant that defines the item 'View - Zoom - 10%'.
	 */
	public static final String VIEW_ZOOM_10_MENUITEM = VIEW_ZOOM_MENU + ".10";

	/**
	 * Constant that defines the item 'View - Zoom - Other'.
	 */
	public static final String VIEW_ZOOM_OTHER_MENUITEM =
		VIEW_ZOOM_MENU + ".Other";

	/**
	 * Constant that defines the item 'Window - View message window'.
	 */
	public static final String VIEW_MESSAGEWINDOW_MENUITEM =
		VIEW_MENU + ".MessageWindow";

	/**
	 * Constant that defines the menu 'Tools'.
	 */
	public static final String TOOLS_MENU = "Tools";

	/**
	 * Constant that defines the item 'Tools - Learning'.
	 */
	public static final String LEARNING_MENUITEM = TOOLS_MENU + ".Learning";
	
	/**
	 * Constant that defines the item 'Tools - CostEffectivefness'.
	 */
	public static final String COSTEFFECTIVENESSDETERMINISTIC_MENUITEM = TOOLS_MENU + ".CostEffectivenessDeterministic";
	
	/**
	 * Constant that defines the item 'Tools - CostEffectivefness'.
	 */
	public static final String COSTEFFECTIVENESS_SUBMENU = TOOLS_MENU + ".CostEffectiveness";

	/** Constant that defines the item 'Tools - CostEffectivefness'.
	 * 
	 */
	 
	 public static final String SENSITIVITYANALYSIS_MENUITEM = TOOLS_MENU + ".SensitivityAnalysis";
	 	

	
	/**
	 * Constant that defines the item 'Tools - Configuration'.
	 */
	public static final String CONFIGURATION_MENUITEM =
		TOOLS_MENU + ".Configuration";
	
	/**
	 * Constant that defines the menu 'Options'.
	 */
	//public static final String OPTIONS_MENU = "Options"; //FOR FUTURE USE

	/**
	 * Constant that defines the menu 'Help'.
	 */
	public static final String HELP_MENU = "Help";

	/**
	 * Constant that defines the item 'Help - Help'.
	 */
	public static final String HELP_HELP_MENUITEM = HELP_MENU + ".Help";

	/**
	 * Constant that defines the item 'Help - Language'.
	 */
	public static final String HELP_CHANGELANGUAGE_MENUITEM =
		HELP_MENU + ".ChangeLanguage";

	/**
	 * Constant that defines the item 'Help - About'.
	 */
	public static final String HELP_ABOUT_MENUITEM = HELP_MENU + ".About";
	
	
	
	/**
	 * Constant that defines the item 'Assign'. Only (momently) in popupmenu
	 */
	public static final String UNCERTAINTY_ASSIGN_MENUITEM = 
		"Uncertainty.Assign";

	/**
	 * Constant that defines the item 'Edit'. Only (momently) in popupmenu
	 */
	public static final String UNCERTAINTY_EDIT_MENUITEM =
		 "Uncertainty.Edit";
	
	/**
	 * Constant that defines the item 'Remove'. Only (momently) in popupmenu
	 */
	public static final String UNCERTAINTY_REMOVE_MENUITEM =
		 "Uncertainty.Remove";

	public static final String EDIT_LOG_MENUITEM = EDIT_MENU + ".Log";

	

	/**
	 * Object that represents the menu 'File'.
	 */
	private JMenu fileMenu = null;

	/**
	 * Object that represents the item 'File - New'.
	 */
	private JMenuItem fileNewMenuItem = null;

	/**
	 * Object that represents the item 'File - Open'.
	 */
	private JMenuItem fileOpenMenuItem = null;

	/**
	 * Object that represents the item 'File - Save'.
	 */
	private JMenuItem fileSaveMenuItem = null;
	
	/**
	 * Object that represents the item 'File - Save and Reopen'.
	 */
	private JMenuItem fileSaveOpenMenuItem = null;

	/**
	 * Object that represents the item 'File - Save as'.
	 */
	private JMenuItem fileSaveAsMenuItem = null;

	/**
	 * Object that represents the item 'File - Close'.
	 */
	private JMenuItem fileCloseMenuItem = null;

	/**
	 * Object that represents the item 'File - Network adittionalProperties'.
	 */
	private JMenuItem fileNetworkPropertiesMenuItem = null;

	/**
	 * Object that represents the item 'File - Exit'.
	 */
	private JMenuItem fileExitMenuItem = null;

	/**
	 * Object that represents the menu 'Edit'.
	 */
	private JMenu editMenu = null;

	/**
	 * Object that represents the item 'Edit - Undo'.
	 */
	private JMenuItem editUndoMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Redo'.
	 */
	private JMenuItem editRedoMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Cut'.
	 */
	private JMenuItem editCutMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Copy'.
	 */
	private JMenuItem editCopyMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Paste'.
	 */
	private JMenuItem editPasteMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Remove'.
	 */
	private JMenuItem editRemoveMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Select all'.
	 */
	private JMenuItem editSelectAllMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Object selection'.
	 */
	private JCheckBoxMenuItem editObjectSelectionMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Chance nodes creation'.
	 */
	private JCheckBoxMenuItem editChanceCreationMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Decision nodes creation'.
	 */
	private JCheckBoxMenuItem editDecisionCreationMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Utility nodes creation'.
	 */
	private JCheckBoxMenuItem editUtilityCreationMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Links creation'.
	 */
	private JCheckBoxMenuItem editLinkCreationMenuItem = null;

	/**
	 * Object used to make autoexclusive the different select options.
	 */
	private ButtonGroup groupEditOptions = new ButtonGroup();

	/**
	 * Object that represents the item 'Edit - Node adittionalProperties'.
	 */
	private JMenuItem editNodePropertiesMenuItem = null;
	
	/**
	 * Object that represents the item 'Edit - Node Relation Table'.
	 */
	private JMenuItem editRelationMenuItem = null;

	/**
	 * Object that represents the item 'Edit - Link adittionalProperties'.
	 */
	private JMenuItem editLinkPropertiesMenuItem = null;
	
	/**
	 * Object that represents the item 'Edit - Switch to Inference mode'.
	 */
	private JMenuItem editSwitchToInferenceModeMenuItem = null;
	
	/**
	 * Object that represents the menu 'Inference'.
	 */
	private JMenu inferenceMenu = null;
	
	/**
	 * Object that represents the item 'Inference - Switch to Edition mode'.
	 */
	private JMenuItem inferenceSwitchToEditionModeMenuItem = null;
	
	/**
	 * Object that represents the item 'Inference - Inference Options'.
	 */
	private JMenuItem inferenceOptionsMenuItem = null;
	
	/**
	 * Object that represents the item 'Inference - Create New Evidence Case'.
	 */
	private JMenuItem inferenceCreateNewEvidenceCaseMenuItem = null;
	
	/**
	 * Object that represents the item 'Inference - Go To First Evidence Case'.
	 */
	private JMenuItem inferenceGoToFirstEvidenceCaseMenuItem = null;
	
	/**
	 * Object that represents the item 'Inference - Go To Previous Evidence Case'.
	 */
	private JMenuItem inferenceGoToPreviousEvidenceCaseMenuItem = null;
	
	/**
	 * Object that represents the item 'Inference - Go To Next Evidence Case'.
	 */
	private JMenuItem inferenceGoToNextEvidenceCaseMenuItem = null;
	
	/**
	 * Object that represents the item 'Inference - Go To Last Evidence Case'.
	 */
	private JMenuItem inferenceGoToLastEvidenceCaseMenuItem = null;
	
	/**
	 * Object that represents the item 'Inference - Clear Out All Evidence Cases'.
	 */
	private JMenuItem inferenceClearOutAllEvidenceCasesMenuItem = null;
	
	/**
	 * Object that represents the item 'Inference - PropagateEvidence'.
	 */
	private JMenuItem inferencePropagateEvidenceMenuItem = null;

	/**
	 * Object that represents the item 'Inference - ExpandNode'.
	 */
	private JMenuItem inferenceExpandNodeMenuItem = null;	

	/**
	 * Object that represents the item 'Inference - ContractNode'.
	 */
	private JMenuItem inferenceContractNodeMenuItem = null;

	/**
	 * Object that represents the item 'Inference - RemoveAllFindings'.
	 */
	private JMenuItem inferenceRemoveAllFindingsMenuItem = null;

	/**
	 * Object that represents the menu 'View'.
	 */
	private JMenu viewMenu = null;

	/**
	 * Object that represents the menu 'View - Nodes'.
	 */
	private JMenu viewNodesMenu = null;

	/**
	 * Object that represents the item 'View - Nodes - ByName'.
	 */
	private JCheckBoxMenuItem viewNodesByNameMenuItem = null;

	/**
	 * Object that represents the item 'View - Nodes - ByTitle'.
	 */
	private JCheckBoxMenuItem viewNodesByTitleMenuItem = null;

	/**
	 * Object used to make autoexclusive the options 'ByName' and 'ByTitle'.
	 */
	private ButtonGroup groupByNameByTitle = new ButtonGroup();

	/**
	 * Object that represents the menu 'View - Zoom'.
	 */
	private JMenu viewZoomMenu = null;

	/**
	 * Object that represents the item 'View - Zoom - Zoom in'.
	 */
	private JMenuItem viewZoomInMenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - Zoom out'.
	 */
	private JMenuItem viewZoomOutMenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - 500%'.
	 */
	private JCheckBoxMenuItem viewZoom500MenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - 200%'.
	 */
	private JCheckBoxMenuItem viewZoom200MenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - 150%'.
	 */
	private JCheckBoxMenuItem viewZoom150MenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - 100%'.
	 */
	private JCheckBoxMenuItem viewZoom100MenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - 75%'.
	 */
	private JCheckBoxMenuItem viewZoom75MenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - 50%'.
	 */
	private JCheckBoxMenuItem viewZoom50MenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - 25%'.
	 */
	private JCheckBoxMenuItem viewZoom25MenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - 10%'.
	 */
	private JCheckBoxMenuItem viewZoom10MenuItem = null;

	/**
	 * Object that represents the item 'View - Zoom - Other'.
	 */
	private JCheckBoxMenuItem viewZoomOtherMenuItem = null;

	/**
	 * Object used to make autoexclusive the zoom values.
	 */
	private ButtonGroup groupZoom = new ButtonGroup();

	/**
	 * Object that represents the item 'View - Message window'.
	 */
	private JMenuItem viewMessageWindowMenuItem = null;

	/**
	 * Object that represents the menu 'Tools'.
	 */
	private JMenu toolsMenu = null;

	/**
	 * Object that represents the item 'Tools - Learning'.
	 */
	private JMenuItem toolsLearningMenuItem = null;

	/**
	 * Object that represents the item 'Tools - Configuration'.
	 */
	private JMenuItem toolsConfigurationMenuItem = null;
	
	/**
	 * Object that represents the item 'Tools - CostEffectiveness analysis'.
	 */
	private JMenuItem toolsCostEffectivenessDeterministicMenuItem = null;
	
		/**
	 * Object that represents the item 'Tools - CostEffectiveness analysis'.
	 */
	private JMenuItem toolsSensitivityAnalysisMenuItem = null;
	
	/**
	 * Object that represents the menu 'Options'.
	 */
	//private JMenu optionsMenu = null; //FOR FUTURE USE
	
	/**
	 * Object that represents the menu 'Help'.
	 */
	private JMenu helpMenu = null;

	/**
	 * Object that represents the item 'Help - Help'.
	 */
	private JMenuItem helpOpenHelpMenuItem = null;

	/**
	 * Object that represents the item 'Help - ChangeLanguage'.
	 */
	private JMenuItem helpOpenChangeLanguageMenuItem = null;

	/**
	 * Object that represents the item 'Help - About'.
	 */
	private JMenuItem helpOpenAboutMenuItem = null;

	/**
	 * Object that is filled the MDI class.
	 */
	private JMenu menuMDI = null;

	/**
	 * String resource.
	 */
	private StringResource stringResource = null;

	/**
	 * Icon loader.
	 */
	private IconLoader iconLoader = null;

	/**
	 * Set of menu items and their default texts.
	 */
	HashMap<JComponent, String> defaultText = new HashMap<JComponent, String>();

	/**
	 * Object that listen to the user's actions.
	 */
	private ActionListener listener;

	/**
	 * Menu option for testing
	 */
	private JMenuItem editTestMenuItem = null;

	private JMenu toolsCostEffectivenessMenuItem;

	/**
	 * last open file index
	 */
	//private int lastOpenFileIndex = 0;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param newListener
	 *            listener of the user's actions.
	 */
	public MainMenu(ActionListener newListener) {

		listener = newListener;
		initialize();

	}

	/**
	 * This method initialises the instance.
	 */
	private void initialize() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMenus();
		iconLoader = new IconLoader();

		add(getFileMenu());
		add(getEditMenu());
		add(getInferenceMenu());
		add(getViewMenu());
		add(getMenuMDI());
		add(getToolsMenu());
		//add(getOptionsMenu()); //FOR FUTURE USE
		add(getHelpingMenu());

	}

	/**
	 * This method initialises fileMenu.
	 * 
	 * @return a new File menu.
	 */
	private JMenu getFileMenu() {

		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setName(FILE_MENU);
			fileMenu
				.setText(stringResource.getString(FILE_MENU + LABEL_SUFFIX));
			fileMenu.setMnemonic(stringResource.getString(
				FILE_MENU + MNEMONIC_SUFFIX).charAt(0));
			getBasicFileMenu();
			getLastOpenFiles();

		}

		return fileMenu;

	}
	
	/**
	 * get all the menu items for the basic File Menu (without Last Open Files)
	 */
	private void getBasicFileMenu() {
		fileMenu.add(getFileNewMenuItem());
		fileMenu.add(getFileOpenMenuItem());
		fileMenu.addSeparator();
		fileMenu.add(getFileSaveMenuItem());
		fileMenu.add(getFileSaveOpenMenuItem());
		fileMenu.add(getFileSaveAsMenuItem());
		fileMenu.addSeparator();
		fileMenu.add(getFileCloseMenuItem());
		fileMenu.addSeparator();
		fileMenu.add(getFileNetworkPropertiesMenuItem());
		fileMenu.addSeparator();
		fileMenu.add(getFileExitMenuItem());
	}

	/**
	 * This method initialises fileNewMenuItem.
	 * 
	 * @return a new item 'File - New'.
	 */
	private JMenuItem getFileNewMenuItem() {

		if (fileNewMenuItem == null) {
			fileNewMenuItem = new JMenuItem();
			fileNewMenuItem.setName(FILE_NEW_MENUITEM);
			fileNewMenuItem.setText(stringResource.getString(FILE_NEW_MENUITEM
				+ LABEL_SUFFIX));
			fileNewMenuItem.setMnemonic(stringResource.getString(
				FILE_NEW_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			fileNewMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_NEW_ENABLED));
			fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
			fileNewMenuItem.setActionCommand(ActionCommands.NEW_NETWORK);

			fileNewMenuItem.addActionListener(listener);

		}

		return fileNewMenuItem;

	}

	/**
	 * This method initialises fileOpenMenuItem.
	 * 
	 * @return a new item 'File - Open'.
	 */
	private JMenuItem getFileOpenMenuItem() {

		if (fileOpenMenuItem == null) {
			fileOpenMenuItem = new JMenuItem();
			fileOpenMenuItem.setName(FILE_OPEN_MENUITEM);
			fileOpenMenuItem.setText(stringResource
				.getString(FILE_OPEN_MENUITEM + LABEL_SUFFIX));
			fileOpenMenuItem.setMnemonic(stringResource.getString(
				FILE_OPEN_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			fileOpenMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_OPEN_ENABLED));
			fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
			fileOpenMenuItem.setActionCommand(ActionCommands.OPEN_NETWORK);
			fileOpenMenuItem.addActionListener(listener);
		}

		return fileOpenMenuItem;

	}

	/**
	 * This method initialises fileSaveMenuItem.
	 * 
	 * @return a new item 'File - Save'.
	 */
	private JMenuItem getFileSaveMenuItem() {

		if (fileSaveMenuItem == null) {
			fileSaveMenuItem = new JMenuItem();
			fileSaveMenuItem.setName(FILE_SAVE_MENUITEM);
			fileSaveMenuItem.setText(stringResource
				.getString(FILE_SAVE_MENUITEM + LABEL_SUFFIX));
			fileSaveMenuItem.setMnemonic(stringResource.getString(
				FILE_SAVE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			fileSaveMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_SAVE_ENABLED));
			fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
			fileSaveMenuItem.setActionCommand(ActionCommands.SAVE_NETWORK);
			fileSaveMenuItem.addActionListener(listener);
		}

		return fileSaveMenuItem;

	}
	/**
	 * This method initializes fileSaveMenuItem.
	 * 
	 * @return a new item 'File - Save'.
	 */
	private JMenuItem getFileSaveOpenMenuItem() {

		if (fileSaveOpenMenuItem == null) {
			fileSaveOpenMenuItem = new JMenuItem();
			fileSaveOpenMenuItem.setName(FILE_SAVE_OPEN_MENUITEM);
			fileSaveOpenMenuItem.setText(stringResource
				.getString(FILE_SAVE_OPEN_MENUITEM + LABEL_SUFFIX));
			fileSaveOpenMenuItem.setMnemonic(stringResource.getString(
					FILE_SAVE_OPEN_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			fileSaveOpenMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_SAVE_ENABLED));
			fileSaveOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
			fileSaveOpenMenuItem.setActionCommand(ActionCommands.SAVE_OPEN_NETWORK);
			fileSaveOpenMenuItem.addActionListener(listener);
		}

		return fileSaveOpenMenuItem;

	}

	/**
	 * This method initialises fileSaveAsMenuItem.
	 * 
	 * @return a new item 'File - Save as'.
	 */
	private JMenuItem getFileSaveAsMenuItem() {

		if (fileSaveAsMenuItem == null) {
			fileSaveAsMenuItem = new JMenuItem();
			fileSaveAsMenuItem.setName(FILE_SAVEAS_MENUITEM);
			fileSaveAsMenuItem.setText(stringResource
				.getString(FILE_SAVEAS_MENUITEM + LABEL_SUFFIX));
			fileSaveAsMenuItem.setMnemonic(stringResource.getString(
				FILE_SAVEAS_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			fileSaveAsMenuItem.setActionCommand(ActionCommands.SAVEAS_NETWORK);
			fileSaveAsMenuItem.addActionListener(listener);
		}

		return fileSaveAsMenuItem;

	}

	/**
	 * This method initialises fileCloseMenuItem.
	 * 
	 * @return a new item 'File - Close'.
	 */
	private JMenuItem getFileCloseMenuItem() {

		if (fileCloseMenuItem == null) {
			fileCloseMenuItem = new JMenuItem();
			fileCloseMenuItem.setName(FILE_CLOSE_MENUITEM);
			fileCloseMenuItem.setText(stringResource
				.getString(FILE_CLOSE_MENUITEM + LABEL_SUFFIX));
			fileCloseMenuItem.setMnemonic(stringResource.getString(
				FILE_CLOSE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			fileCloseMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_CLOSE_ENABLED));
			fileCloseMenuItem.setActionCommand(ActionCommands.CLOSE_NETWORK);
			fileCloseMenuItem.addActionListener(listener);
		}

		return fileCloseMenuItem;

	}

	/**
	 * This method initialises fileNetworkPropertiesMenuItem.
	 * 
	 * @return a new item 'File - Network adittionalProperties'.
	 */
	private JMenuItem getFileNetworkPropertiesMenuItem() {

		if (fileNetworkPropertiesMenuItem == null) {
			fileNetworkPropertiesMenuItem = new JMenuItem();
			fileNetworkPropertiesMenuItem
				.setName(FILE_NETWORKPROPERTIES_MENUITEM);
			fileNetworkPropertiesMenuItem.setText(stringResource
				.getString(FILE_NETWORKPROPERTIES_MENUITEM + LABEL_SUFFIX));
			fileNetworkPropertiesMenuItem.setMnemonic(stringResource.getString(
				FILE_NETWORKPROPERTIES_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			fileNetworkPropertiesMenuItem
				.setActionCommand(ActionCommands.NETWORK_PROPERTIES);
			fileNetworkPropertiesMenuItem.addActionListener(listener);
		}

		return fileNetworkPropertiesMenuItem;

	}

	/**
	 * This method initialises fileExitMenuItem.
	 * 
	 * @return a new item 'File - Exit'.
	 */
	private JMenuItem getFileExitMenuItem() {

		if (fileExitMenuItem == null) {
			fileExitMenuItem = new JMenuItem();
			fileExitMenuItem.setName(FILE_EXIT_MENUITEM);
			fileExitMenuItem.setText(stringResource
				.getString(FILE_EXIT_MENUITEM + LABEL_SUFFIX));
			fileExitMenuItem.setMnemonic(stringResource.getString(
				FILE_EXIT_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			fileExitMenuItem.setActionCommand(ActionCommands.EXIT_APPLICATION);
			fileExitMenuItem.addActionListener(listener);
		}

		return fileExitMenuItem;

	}

	/**
	 * This method retrieves the LastOpenFiles and show them in the File Menu
	 * 
	 * @return a new set of items
	 */
	private void getLastOpenFiles() {

		int index, lastIndex;
		LastRecentFilesMenuItem item = null;
		LastOpenFiles lastOpenFiles = new LastOpenFiles();

		if (lastOpenFiles.existLastOpenFiles()) {

			fileMenu.addSeparator();
			lastIndex = lastOpenFiles.getOldestOpenFileIndex();
			//lastOpenFileIndex = lastIndex;
			for (index = 1; index <= lastIndex; index++) {
				item = new LastRecentFilesMenuItem();
				item.setName("lastRecentFilesMenuItem" + index);
				item
					.setText(index + " - " + lastOpenFiles.getFileNameAt(index));
				switch (index) {
				case 1:
					item.setActionCommand(ActionCommands.OPEN_LAST_1_FILE);
					break;
				case 2:
					item.setActionCommand(ActionCommands.OPEN_LAST_2_FILE);
					break;
				case 3:
					item.setActionCommand(ActionCommands.OPEN_LAST_3_FILE);
					break;
				case 4:
					item.setActionCommand(ActionCommands.OPEN_LAST_4_FILE);
					break;
				case 5:
					item.setActionCommand(ActionCommands.OPEN_LAST_5_FILE);
					break;
				default:

				}
				item.addActionListener(listener);
				fileMenu.add(item);
			}
 		}
 
	}

	/**
	 * This method reset the LastOpenFiles set of items in the File Menu
	 * 
	 * @return a new set of items
	 */
	public void rechargeLastOpenFiles() {
		 fileMenu.removeAll();
		 getBasicFileMenu();
		 getLastOpenFiles();
		 fileMenu.repaint();
	}

	/**
	 * This method initialises editMenu.
	 * 
	 * @return a new Edit menu.
	 */
	private JMenu getEditMenu() {

		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setName(EDIT_MENU);
			editMenu
				.setText(stringResource.getString(EDIT_MENU + LABEL_SUFFIX));
			editMenu.setMnemonic(stringResource.getString(
				EDIT_MENU + MNEMONIC_SUFFIX).charAt(0));
			editMenu.add(getEditCutMenuItem());
			editMenu.add(getEditCopyMenuItem());
			editMenu.add(getEditPasteMenuItem());
			editMenu.add(getEditRemoveMenuItem());
			editMenu.addSeparator();
			editMenu.add(getEditUndoMenuItem());
			editMenu.add(getEditRedoMenuItem());
			editMenu.addSeparator();
			editMenu.add(getEditSelectAllMenuItem());
			editMenu.addSeparator();
			editMenu.add(getEditObjectSelectionMenuItem());
			editMenu.add(getEditChanceCreationMenuItem());
			editMenu.add(getEditDecisionCreationMenuItem());
			editMenu.add(getEditUtilityCreationMenuItem());
			editMenu.add(getEditLinkCreationMenuItem());
			editMenu.addSeparator();
			editMenu.add(getEditNodePropertiesMenuItem());
			editMenu.add(getEditRelationMenuItem());
			//Menu option for test
			editMenu.add(getTestMenuItem());
			editMenu.addSeparator();
			editMenu.add(getEditSwitchToInferenceModeMenuItem());			
			
			/*
			 * This item must be added to the menu when is active the
			 * possibility of editing the adittionalProperties of a link in future
			 * versions.
			 */
			// editMenu.add(getEditLinkPropertiesMenuItem());
			getEditLinkPropertiesMenuItem();
		}

		return editMenu;

	}

	/**
	 * This method initialises editCutMenuItem.
	 * 
	 * @return a new item 'Edit - Cut'.
	 */
	private JMenuItem getEditCutMenuItem() {

		if (editCutMenuItem == null) {
			editCutMenuItem = new JMenuItem();
			editCutMenuItem.setName(EDIT_CUT_MENUITEM);
			editCutMenuItem.setText(stringResource.getString(EDIT_CUT_MENUITEM
				+ LABEL_SUFFIX));
			editCutMenuItem.setMnemonic(stringResource.getString(
				EDIT_CUT_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editCutMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_CUT_ENABLED));
			editCutMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
			editCutMenuItem.setActionCommand(ActionCommands.CLIPBOARD_CUT);
			editCutMenuItem.addActionListener(listener);
		}

		return editCutMenuItem;

	}

	/**
	 * This method initialises editCopyMenuItem.
	 * 
	 * @return a new item 'Edit - Copy'.
	 */
	private JMenuItem getEditCopyMenuItem() {

		if (editCopyMenuItem == null) {
			editCopyMenuItem = new JMenuItem();
			editCopyMenuItem.setName(EDIT_COPY_MENUITEM);
			editCopyMenuItem.setText(stringResource
				.getString(EDIT_COPY_MENUITEM + LABEL_SUFFIX));
			editCopyMenuItem.setMnemonic(stringResource.getString(
				EDIT_COPY_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editCopyMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_COPY_ENABLED));
			editCopyMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
			editCopyMenuItem.setActionCommand(ActionCommands.CLIPBOARD_COPY);
			editCopyMenuItem.addActionListener(listener);
		}

		return editCopyMenuItem;

	}

	/**
	 * This method initialises editPasteMenuItem.
	 * 
	 * @return a new item 'Edit - Paste'.
	 */
	private JMenuItem getEditPasteMenuItem() {

		if (editPasteMenuItem == null) {
			editPasteMenuItem = new JMenuItem();
			editPasteMenuItem.setName(EDIT_PASTE_MENUITEM);
			editPasteMenuItem.setText(stringResource
				.getString(EDIT_PASTE_MENUITEM + LABEL_SUFFIX));
			editPasteMenuItem.setMnemonic(stringResource.getString(
				EDIT_PASTE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editPasteMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_PASTE_ENABLED));
			editPasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
			editPasteMenuItem.setActionCommand(ActionCommands.CLIPBOARD_PASTE);
			editPasteMenuItem.addActionListener(listener);
		}

		return editPasteMenuItem;

	}

	/**
	 * This method initialises editRemoveMenuItem.
	 * 
	 * @return a new item 'Edit - Remove'.
	 */
	private JMenuItem getEditRemoveMenuItem() {

		if (editRemoveMenuItem == null) {
			editRemoveMenuItem = new JMenuItem();
			editRemoveMenuItem.setName(EDIT_REMOVE_MENUITEM);
			editRemoveMenuItem.setText(stringResource
				.getString(EDIT_REMOVE_MENUITEM + LABEL_SUFFIX));
			editRemoveMenuItem.setMnemonic(stringResource.getString(
				EDIT_REMOVE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editRemoveMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_REMOVE_ENABLED));
			editRemoveMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_DELETE, 0));
			editRemoveMenuItem.setActionCommand(ActionCommands.OBJECT_REMOVAL);
			editRemoveMenuItem.addActionListener(listener);
		}

		return editRemoveMenuItem;

	}

	/**
	 * This method initialises editUndoMenuItem.
	 * 
	 * @return a new item 'Edit - Undo'.
	 */
	private JMenuItem getEditUndoMenuItem() {

		if (editUndoMenuItem == null) {
			editUndoMenuItem = new JMenuItem();
			editUndoMenuItem.setName(EDIT_UNDO_MENUITEM);
			editUndoMenuItem.setText(stringResource
				.getString(EDIT_UNDO_MENUITEM + LABEL_SUFFIX));
			defaultText.put(editUndoMenuItem, editUndoMenuItem.getText());
			editUndoMenuItem.setMnemonic(stringResource.getString(
				EDIT_UNDO_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editUndoMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_UNDO_ENABLED));
			editUndoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
			editUndoMenuItem.setActionCommand(ActionCommands.UNDO);
			editUndoMenuItem.addActionListener(listener);
		}

		return editUndoMenuItem;

	}

	/**
	 * This method initialises editRedoMenuItem.
	 * 
	 * @return a new item 'Edit - Redo'.
	 */
	private JMenuItem getEditRedoMenuItem() {

		if (editRedoMenuItem == null) {
			editRedoMenuItem = new JMenuItem();
			editRedoMenuItem.setName(EDIT_REDO_MENUITEM);
			editRedoMenuItem.setText(stringResource
				.getString(EDIT_REDO_MENUITEM + LABEL_SUFFIX));
			defaultText.put(editRedoMenuItem, editRedoMenuItem.getText());
			editRedoMenuItem.setMnemonic(stringResource.getString(
				EDIT_REDO_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editRedoMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_REDO_ENABLED));
			editRedoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
			editRedoMenuItem.setActionCommand(ActionCommands.REDO);
			editRedoMenuItem.addActionListener(listener);
		}

		return editRedoMenuItem;

	}

	/**
	 * This method initialises editSelectAllMenuItem.
	 * 
	 * @return a new item 'Edit - Select all'.
	 */
	private JMenuItem getEditSelectAllMenuItem() {

		if (editSelectAllMenuItem == null) {
			editSelectAllMenuItem = new JMenuItem();
			editSelectAllMenuItem.setName(EDIT_SELECTALL_MENUITEM);
			editSelectAllMenuItem.setText(stringResource
				.getString(EDIT_SELECTALL_MENUITEM + LABEL_SUFFIX));
			editSelectAllMenuItem.setMnemonic(stringResource.getString(
				EDIT_SELECTALL_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editSelectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
			editSelectAllMenuItem.setActionCommand(ActionCommands.SELECT_ALL);
			editSelectAllMenuItem.addActionListener(listener);
		}

		return editSelectAllMenuItem;

	}

	/**
	 * This method initialises editObjectSelectionMenuItem.
	 * 
	 * @return a new item 'Edit - Object selection'.
	 */
	private JCheckBoxMenuItem getEditObjectSelectionMenuItem() {

		if (editObjectSelectionMenuItem == null) {
			editObjectSelectionMenuItem = new JCheckBoxMenuItem();
			editObjectSelectionMenuItem.setName(EDIT_OBJECTSELECTION_MENUITEM);
			editObjectSelectionMenuItem.setText(stringResource
				.getString(EDIT_OBJECTSELECTION_MENUITEM + LABEL_SUFFIX));
			editObjectSelectionMenuItem.setMnemonic(stringResource.getString(
				EDIT_OBJECTSELECTION_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editObjectSelectionMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_SELECTION_ENABLED));
			editObjectSelectionMenuItem
				.setActionCommand(ActionCommands.OBJECT_SELECTION);
			editObjectSelectionMenuItem.addActionListener(listener);
			groupEditOptions.add(editObjectSelectionMenuItem);
		}

		return editObjectSelectionMenuItem;

	}

	/**
	 * This method initialises editChanceCreationMenuItem.
	 * 
	 * @return a new item 'Edit - Chance nodes creation'.
	 */
	private JCheckBoxMenuItem getEditChanceCreationMenuItem() {

		if (editChanceCreationMenuItem == null) {
			editChanceCreationMenuItem = new JCheckBoxMenuItem();
			editChanceCreationMenuItem.setName(EDIT_CHANCECREATION_MENUITEM);
			editChanceCreationMenuItem.setText(stringResource
				.getString(EDIT_CHANCECREATION_MENUITEM + LABEL_SUFFIX));
			editChanceCreationMenuItem.setMnemonic(stringResource.getString(
				EDIT_CHANCECREATION_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editChanceCreationMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_CHANCE_ENABLED));
			editChanceCreationMenuItem
				.setActionCommand(ActionCommands.CHANCE_CREATION);
			editChanceCreationMenuItem.addActionListener(listener);
			groupEditOptions.add(editChanceCreationMenuItem);
		}

		return editChanceCreationMenuItem;

	}

	/**
	 * This method initialises editDecisionCreationMenuItem.
	 * 
	 * @return a new item 'Edit - Decision nodes creation'.
	 */
	private JCheckBoxMenuItem getEditDecisionCreationMenuItem() {

		if (editDecisionCreationMenuItem == null) {
			editDecisionCreationMenuItem = new JCheckBoxMenuItem();
			editDecisionCreationMenuItem
				.setName(EDIT_DECISIONCREATION_MENUITEM);
			editDecisionCreationMenuItem.setText(stringResource
				.getString(EDIT_DECISIONCREATION_MENUITEM + LABEL_SUFFIX));
			editDecisionCreationMenuItem.setMnemonic(stringResource.getString(
				EDIT_DECISIONCREATION_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editDecisionCreationMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_DECISION_ENABLED));
			editDecisionCreationMenuItem
				.setActionCommand(ActionCommands.DECISION_CREATION);
			editDecisionCreationMenuItem.addActionListener(listener);
			groupEditOptions.add(editDecisionCreationMenuItem);
		}

		return editDecisionCreationMenuItem;

	}

	/**
	 * This method initialises editUtilityCreationMenuItem.
	 * 
	 * @return a new item 'Edit - Utility nodes creation'.
	 */
	private JCheckBoxMenuItem getEditUtilityCreationMenuItem() {

		if (editUtilityCreationMenuItem == null) {
			editUtilityCreationMenuItem = new JCheckBoxMenuItem();
			editUtilityCreationMenuItem.setName(EDIT_UTILITYCREATION_MENUITEM);
			editUtilityCreationMenuItem.setText(stringResource
				.getString(EDIT_UTILITYCREATION_MENUITEM + LABEL_SUFFIX));
			editUtilityCreationMenuItem.setMnemonic(stringResource.getString(
				EDIT_UTILITYCREATION_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editUtilityCreationMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_UTILITY_ENABLED));
			editUtilityCreationMenuItem
				.setActionCommand(ActionCommands.UTILITY_CREATION);
			editUtilityCreationMenuItem.addActionListener(listener);
			groupEditOptions.add(editUtilityCreationMenuItem);
		}

		return editUtilityCreationMenuItem;

	}

	/**
	 * This method initialises editLinkCreationMenuItem.
	 * 
	 * @return a new item 'Edit - Links creation'.
	 */
	private JCheckBoxMenuItem getEditLinkCreationMenuItem() {

		if (editLinkCreationMenuItem == null) {
			editLinkCreationMenuItem = new JCheckBoxMenuItem();
			editLinkCreationMenuItem.setName(EDIT_LINKCREATION_MENUITEM);
			editLinkCreationMenuItem.setText(stringResource
				.getString(EDIT_LINKCREATION_MENUITEM + LABEL_SUFFIX));
			editLinkCreationMenuItem.setMnemonic(stringResource.getString(
				EDIT_LINKCREATION_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editLinkCreationMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_LINK_ENABLED));
			editLinkCreationMenuItem
				.setActionCommand(ActionCommands.LINK_CREATION);
			editLinkCreationMenuItem.addActionListener(listener);
			groupEditOptions.add(editLinkCreationMenuItem);
		}

		return editLinkCreationMenuItem;

	}

	/**
	 * This method initialises editNodePropertiesMenuItem.
	 * 
	 * @return a new item 'Edit - Node adittionalProperties'.
	 */
	private JMenuItem getEditNodePropertiesMenuItem() {

		if (editNodePropertiesMenuItem == null) {
			editNodePropertiesMenuItem = new JMenuItem();
			editNodePropertiesMenuItem.setName(EDIT_NODEPROPERTIES_MENUITEM);
			editNodePropertiesMenuItem.setText(stringResource
				.getString(EDIT_NODEPROPERTIES_MENUITEM + LABEL_SUFFIX));
			editNodePropertiesMenuItem.setMnemonic(stringResource.getString(
				EDIT_NODEPROPERTIES_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editNodePropertiesMenuItem
				.setActionCommand(ActionCommands.NODE_PROPERTIES);
			editNodePropertiesMenuItem.addActionListener(listener);
		}

		return editNodePropertiesMenuItem;

	}
	
	/**
	 * This method initialises editNodeRelationMenuItem.
	 * 
	 * @return a new item 'Edit - Node Relation Table'.
	 */
	private JMenuItem getEditRelationMenuItem() {

		if (editRelationMenuItem == null) {
			editRelationMenuItem = new JMenuItem();
			editRelationMenuItem.setName(EDIT_NODERELATION_MENUITEM);
			editRelationMenuItem.setText(stringResource
				.getString(EDIT_NODERELATION_MENUITEM + LABEL_SUFFIX));
			editRelationMenuItem.setMnemonic(stringResource.getString(
					EDIT_NODERELATION_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editRelationMenuItem
				.setActionCommand(ActionCommands.CHANGE_POTENTIAL);
			editRelationMenuItem.addActionListener(listener);
		}

		return editRelationMenuItem;

	}
	/**
	 * This method initialises editTestMenuItem.
	 * 
	 * @return a new item 'Edit - Node Relation Table'.
	 */
	private JMenuItem getTestMenuItem() {

		if (editTestMenuItem == null) {
			editTestMenuItem = new JMenuItem();
			editTestMenuItem.setName(EDIT_NODETEST_MENUITEM);
			editTestMenuItem.setText(stringResource
				.getString(EDIT_NODETEST_MENUITEM + LABEL_SUFFIX));
			editTestMenuItem.setMnemonic(stringResource.getString(
					EDIT_NODETEST_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editTestMenuItem
				.setActionCommand(ActionCommands.TEST);
			editTestMenuItem.addActionListener(listener);
		}

		return editTestMenuItem;

	}
	
	/**
	 * This method initialises editLinkPropertiesMenuItem.
	 * 
	 * @return a new item 'Edit - Link adittionalProperties'.
	 */
	private JMenuItem getEditLinkPropertiesMenuItem() {

		if (editLinkPropertiesMenuItem == null) {
			editLinkPropertiesMenuItem = new JMenuItem();
			editLinkPropertiesMenuItem.setName(EDIT_LINKPROPERTIES_MENUITEM);
			editLinkPropertiesMenuItem.setText(stringResource
				.getString(EDIT_LINKPROPERTIES_MENUITEM + LABEL_SUFFIX));
			editLinkPropertiesMenuItem.setMnemonic(stringResource.getString(
				EDIT_LINKPROPERTIES_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editLinkPropertiesMenuItem
				.setActionCommand(ActionCommands.LINK_PROPERTIES);
			editLinkPropertiesMenuItem.addActionListener(listener);
		}

		return editLinkPropertiesMenuItem;

	}
	
	/**
	 * This method initialises editSwitchToInferenceModeMenuItem.
	 * 
	 * @return a new item 'Edit - Switch to Inference mode'.
	 */
	private JMenuItem getEditSwitchToInferenceModeMenuItem() {
		if (editSwitchToInferenceModeMenuItem == null) {
			editSwitchToInferenceModeMenuItem = new JMenuItem();
			editSwitchToInferenceModeMenuItem.setName(EDIT_SWITCH_TO_INFERENCE_MODE_MENUITEM);
			editSwitchToInferenceModeMenuItem.setText(stringResource
				.getString(EDIT_SWITCH_TO_INFERENCE_MODE_MENUITEM + LABEL_SUFFIX));
			editSwitchToInferenceModeMenuItem.setMnemonic(stringResource.getString(
					EDIT_SWITCH_TO_INFERENCE_MODE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			editSwitchToInferenceModeMenuItem.setIcon(iconLoader
					.load(IconLoader.ICON_INFERENCE_MODE_ENABLED));
			editSwitchToInferenceModeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
			editSwitchToInferenceModeMenuItem
				.setActionCommand(ActionCommands.CHANGE_TO_INFERENCE_MODE);
			editSwitchToInferenceModeMenuItem.addActionListener(listener);
		}
		return editSwitchToInferenceModeMenuItem;
	}
		
	/**
	 * This method initialises inferenceMenu.
	 * 
	 * @return a new Inference menu.
	 */
	private JMenu getInferenceMenu() {
		if (inferenceMenu == null) {
			inferenceMenu = new JMenu();
			inferenceMenu.setName(INFERENCE_MENU);
			inferenceMenu
				.setText(stringResource.getString(INFERENCE_MENU + LABEL_SUFFIX));
			inferenceMenu.setMnemonic(stringResource.getString(
				INFERENCE_MENU + MNEMONIC_SUFFIX).charAt(0));
			inferenceMenu.add(getInferenceSwitchToEditionModeMenuItem());
			inferenceMenu.addSeparator();
			inferenceMenu.add(getInferenceOptionsMenuItem());
			inferenceMenu.addSeparator();
			inferenceMenu.add(getInferenceCreateNewEvidenceCaseMenuItem());
			inferenceMenu.add(getInferenceGoToFirstEvidenceCaseMenuItem());
			inferenceMenu.add(getInferenceGoToPreviousEvidenceCaseMenuItem());
			inferenceMenu.add(getInferenceGoToNextEvidenceCaseMenuItem());
			inferenceMenu.add(getInferenceGoToLastEvidenceCaseMenuItem());
			inferenceMenu.add(getInferenceClearOutAllEvidenceCasesMenuItem());
			inferenceMenu.addSeparator();
			inferenceMenu.add(getInferencePropagateEvidenceMenuItem());
			inferenceMenu.addSeparator();
			inferenceMenu.add(getInferenceExpandNodeMenuItem());
			inferenceMenu.add(getInferenceContractNodeMenuItem());
			inferenceMenu.addSeparator();
			inferenceMenu.add(getInferenceRemoveAllFindingsMenuItem());
		}
		return inferenceMenu;
	}
	
	/**
	 * This method initialises inferenceSwitchToEditionModeMenuItem.
	 * 
	 * @return a new item 'Inference - Switch to Edition mode'.
	 */
	private JMenuItem getInferenceSwitchToEditionModeMenuItem() {
		if (inferenceSwitchToEditionModeMenuItem == null) {
			inferenceSwitchToEditionModeMenuItem = new JMenuItem();
			inferenceSwitchToEditionModeMenuItem.setName(INFERENCE_SWITCH_TO_EDITION_MODE_MENUITEM);
			inferenceSwitchToEditionModeMenuItem.setText(stringResource
				.getString(INFERENCE_SWITCH_TO_EDITION_MODE_MENUITEM + LABEL_SUFFIX));
			inferenceSwitchToEditionModeMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_SWITCH_TO_EDITION_MODE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceSwitchToEditionModeMenuItem.setIcon(iconLoader
					.load(IconLoader.ICON_EDITION_MODE_ENABLED));
			inferenceSwitchToEditionModeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
			inferenceSwitchToEditionModeMenuItem
				.setActionCommand(ActionCommands.CHANGE_TO_EDITION_MODE);
			inferenceSwitchToEditionModeMenuItem.addActionListener(listener);
		}
		return inferenceSwitchToEditionModeMenuItem;
	}
	
	/**
	 * This method initialises inferenceOptionsMenuItem.
	 * 
	 * @return a new item 'Inference - Inference Options'.
	 */
	private JMenuItem getInferenceOptionsMenuItem() {
		if (inferenceOptionsMenuItem == null) {
			inferenceOptionsMenuItem = new JMenuItem();
			inferenceOptionsMenuItem.setName(INFERENCE_OPTIONS_MENUITEM);
			inferenceOptionsMenuItem.setText(stringResource
				.getString(INFERENCE_OPTIONS_MENUITEM + LABEL_SUFFIX));
			inferenceOptionsMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_OPTIONS_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceOptionsMenuItem
				.setActionCommand(ActionCommands.INFERENCE_OPTIONS);
			inferenceOptionsMenuItem.addActionListener(listener);
		}
		return inferenceOptionsMenuItem;
	}
	
	/**
	 * This method initialises inferenceCreateNewEvidenceCaseMenuItem.
	 * 
	 * @return a new item 'Inference - Create New Evidence Case'.
	 */
	private JMenuItem getInferenceCreateNewEvidenceCaseMenuItem() {
		if (inferenceCreateNewEvidenceCaseMenuItem == null) {
			inferenceCreateNewEvidenceCaseMenuItem = new JMenuItem();
			inferenceCreateNewEvidenceCaseMenuItem.setName(INFERENCE_CREATE_NEW_EVIDENCE_CASE_MENUITEM);
			inferenceCreateNewEvidenceCaseMenuItem.setText(stringResource
				.getString(INFERENCE_CREATE_NEW_EVIDENCE_CASE_MENUITEM + LABEL_SUFFIX));
			inferenceCreateNewEvidenceCaseMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_CREATE_NEW_EVIDENCE_CASE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceCreateNewEvidenceCaseMenuItem.setIcon(iconLoader
					.load(IconLoader.ICON_CREATE_NEW_EVIDENCE_CASE_ENABLED));
			inferenceCreateNewEvidenceCaseMenuItem
				.setActionCommand(ActionCommands.CREATE_NEW_EVIDENCE_CASE);
			inferenceCreateNewEvidenceCaseMenuItem.addActionListener(listener);
		}
		return inferenceCreateNewEvidenceCaseMenuItem;
	}	
	
	/**
	 * This method initialises inferenceGoToFirstEvidenceCaseMenuItem.
	 * 
	 * @return a new item 'Inference - Go To First Evidence Case'.
	 */
	private JMenuItem getInferenceGoToFirstEvidenceCaseMenuItem() {
		if (inferenceGoToFirstEvidenceCaseMenuItem == null) {
			inferenceGoToFirstEvidenceCaseMenuItem = new JMenuItem();
			inferenceGoToFirstEvidenceCaseMenuItem.setName(INFERENCE_GO_TO_FIRST_EVIDENCE_CASE_MENUITEM);
			inferenceGoToFirstEvidenceCaseMenuItem.setText(stringResource
				.getString(INFERENCE_GO_TO_FIRST_EVIDENCE_CASE_MENUITEM + LABEL_SUFFIX));
			inferenceGoToFirstEvidenceCaseMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_GO_TO_FIRST_EVIDENCE_CASE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceGoToFirstEvidenceCaseMenuItem.setIcon(iconLoader
					.load(IconLoader.ICON_GO_TO_FIRST_EVIDENCE_CASE_ENABLED));
			inferenceGoToFirstEvidenceCaseMenuItem
				.setActionCommand(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE);
			inferenceGoToFirstEvidenceCaseMenuItem.addActionListener(listener);
		}
		return inferenceGoToFirstEvidenceCaseMenuItem;
	}
	
	/**
	 * This method initialises inferenceGoToPreviousEvidenceCaseMenuItem.
	 * 
	 * @return a new item 'Inference - Go To Previous Evidence Case'.
	 */
	private JMenuItem getInferenceGoToPreviousEvidenceCaseMenuItem() {
		if (inferenceGoToPreviousEvidenceCaseMenuItem == null) {
			inferenceGoToPreviousEvidenceCaseMenuItem = new JMenuItem();
			inferenceGoToPreviousEvidenceCaseMenuItem.setName(INFERENCE_GO_TO_PREVIOUS_EVIDENCE_CASE_MENUITEM);
			inferenceGoToPreviousEvidenceCaseMenuItem.setText(stringResource
				.getString(INFERENCE_GO_TO_PREVIOUS_EVIDENCE_CASE_MENUITEM + LABEL_SUFFIX));
			inferenceGoToPreviousEvidenceCaseMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_GO_TO_PREVIOUS_EVIDENCE_CASE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceGoToPreviousEvidenceCaseMenuItem.setIcon(iconLoader
					.load(IconLoader.ICON_GO_TO_PREVIOUS_EVIDENCE_CASE_ENABLED));
			inferenceGoToPreviousEvidenceCaseMenuItem
				.setActionCommand(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE);
			inferenceGoToPreviousEvidenceCaseMenuItem.addActionListener(listener);
		}
		return inferenceGoToPreviousEvidenceCaseMenuItem;
	}
	
	/**
	 * This method initialises inferenceGoToNextEvidenceCaseMenuItem.
	 * 
	 * @return a new item 'Inference - Go To Next Evidence Case'.
	 */
	private JMenuItem getInferenceGoToNextEvidenceCaseMenuItem() {
		if (inferenceGoToNextEvidenceCaseMenuItem == null) {
			inferenceGoToNextEvidenceCaseMenuItem = new JMenuItem();
			inferenceGoToNextEvidenceCaseMenuItem.setName(INFERENCE_GO_TO_NEXT_EVIDENCE_CASE_MENUITEM);
			inferenceGoToNextEvidenceCaseMenuItem.setText(stringResource
				.getString(INFERENCE_GO_TO_NEXT_EVIDENCE_CASE_MENUITEM + LABEL_SUFFIX));
			inferenceGoToNextEvidenceCaseMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_GO_TO_NEXT_EVIDENCE_CASE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceGoToNextEvidenceCaseMenuItem.setIcon(iconLoader
					.load(IconLoader.ICON_GO_TO_NEXT_EVIDENCE_CASE_ENABLED));
			inferenceGoToNextEvidenceCaseMenuItem
				.setActionCommand(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE);
			inferenceGoToNextEvidenceCaseMenuItem.addActionListener(listener);
		}
		return inferenceGoToNextEvidenceCaseMenuItem;
	}
	
	/**
	 * This method initialises inferenceGoToLastEvidenceCaseMenuItem.
	 * 
	 * @return a new item 'Inference - Go To Last Evidence Case'.
	 */
	private JMenuItem getInferenceGoToLastEvidenceCaseMenuItem() {
		if (inferenceGoToLastEvidenceCaseMenuItem == null) {
			inferenceGoToLastEvidenceCaseMenuItem = new JMenuItem();
			inferenceGoToLastEvidenceCaseMenuItem.setName(INFERENCE_GO_TO_LAST_EVIDENCE_CASE_MENUITEM);
			inferenceGoToLastEvidenceCaseMenuItem.setText(stringResource
				.getString(INFERENCE_GO_TO_LAST_EVIDENCE_CASE_MENUITEM + LABEL_SUFFIX));
			inferenceGoToLastEvidenceCaseMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_GO_TO_LAST_EVIDENCE_CASE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceGoToLastEvidenceCaseMenuItem.setIcon(iconLoader
					.load(IconLoader.ICON_GO_TO_LAST_EVIDENCE_CASE_ENABLED));
			inferenceGoToLastEvidenceCaseMenuItem
				.setActionCommand(ActionCommands.GO_TO_LAST_EVIDENCE_CASE);
			inferenceGoToLastEvidenceCaseMenuItem.addActionListener(listener);
		}
		return inferenceGoToLastEvidenceCaseMenuItem;
	}	
	/**
	 * This method initialises inferenceClearOutAllEvidenceCasesMenuItem.
	 * 
	 * @return a new item 'Inference - Clear Out All Evidence Cases'.
	 */
	private JMenuItem getInferenceClearOutAllEvidenceCasesMenuItem() {
		if (inferenceClearOutAllEvidenceCasesMenuItem == null) {
			inferenceClearOutAllEvidenceCasesMenuItem = new JMenuItem();
			inferenceClearOutAllEvidenceCasesMenuItem.setName(INFERENCE_CLEAR_OUT_ALL_EVIDENCE_CASES_MENUITEM);
			inferenceClearOutAllEvidenceCasesMenuItem.setText(stringResource
				.getString(INFERENCE_CLEAR_OUT_ALL_EVIDENCE_CASES_MENUITEM + LABEL_SUFFIX));
			inferenceClearOutAllEvidenceCasesMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_CLEAR_OUT_ALL_EVIDENCE_CASES_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceClearOutAllEvidenceCasesMenuItem.setIcon(iconLoader
					.load(IconLoader.ICON_CLEAR_OUT_ALL_EVIDENCE_CASES_ENABLED));
			inferenceClearOutAllEvidenceCasesMenuItem
				.setActionCommand(ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES);
			inferenceClearOutAllEvidenceCasesMenuItem.addActionListener(listener);
		}
		return inferenceClearOutAllEvidenceCasesMenuItem;
	}
	
	/**
	 * This method initialises inferencePropagateEvidenceMenuItem.
	 * 
	 * @return a new item 'Inference - Switch to Edition mode'.
	 */
	private JMenuItem getInferencePropagateEvidenceMenuItem() {
		if (inferencePropagateEvidenceMenuItem == null) {
			inferencePropagateEvidenceMenuItem = new JMenuItem();
			inferencePropagateEvidenceMenuItem.setName(INFERENCE_PROPAGATE_EVIDENCE_MENUITEM);
			inferencePropagateEvidenceMenuItem.setText(stringResource
				.getString(INFERENCE_PROPAGATE_EVIDENCE_MENUITEM + LABEL_SUFFIX));
			inferencePropagateEvidenceMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_PROPAGATE_EVIDENCE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferencePropagateEvidenceMenuItem.setIcon(iconLoader
					.load(IconLoader.ICON_PROPAGATE_EVIDENCE_ENABLED));
			inferencePropagateEvidenceMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
			inferencePropagateEvidenceMenuItem
				.setActionCommand(ActionCommands.PROPAGATE_EVIDENCE);
			inferencePropagateEvidenceMenuItem.addActionListener(listener);
		}
		return inferencePropagateEvidenceMenuItem;
	}
	
	/**
	 * This method initialises inferenceExpandNodeMenuItem.
	 * 
	 * @return a new item 'Inference - ExpandNode'.
	 */
	private JMenuItem getInferenceExpandNodeMenuItem() {
		if (inferenceExpandNodeMenuItem == null) {
			inferenceExpandNodeMenuItem = new JMenuItem();
			inferenceExpandNodeMenuItem.setName(INFERENCE_EXPAND_NODE_MENUITEM);
			inferenceExpandNodeMenuItem.setText(stringResource
				.getString(INFERENCE_EXPAND_NODE_MENUITEM + LABEL_SUFFIX));
			inferenceExpandNodeMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_EXPAND_NODE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceExpandNodeMenuItem
				.setActionCommand(ActionCommands.NODE_EXPANSION);
			inferenceExpandNodeMenuItem.addActionListener(listener);
		}
		return inferenceExpandNodeMenuItem;
	}
	
	/**
	 * This method initialises inferenceContractNodeMenuItem.
	 * 
	 * @return a new item 'Inference - ContractNode'.
	 */
	private JMenuItem getInferenceContractNodeMenuItem() {
		if (inferenceContractNodeMenuItem == null) {
			inferenceContractNodeMenuItem = new JMenuItem();
			inferenceContractNodeMenuItem.setName(INFERENCE_CONTRACT_NODE_MENUITEM);
			inferenceContractNodeMenuItem.setText(stringResource
				.getString(INFERENCE_CONTRACT_NODE_MENUITEM + LABEL_SUFFIX));
			inferenceContractNodeMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_CONTRACT_NODE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceContractNodeMenuItem
				.setActionCommand(ActionCommands.NODE_CONTRACTION);
			inferenceContractNodeMenuItem.addActionListener(listener);
		}
		return inferenceContractNodeMenuItem;
	}

	/**
	 * This method initialises inferenceRemoveAllFindingsMenuItem.
	 * 
	 * @return a new item 'Inference - RemoveAllFindings'.
	 */
	private JMenuItem getInferenceRemoveAllFindingsMenuItem() {
		if (inferenceRemoveAllFindingsMenuItem == null) {
			inferenceRemoveAllFindingsMenuItem = new JMenuItem();
			inferenceRemoveAllFindingsMenuItem.setName(INFERENCE_REMOVE_ALL_FINDINGS_MENUITEM);
			inferenceRemoveAllFindingsMenuItem.setText(stringResource
				.getString(INFERENCE_REMOVE_ALL_FINDINGS_MENUITEM + LABEL_SUFFIX));
			inferenceRemoveAllFindingsMenuItem.setMnemonic(stringResource.getString(
					INFERENCE_REMOVE_ALL_FINDINGS_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			inferenceRemoveAllFindingsMenuItem
				.setActionCommand(ActionCommands.NODE_REMOVE_ALL_FINDINGS);
			inferenceRemoveAllFindingsMenuItem.addActionListener(listener);
		}
		return inferenceRemoveAllFindingsMenuItem;
	}
	
	/**
	 * This method initialises viewMenu.
	 * 
	 * @return a new menu 'View'.
	 */
	private JMenu getViewMenu() {

		if (viewMenu == null) {
			viewMenu = new JMenu();
			viewMenu.setName(VIEW_MENU);
			viewMenu
				.setText(stringResource.getString(VIEW_MENU + LABEL_SUFFIX));
			viewMenu.setMnemonic(stringResource.getString(
				VIEW_MENU + MNEMONIC_SUFFIX).charAt(0));
			// viewMenu.add(getViewNodesMenu()); 29/03/2009 - jlgozalo- Not
			// required in OpenMarkov
			viewMenu.add(getViewZoomMenu());
			viewMenu.addSeparator();
			viewMenu.add(getViewMessageWindowMenuItem());
		}

		return viewMenu;

	}

	/**
	 * This method initialises viewNodesMenu.
	 * 
	 * @return a new menu 'View - Nodes'.
	 */
	private JMenu getViewNodesMenu() {

		if (viewNodesMenu == null) {
			viewNodesMenu = new JMenu();
			viewNodesMenu.setName(VIEW_NODES_MENU);
			viewNodesMenu.setText(stringResource.getString(VIEW_NODES_MENU
				+ LABEL_SUFFIX));
			viewNodesMenu.setMnemonic(stringResource.getString(
				VIEW_NODES_MENU + MNEMONIC_SUFFIX).charAt(0));
			viewNodesMenu.add(getViewNodesByNameMenuItem());
			viewNodesMenu.add(getViewNodesByTitleMenuItem());
		}

		return viewNodesMenu;

	}

	/**
	 * This method initialises viewNodesByNameMenuItem.
	 * 
	 * @return a new item 'View - Nodes - ByName'.
	 */
	private JCheckBoxMenuItem getViewNodesByNameMenuItem() {

		if (viewNodesByNameMenuItem == null) {
			viewNodesByNameMenuItem = new JCheckBoxMenuItem();
			viewNodesByNameMenuItem.setName(VIEW_NODES_BYNAME_MENUITEM);
			viewNodesByNameMenuItem.setText(stringResource
				.getString(VIEW_NODES_BYNAME_MENUITEM + LABEL_SUFFIX));
			viewNodesByNameMenuItem.setMnemonic(stringResource.getString(
				VIEW_NODES_BYNAME_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			viewNodesByNameMenuItem
				.setActionCommand(ActionCommands.BYNAME_NODES);
			viewNodesByNameMenuItem.addActionListener(listener);
			groupByNameByTitle.add(viewNodesByNameMenuItem);
		}

		return viewNodesByNameMenuItem;

	}

	/**
	 * This method initialises viewNodesByTitleMenuItem.
	 * 
	 * @return a new item 'View - Nodes - ByTitle'.
	 */
	private JCheckBoxMenuItem getViewNodesByTitleMenuItem() {

		if (viewNodesByTitleMenuItem == null) {
			viewNodesByTitleMenuItem = new JCheckBoxMenuItem();
			viewNodesByTitleMenuItem.setName(VIEW_NODES_BYTITLE_MENUITEM);
			viewNodesByTitleMenuItem.setText(stringResource
				.getString(VIEW_NODES_BYTITLE_MENUITEM + LABEL_SUFFIX));
			viewNodesByTitleMenuItem.setMnemonic(stringResource.getString(
				VIEW_NODES_BYTITLE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			viewNodesByTitleMenuItem
				.setActionCommand(ActionCommands.BYTITLE_NODES);
			viewNodesByTitleMenuItem.addActionListener(listener);
			groupByNameByTitle.add(viewNodesByTitleMenuItem);
		}

		return viewNodesByTitleMenuItem;

	}

	/**
	 * This method initialises viewZoomMenu.
	 * 
	 * @return a new menu 'View - Zoom'.
	 */
	private JMenu getViewZoomMenu() {

		if (viewZoomMenu == null) {
			viewZoomMenu = new JMenu();
			viewZoomMenu.setName(VIEW_ZOOM_MENU);
			viewZoomMenu.setText(stringResource.getString(VIEW_ZOOM_MENU
				+ LABEL_SUFFIX));
			viewZoomMenu.setMnemonic(stringResource.getString(
				VIEW_ZOOM_MENU + MNEMONIC_SUFFIX).charAt(0));
			viewZoomMenu.add(getViewZoomInMenuItem());
			viewZoomMenu.add(getViewZoomOutMenuItem());
			viewZoomMenu.addSeparator();
			viewZoomMenu.add(getViewZoom500MenuItem());
			viewZoomMenu.add(getViewZoom200MenuItem());
			viewZoomMenu.add(getViewZoom150MenuItem());
			viewZoomMenu.add(getViewZoom100MenuItem());
			viewZoomMenu.add(getViewZoom75MenuItem());
			viewZoomMenu.add(getViewZoom50MenuItem());
			viewZoomMenu.add(getViewZoom25MenuItem());
			viewZoomMenu.add(getViewZoom10MenuItem());
			viewZoomMenu.addSeparator();
			viewZoomMenu.add(getViewZoomOtherMenuItem());
		}

		return viewZoomMenu;

	}

	/**
	 * This method initialises viewZoomInMenuItem.
	 * 
	 * @return a new item 'View - Zoom - Zoom in.
	 */
	private JMenuItem getViewZoomInMenuItem() {

		if (viewZoomInMenuItem == null) {
			viewZoomInMenuItem = new JMenuItem();
			viewZoomInMenuItem.setName(VIEW_ZOOM_IN_MENUITEM);
			viewZoomInMenuItem.setText(stringResource
				.getString(VIEW_ZOOM_IN_MENUITEM + LABEL_SUFFIX));
			viewZoomInMenuItem.setMnemonic(stringResource.getString(
				VIEW_ZOOM_IN_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			viewZoomInMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_ZOOM_IN_ENABLED));
			viewZoomInMenuItem.setActionCommand(ActionCommands.ZOOM_IN);
			viewZoomInMenuItem.addActionListener(listener);
		}

		return viewZoomInMenuItem;

	}

	/**
	 * This method initialises viewZoomOutMenuItem.
	 * 
	 * @return a new item 'View - Zoom - Zoom out.
	 */
	private JMenuItem getViewZoomOutMenuItem() {

		if (viewZoomOutMenuItem == null) {
			viewZoomOutMenuItem = new JMenuItem();
			viewZoomOutMenuItem.setName(VIEW_ZOOM_OUT_MENUITEM);
			viewZoomOutMenuItem.setText(stringResource
				.getString(VIEW_ZOOM_OUT_MENUITEM + LABEL_SUFFIX));
			viewZoomOutMenuItem.setMnemonic(stringResource.getString(
				VIEW_ZOOM_OUT_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			viewZoomOutMenuItem.setIcon(iconLoader
				.load(IconLoader.ICON_ZOOM_OUT_ENABLED));
			viewZoomOutMenuItem.setActionCommand(ActionCommands.ZOOM_OUT);
			viewZoomOutMenuItem.addActionListener(listener);
		}

		return viewZoomOutMenuItem;

	}

	/**
	 * This method initialises viewZoom500MenuItem
	 * 
	 * @return a new item 'View - Zoom - 500%'.
	 */
	private JCheckBoxMenuItem getViewZoom500MenuItem() {

		if (viewZoom500MenuItem == null) {
			viewZoom500MenuItem = new JCheckBoxMenuItem();
			viewZoom500MenuItem.setName(VIEW_ZOOM_500_MENUITEM);
			viewZoom500MenuItem.setText(stringResource
				.getString(VIEW_ZOOM_500_MENUITEM + LABEL_SUFFIX));
			viewZoom500MenuItem.setActionCommand(ActionCommands
				.getZoomActionCommandValue(5));
			viewZoom500MenuItem.addActionListener(listener);
			groupZoom.add(viewZoom500MenuItem);
		}

		return viewZoom500MenuItem;

	}

	/**
	 * This method initialises viewZoom200MenuItem
	 * 
	 * @return a new item 'View - Zoom - 200%'.
	 */
	private JCheckBoxMenuItem getViewZoom200MenuItem() {

		if (viewZoom200MenuItem == null) {
			viewZoom200MenuItem = new JCheckBoxMenuItem();
			viewZoom200MenuItem.setName(VIEW_ZOOM_200_MENUITEM);
			viewZoom200MenuItem.setText(stringResource
				.getString(VIEW_ZOOM_200_MENUITEM + LABEL_SUFFIX));
			viewZoom200MenuItem.setActionCommand(ActionCommands
				.getZoomActionCommandValue(2));
			viewZoom200MenuItem.addActionListener(listener);
			groupZoom.add(viewZoom200MenuItem);
		}

		return viewZoom200MenuItem;

	}

	/**
	 * This method initialises viewZoom150MenuItem.
	 * 
	 * @return a new item 'View - Zoom - 150%'.
	 */
	private JCheckBoxMenuItem getViewZoom150MenuItem() {

		if (viewZoom150MenuItem == null) {
			viewZoom150MenuItem = new JCheckBoxMenuItem();
			viewZoom150MenuItem.setName(VIEW_ZOOM_150_MENUITEM);
			viewZoom150MenuItem.setText(stringResource
				.getString(VIEW_ZOOM_150_MENUITEM + LABEL_SUFFIX));
			viewZoom150MenuItem.setActionCommand(ActionCommands
				.getZoomActionCommandValue(1.5));
			viewZoom150MenuItem.addActionListener(listener);
			groupZoom.add(viewZoom150MenuItem);
		}

		return viewZoom150MenuItem;

	}

	/**
	 * This method initialises viewZoom100MenuItem.
	 * 
	 * @return a new item 'View - Zoom - 100%'.
	 */
	private JCheckBoxMenuItem getViewZoom100MenuItem() {

		if (viewZoom100MenuItem == null) {
			viewZoom100MenuItem = new JCheckBoxMenuItem();
			viewZoom100MenuItem.setName(VIEW_ZOOM_100_MENUITEM);
			viewZoom100MenuItem.setText(stringResource
				.getString(VIEW_ZOOM_100_MENUITEM + LABEL_SUFFIX));
			viewZoom100MenuItem.setActionCommand(ActionCommands
				.getZoomActionCommandValue(1));
			viewZoom100MenuItem.addActionListener(listener);
			groupZoom.add(viewZoom100MenuItem);
			viewZoom100MenuItem.setSelected(true);
		}

		return viewZoom100MenuItem;

	}

	/**
	 * This method initialises viewZoom75MenuItem
	 * 
	 * @return a new item 'View - Zoom - 75%'.
	 */
	private JCheckBoxMenuItem getViewZoom75MenuItem() {

		if (viewZoom75MenuItem == null) {
			viewZoom75MenuItem = new JCheckBoxMenuItem();
			viewZoom75MenuItem.setName(VIEW_ZOOM_75_MENUITEM);
			viewZoom75MenuItem.setText(stringResource
				.getString(VIEW_ZOOM_75_MENUITEM + LABEL_SUFFIX));
			viewZoom75MenuItem.setActionCommand(ActionCommands
				.getZoomActionCommandValue(0.75));
			viewZoom75MenuItem.addActionListener(listener);
			groupZoom.add(viewZoom75MenuItem);
		}

		return viewZoom75MenuItem;

	}

	/**
	 * This method initialises viewZoom50MenuItem
	 * 
	 * @return a new item 'View - Zoom - 50%'.
	 */
	private JCheckBoxMenuItem getViewZoom50MenuItem() {

		if (viewZoom50MenuItem == null) {
			viewZoom50MenuItem = new JCheckBoxMenuItem();
			viewZoom50MenuItem.setName(VIEW_ZOOM_50_MENUITEM);
			viewZoom50MenuItem.setText(stringResource
				.getString(VIEW_ZOOM_50_MENUITEM + LABEL_SUFFIX));
			viewZoom50MenuItem.setActionCommand(ActionCommands
				.getZoomActionCommandValue(0.5));
			viewZoom50MenuItem.addActionListener(listener);
			groupZoom.add(viewZoom50MenuItem);
		}

		return viewZoom50MenuItem;

	}

	/**
	 * This method initialises viewZoom25MenuItem.
	 * 
	 * @return a new item 'View - Zoom - 25%'.
	 */
	private JCheckBoxMenuItem getViewZoom25MenuItem() {

		if (viewZoom25MenuItem == null) {
			viewZoom25MenuItem = new JCheckBoxMenuItem();
			viewZoom25MenuItem.setName(VIEW_ZOOM_25_MENUITEM);
			viewZoom25MenuItem.setText(stringResource
				.getString(VIEW_ZOOM_25_MENUITEM + LABEL_SUFFIX));
			viewZoom25MenuItem.setActionCommand(ActionCommands
				.getZoomActionCommandValue(0.25));
			viewZoom25MenuItem.addActionListener(listener);
			groupZoom.add(viewZoom25MenuItem);
		}

		return viewZoom25MenuItem;

	}

	/**
	 * This method initialises viewZoom10MenuItem.
	 * 
	 * @return a new item 'View - Zoom - 10%'.
	 */
	private JCheckBoxMenuItem getViewZoom10MenuItem() {

		if (viewZoom10MenuItem == null) {
			viewZoom10MenuItem = new JCheckBoxMenuItem();
			viewZoom10MenuItem.setName(VIEW_ZOOM_10_MENUITEM);
			viewZoom10MenuItem.setText(stringResource
				.getString(VIEW_ZOOM_10_MENUITEM + LABEL_SUFFIX));
			viewZoom10MenuItem.setActionCommand(ActionCommands
				.getZoomActionCommandValue(0.1));
			viewZoom10MenuItem.addActionListener(listener);
			groupZoom.add(viewZoom10MenuItem);
		}

		return viewZoom10MenuItem;

	}

	/**
	 * This method initialises viewZoomOtherMenuItem.
	 * 
	 * @return a new item 'View - Zoom - Other'.
	 */
	private JCheckBoxMenuItem getViewZoomOtherMenuItem() {

		if (viewZoomOtherMenuItem == null) {
			viewZoomOtherMenuItem = new JCheckBoxMenuItem();
			viewZoomOtherMenuItem.setName(VIEW_ZOOM_OTHER_MENUITEM);
			viewZoomOtherMenuItem.setText(stringResource
				.getString(VIEW_ZOOM_OTHER_MENUITEM + LABEL_SUFFIX));
			viewZoomOtherMenuItem.setMnemonic(stringResource.getString(
				VIEW_ZOOM_OTHER_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			viewZoomOtherMenuItem.setActionCommand(ActionCommands.ZOOM_OTHER);
			viewZoomOtherMenuItem.addActionListener(listener);
			groupZoom.add(viewZoomOtherMenuItem);
		}

		return viewZoomOtherMenuItem;

	}

	/**
	 * This method initialises viewMessageWindowMenuItem.
	 * 
	 * @return a new item 'View - Message item'.
	 */
	private JMenuItem getViewMessageWindowMenuItem() {

		if (viewMessageWindowMenuItem == null) {
			viewMessageWindowMenuItem = new JMenuItem();
			viewMessageWindowMenuItem.setName(VIEW_MESSAGEWINDOW_MENUITEM);
			viewMessageWindowMenuItem.setText(stringResource
				.getString(VIEW_MESSAGEWINDOW_MENUITEM + LABEL_SUFFIX));
			viewMessageWindowMenuItem.setMnemonic(stringResource.getString(
				VIEW_MESSAGEWINDOW_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			viewMessageWindowMenuItem
				.setActionCommand(ActionCommands.MESSAGE_WINDOW);
			viewMessageWindowMenuItem.addActionListener(listener);
		}

		return viewMessageWindowMenuItem;

	}

	/**
	 * This method initialises toolsMenu.
	 * 
	 * @return a new File menu.
	 */
	private JMenu getToolsMenu() {

		if (toolsMenu == null) {
			toolsMenu = new JMenu();
			toolsMenu.setName(TOOLS_MENU);
			toolsMenu
				.setText(stringResource.getString(TOOLS_MENU + LABEL_SUFFIX));
			toolsMenu.setMnemonic(stringResource.getString(
				TOOLS_MENU + MNEMONIC_SUFFIX).charAt(0));
			toolsMenu.add(getToolsLearningMenuItem());
			toolsMenu.add(getToolsCostEffectivenessMenuItem());
			toolsMenu.addSeparator();
			toolsMenu.add(getToolsConfigurationMenuItem());
		}

		return toolsMenu;

	}

	private JMenuItem getToolsSensitivityAnalysis() {
		if (toolsSensitivityAnalysisMenuItem == null) {
			toolsSensitivityAnalysisMenuItem = new JMenuItem();
			toolsSensitivityAnalysisMenuItem.setName(SENSITIVITYANALYSIS_MENUITEM);
			toolsSensitivityAnalysisMenuItem.setText(stringResource
				.getString(SENSITIVITYANALYSIS_MENUITEM + LABEL_SUFFIX));
			toolsSensitivityAnalysisMenuItem.setMnemonic(stringResource.getString(
					SENSITIVITYANALYSIS_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			toolsSensitivityAnalysisMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
			toolsSensitivityAnalysisMenuItem.setActionCommand(ActionCommands.SENSITIVITY_ANALYSIS);
			toolsSensitivityAnalysisMenuItem.addActionListener(listener);
		}

		return toolsSensitivityAnalysisMenuItem;
	}

	private JMenuItem getToolsCostEffectivenessDeterministicMenuItem() {
		if (toolsCostEffectivenessDeterministicMenuItem == null) {
			toolsCostEffectivenessDeterministicMenuItem = new JMenuItem();
			toolsCostEffectivenessDeterministicMenuItem.setName(COSTEFFECTIVENESSDETERMINISTIC_MENUITEM);
			toolsCostEffectivenessDeterministicMenuItem.setText(stringResource
				.getString(COSTEFFECTIVENESSDETERMINISTIC_MENUITEM + LABEL_SUFFIX));
			toolsCostEffectivenessDeterministicMenuItem.setMnemonic(stringResource.getString(
					COSTEFFECTIVENESSDETERMINISTIC_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			toolsCostEffectivenessDeterministicMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
			toolsCostEffectivenessDeterministicMenuItem.setActionCommand(ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC);
			toolsCostEffectivenessDeterministicMenuItem.addActionListener(listener);
		}

		return toolsCostEffectivenessDeterministicMenuItem;
	}
	
	private JMenuItem getToolsCostEffectivenessMenuItem() {
		if (toolsCostEffectivenessMenuItem == null) {
			toolsCostEffectivenessMenuItem = new JMenu();
			toolsCostEffectivenessMenuItem.setName(COSTEFFECTIVENESS_SUBMENU);
			toolsCostEffectivenessMenuItem.setText(stringResource
				.getString(COSTEFFECTIVENESS_SUBMENU + LABEL_SUFFIX));
			toolsCostEffectivenessMenuItem.add(getToolsCostEffectivenessDeterministicMenuItem());
			toolsCostEffectivenessMenuItem.add(getToolsSensitivityAnalysis());

		}

		return toolsCostEffectivenessMenuItem;
	}

	/**
	 * This method initialises toolsLearningMenuItem.
	 * 
	 * @return a new item 'Tools - Learning'.
	 */
	private JMenuItem getToolsLearningMenuItem() {

		if (toolsLearningMenuItem == null) {
			toolsLearningMenuItem = new JMenuItem();
			toolsLearningMenuItem.setName(LEARNING_MENUITEM);
			toolsLearningMenuItem.setText(stringResource
				.getString(LEARNING_MENUITEM + LABEL_SUFFIX));
			toolsLearningMenuItem.setMnemonic(stringResource.getString(
				LEARNING_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			toolsLearningMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
			toolsLearningMenuItem.setActionCommand(ActionCommands.LEARNING);
			toolsLearningMenuItem.addActionListener(listener);
		}

		return toolsLearningMenuItem;

	}

	/**
	 * This method initialises toolsConfigurationMenuItem.
	 * 
	 * @return a new item 'Tools - Configuration'.
	 */
	private JMenuItem getToolsConfigurationMenuItem() {

		if (toolsConfigurationMenuItem == null) {
			toolsConfigurationMenuItem = new JMenuItem();
			toolsConfigurationMenuItem.setName(CONFIGURATION_MENUITEM);
			toolsConfigurationMenuItem.setText(stringResource
				.getString(CONFIGURATION_MENUITEM + LABEL_SUFFIX));
			toolsConfigurationMenuItem.setMnemonic(stringResource.getString(
				CONFIGURATION_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			toolsConfigurationMenuItem
				.setActionCommand(ActionCommands.CONFIGURATION);
			toolsConfigurationMenuItem.addActionListener(listener);
		}

		return toolsConfigurationMenuItem;

	}

	/**
	 * This method initialises optionsMenu.
	 * 
	 * @return a new Options menu.
	 */
	/*private JMenu getOptionsMenu() {  //FOR FUTURE USE
		if (optionsMenu == null) {
			optionsMenu = new JMenu();
			optionsMenu.setName(OPTIONS_MENU);
			optionsMenu.setText(stringResource.getString(OPTIONS_MENU + LABEL_SUFFIX));
			optionsMenu.setMnemonic(stringResource.getString(
				OPTIONS_MENU + MNEMONIC_SUFFIX).charAt(0));
		}
		return optionsMenu;
	} */  //FOR FUTURE USE
	
	/**
	 * This method initialises helpMenu
	 * 
	 * @return a new Help menu.
	 */
	private JMenu getHelpingMenu() {

		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setName(HELP_MENU);
			helpMenu
				.setText(stringResource.getString(HELP_MENU + LABEL_SUFFIX));
			helpMenu.setMnemonic(stringResource.getString(
				HELP_MENU + MNEMONIC_SUFFIX).charAt(0));
			helpMenu.add(getHelpOpenHelpItem());
			helpMenu.addSeparator();
			helpMenu.add(getHelpOpenChangeLanguageItem());
			helpMenu.addSeparator();
			helpMenu.add(getHelpOpenAboutItem());
		}

		return helpMenu;

	}

	/**
	 * This methods initialises openHelpMenuItem
	 * 
	 * @return a new item 'Help - Help'
	 */
	private JMenuItem getHelpOpenHelpItem() {

		if (helpOpenHelpMenuItem == null) {
			helpOpenHelpMenuItem = new JMenuItem();
			helpOpenHelpMenuItem.setName(HELP_HELP_MENUITEM);
			helpOpenHelpMenuItem.setText(stringResource
				.getString(HELP_HELP_MENUITEM + LABEL_SUFFIX));
			helpOpenHelpMenuItem.setMnemonic(stringResource.getString(
				HELP_HELP_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			helpOpenHelpMenuItem.setActionCommand(ActionCommands.HELP_HELP);
			HelpViewer helpVw = HelpViewer.getUniqueInstance();
			ActionListener helper =
				new CSH.DisplayHelpFromSource(helpVw.getHb());
			helpOpenHelpMenuItem.addActionListener(helper);
			
			helpOpenHelpMenuItem.addActionListener(listener);
			
		}

		return helpOpenHelpMenuItem;

	}

	/**
	 * This methods initialises openChangeLanguageMenuItem
	 * 
	 * @return a new item 'Help - ChangeLanguage'
	 */
	private JMenuItem getHelpOpenChangeLanguageItem() {

		if (helpOpenChangeLanguageMenuItem == null) {
			helpOpenChangeLanguageMenuItem = new JMenuItem();
			helpOpenChangeLanguageMenuItem
				.setName(HELP_CHANGELANGUAGE_MENUITEM);
			helpOpenChangeLanguageMenuItem.setText(stringResource
				.getString(HELP_CHANGELANGUAGE_MENUITEM + LABEL_SUFFIX));
			helpOpenChangeLanguageMenuItem.setMnemonic(stringResource
				.getString(HELP_CHANGELANGUAGE_MENUITEM + MNEMONIC_SUFFIX)
				.charAt(0));
			helpOpenChangeLanguageMenuItem
				.setActionCommand(ActionCommands.HELP_CHANGE_LANGUAGE);
			helpOpenChangeLanguageMenuItem.addActionListener(listener);
		}

		return helpOpenChangeLanguageMenuItem;

	}

	/**
	 * This methods initialises openAboutMenuItem
	 * 
	 * @return a new item 'Help - About'
	 */
	private JMenuItem getHelpOpenAboutItem() {

		if (helpOpenAboutMenuItem == null) {
			helpOpenAboutMenuItem = new JMenuItem();
			helpOpenAboutMenuItem.setName(HELP_ABOUT_MENUITEM);
			helpOpenAboutMenuItem.setText(stringResource
				.getString(HELP_ABOUT_MENUITEM + LABEL_SUFFIX));
			helpOpenAboutMenuItem.setMnemonic(stringResource.getString(
				HELP_ABOUT_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			helpOpenAboutMenuItem.setActionCommand(ActionCommands.HELP_ABOUT);
			helpOpenAboutMenuItem.addActionListener(listener);
		}

		return helpOpenAboutMenuItem;

	}

	/**
	 * This method initialises menuMDI.
	 * 
	 * @return a new menu dependent of the MDI.
	 */
	public JMenu getMenuMDI() {

		if (menuMDI == null) {
			menuMDI = new JMenu();
			menuMDI.setName("MDIMenu");
		}

		return menuMDI;

	}

	/**
	 * Checks the checkbox menu item corresponding to the zoom value. If the
	 * value isn't any of the prefixed zoom values, then checks the last menu
	 * item and modifies its string so that the zoom value appears in the text
	 * of the menu item.
	 * 
	 * @param value
	 *            zoom value.
	 */
	public void setZoom(double value) {

		if (value == 5) {
			viewZoom500MenuItem.setSelected(true);
		} else if (value == 2) {
			viewZoom200MenuItem.setSelected(true);
		} else if (value == 1.5) {
			viewZoom150MenuItem.setSelected(true);
		} else if (value == 1) {
			viewZoom100MenuItem.setSelected(true);
		} else if (value == 0.75) {
			viewZoom75MenuItem.setSelected(true);
		} else if (value == 0.5) {
			viewZoom50MenuItem.setSelected(true);
		} else if (value == 0.25) {
			viewZoom25MenuItem.setSelected(true);
		} else if (value == 0.1) {
			viewZoom10MenuItem.setSelected(true);
		} else {
			viewZoomOtherMenuItem.setSelected(true);
		}
		viewZoomOtherMenuItem.setText(stringResource
			.getString(VIEW_ZOOM_OTHER_MENUITEM + LABEL_SUFFIX)
			+ " (" + (int) Math.round(value * 100) + "%)...");

	}

	/**
	 * Returns the component that correspond to an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the component.
	 * @return a components identified by the action command.
	 */
	private JComponent getJComponentActionCommand(String actionCommand) {

		JComponent component = null;

		if (actionCommand.equals(ActionCommands.NEW_NETWORK)) {
			component = fileNewMenuItem;
		} else if (actionCommand.equals(ActionCommands.OPEN_NETWORK)) {
			component = fileOpenMenuItem;
		} else if (actionCommand.equals(ActionCommands.SAVE_NETWORK)) {
			component = fileSaveMenuItem;
		} else if (actionCommand.equals(ActionCommands.SAVEAS_NETWORK)) {
			component = fileSaveAsMenuItem;
		} else if (actionCommand.equals(ActionCommands.SAVE_OPEN_NETWORK)) {
			component = fileSaveOpenMenuItem;
		} else if (actionCommand.equals(ActionCommands.CLOSE_NETWORK)) {
			component = fileCloseMenuItem;
		} else if (actionCommand.equals(ActionCommands.NETWORK_PROPERTIES)) {
			component = fileNetworkPropertiesMenuItem;
		} else if (actionCommand.equals(ActionCommands.EXIT_APPLICATION)) {
			component = fileExitMenuItem;
		} else if (actionCommand.equals(ActionCommands.CLIPBOARD_CUT)) {
			component = editCutMenuItem;
		} else if (actionCommand.equals(ActionCommands.CLIPBOARD_COPY)) {
			component = editCopyMenuItem;
		} else if (actionCommand.equals(ActionCommands.CLIPBOARD_PASTE)) {
			component = editPasteMenuItem;
		} else if (actionCommand.equals(ActionCommands.OBJECT_REMOVAL)) {
			component = editRemoveMenuItem;
		} else if (actionCommand.equals(ActionCommands.UNDO)) {
			component = editUndoMenuItem;
		} else if (actionCommand.equals(ActionCommands.REDO)) {
			component = editRedoMenuItem;
		} else if (actionCommand.equals(ActionCommands.SELECT_ALL)) {
			component = editSelectAllMenuItem;
		} else if (actionCommand.equals(ActionCommands.OBJECT_SELECTION)) {
			component = editObjectSelectionMenuItem;
		} else if (actionCommand.equals(ActionCommands.CHANCE_CREATION)) {
			component = editChanceCreationMenuItem;
		} else if (actionCommand.equals(ActionCommands.DECISION_CREATION)) {
			component = editDecisionCreationMenuItem;
		} else if (actionCommand.equals(ActionCommands.UTILITY_CREATION)) {
			component = editUtilityCreationMenuItem;
		} else if (actionCommand.equals(ActionCommands.LINK_CREATION)) {
			component = editLinkCreationMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_PROPERTIES)) {
			component = editNodePropertiesMenuItem;
		} else if (actionCommand.equals(ActionCommands.CHANGE_POTENTIAL)) {
			component = editRelationMenuItem;
		} else if (actionCommand.equals(ActionCommands.LINK_PROPERTIES)) {
			component = editLinkPropertiesMenuItem;
		} else if (actionCommand.equals(ActionCommands.CHANGE_TO_INFERENCE_MODE)) {
			component = editSwitchToInferenceModeMenuItem;
		} else if (actionCommand.equals(ActionCommands.CHANGE_TO_EDITION_MODE)) {
			component = inferenceSwitchToEditionModeMenuItem;
		} else if (actionCommand.equals(ActionCommands.INFERENCE_OPTIONS)) {
			component = inferenceOptionsMenuItem;
		} else if (actionCommand.equals(ActionCommands.CREATE_NEW_EVIDENCE_CASE)) {
			component = inferenceCreateNewEvidenceCaseMenuItem;
		} else if (actionCommand.equals(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE)) {
			component = inferenceGoToFirstEvidenceCaseMenuItem;
		} else if (actionCommand.equals(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE)) {
			component = inferenceGoToPreviousEvidenceCaseMenuItem;
		} else if (actionCommand.equals(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE)) {
			component = inferenceGoToNextEvidenceCaseMenuItem;
		} else if (actionCommand.equals(ActionCommands.GO_TO_LAST_EVIDENCE_CASE)) {
			component = inferenceGoToLastEvidenceCaseMenuItem;
		} else if (actionCommand.equals(ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES)) {
			component = inferenceClearOutAllEvidenceCasesMenuItem;
		} else if (actionCommand.equals(ActionCommands.PROPAGATE_EVIDENCE)) {
			component = inferencePropagateEvidenceMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_EXPANSION)) {
			component = inferenceExpandNodeMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_CONTRACTION)) {
			component = inferenceContractNodeMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_REMOVE_ALL_FINDINGS)) {
			component = inferenceRemoveAllFindingsMenuItem;
		} else if (actionCommand.equals(ActionCommands.BYTITLE_NODES)) {
			component = viewNodesByTitleMenuItem;
		} else if (actionCommand.equals(ActionCommands.BYNAME_NODES)) {
			component = viewNodesByNameMenuItem;
		} else if (actionCommand.equals(ActionCommands.ZOOM_IN)) {
			component = viewZoomInMenuItem;
		} else if (actionCommand.equals(ActionCommands.ZOOM_OUT)) {
			component = viewZoomOutMenuItem;
		} else if (actionCommand.equals(ActionCommands.ZOOM_OTHER)) {
			component = viewZoomOtherMenuItem;
		} else if (actionCommand.equals(ActionCommands.ZOOM)) {
			component = viewZoomMenu;
		} else if (actionCommand.equals(ActionCommands.NODES)) {
			component = viewNodesMenu;
		} else if (actionCommand.equals(ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC)) {
			component = toolsCostEffectivenessDeterministicMenuItem;
		} else if (actionCommand.equals(ActionCommands.SENSITIVITY_ANALYSIS)) {
			component = toolsCostEffectivenessDeterministicMenuItem;
		} 
		
		

		return component;

	}

	/**
	 * Enables or disabled an option identified by an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to enable the option, false to disable.
	 */
	public void setOptionEnabled(String actionCommand, boolean b) {

		MenuToolBarBasicImpl.setOptionEnabled(
			getJComponentActionCommand(actionCommand), b);

	}

	/**
	 * Selects or unselects an option identified by an action command. Only
	 * selects or unselects the components that are AbstractButton.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to select the option, false to unselect.
	 */
	public void setOptionSelected(String actionCommand, boolean b) {

		MenuToolBarBasicImpl.setOptionSelected(
			getJComponentActionCommand(actionCommand), b);

	}

	/**
	 * Adds a text to the label of an option identified by an action command.
	 * Only adds a text to the components that are AbstractButton.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param text
	 *            text to add to the label of the options. If null, nothing is
	 *            added.
	 */
	public void addOptionText(String actionCommand, String text) {

		JComponent component = getJComponentActionCommand(actionCommand);
		MenuToolBarBasicImpl.addOptionText(component, defaultText
			.get(component), text);

	}
	
	/**
	 *Changes the text of menu item
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param text
	 *            text to set to the label.
	 */
	public void setText(String actionCommand, String text) {

		JComponent component = getJComponentActionCommand(actionCommand);
		MenuToolBarBasicImpl.setText(component, text);

	}
}
