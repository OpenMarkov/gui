package org.openmarkov.core.gui.oon;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.plugin.Toolbar;
import org.openmarkov.core.gui.menutoolbar.toolbar.ToolBarBasic;

@SuppressWarnings("serial")
@Toolbar(name="ObjectOriented")
public class OOToolBar extends ToolBarBasic implements MouseMotionListener
{
    
    /**
     * Name of the 'instance creation' enabled icon.
     */
    public static final String ICON_INSTANCE_ENABLED = "instance.gif";  
    
    
    /**
     * Button to activate instance creation.
     */
    private JToggleButton instanceCreationButton = null;    
    
    /**
     * Combobox to select class to instantiate.
     */
    private ClassComboBox classComboBox = null;    
    
    /**
     * String resource.
     */
    private StringResource toolBarsStringResource = null;

    /**
     * Icon loader.
     */
    private IconLoader iconLoader = null;

    
    /**
     * Button group to make autoexclusive the edition options.
     */
    private ButtonGroup inferenceButtonGroup = new ButtonGroup();    

    public OOToolBar (ActionListener newListener)
    {
        super (newListener);
        initialize();
    }
    
    /**
     * This method configures the toolbar.
     */
    private void initialize() {

        toolBarsStringResource =
            StringResourceLoader.getUniqueInstance().getBundleToolBars();
        iconLoader = new IconLoader();
        add(getInstanceCreationButton());
        add(getClassComboBox());
        add(Box.createHorizontalGlue());
    }    
    
    /**
     * This method initialises instanceCreationButton.
     * 
     * @return a link creation button.
     */
    private JToggleButton getInstanceCreationButton() {

        if (instanceCreationButton == null) {
            instanceCreationButton = new JToggleButton();
            instanceCreationButton.setIcon(iconLoader
                .load(ICON_INSTANCE_ENABLED));
            instanceCreationButton.setActionCommand(ActionCommands.INSTANCE_CREATION);
            instanceCreationButton.setFocusable(false);
            instanceCreationButton
                .setToolTipText(toolBarsStringResource
                    .getString(ActionCommands.INSTANCE_CREATION
                        + STRING_TOOLTIP_SUFFIX));
            instanceCreationButton.addActionListener(listener);
            instanceCreationButton.addMouseMotionListener(this);
            inferenceButtonGroup.add(instanceCreationButton);
        }
        return instanceCreationButton;
    }   
    
    /**  This method initialises classComboBox.
     * 
     * @return a class combo box.
     */
    public ClassComboBox getClassComboBox() {

        if (classComboBox == null) {
            classComboBox = new ClassComboBox(listener);
        }
        return classComboBox;
    }       

    @Override
    protected JComponent getJComponentActionCommand (String actionCommand)
    {
        JComponent component = null;
        if (actionCommand.equals (ActionCommands.INSTANCE_CREATION))
        {
            component = instanceCreationButton;
        }
        return component;
    }

    @Override
    public void mouseDragged (MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        if (e.getSource().equals(getInstanceCreationButton())) {
            toolBarsStringResource = StringResourceLoader.getUniqueInstance().getBundleToolBars();
            getInstanceCreationButton().setToolTipText(toolBarsStringResource
                    .getString(ActionCommands.INSTANCE_CREATION
                            + STRING_TOOLTIP_SUFFIX));
        }
    
    }
}
