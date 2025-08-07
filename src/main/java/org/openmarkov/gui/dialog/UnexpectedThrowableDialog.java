package org.openmarkov.gui.dialog;

import org.openmarkov.gui.dialog.common.BottomPanelButtonDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;


public final class UnexpectedThrowableDialog extends BottomPanelButtonDialog {
    
    private static final float TITLE_FONT_SIZE = 14.0f;
    
    public UnexpectedThrowableDialog(Throwable e) {
        super(null);
        this.setTitle("Unexpected error");
        
        this.getComponentsPanel().setLayout(new BoxLayout(this.getComponentsPanel(), BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("An unexpected error has occurred.");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(titleLabel.getFont().deriveFont(UnexpectedThrowableDialog.TITLE_FONT_SIZE));
        this.getComponentsPanel().add(titleLabel);
        JLabel subTitleLabel = new JLabel("Please, send this to the developers of OpenMarkov.");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(subTitleLabel.getFont().deriveFont(UnexpectedThrowableDialog.TITLE_FONT_SIZE));
        this.getComponentsPanel().add(subTitleLabel);
        
        JTextArea areaWithThrowableDescription = new JTextArea(UnexpectedThrowableDialog.stringifyThrowable(e));
        areaWithThrowableDescription.setEditable(false);
        this.getComponentsPanel().add(new JScrollPane(areaWithThrowableDescription));
        
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var closeButton = new JButton("Close");
        closeButton.addActionListener(e1 -> this.dispose());
        this.getButtonsPanel().add(closeButton);
        var copyButton = new JButton("Copy");
        copyButton.addActionListener(e1 -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(UnexpectedThrowableDialog.stringifyThrowable(e)), null));
        this.getButtonsPanel().add(copyButton);
        this.pack();
        
        if(this.getHeight() > 500){
            this.setSize(new Dimension(this.getWidth(), 500));
        }
        if(this.getWidth() > 800){
            this.setSize(new Dimension(800, this.getHeight()));
        }
        
        //This makes the dialog to be centered on the screen, instead of opening from the top-left corner
        this.setLocationRelativeTo(null);
    }
    
    private static String stringifyThrowable(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString());
        for(var staceElement : e.getStackTrace()) {
            sb.append("\n\tat ").append(staceElement);
        }
        return sb.toString();
    }
    
}
