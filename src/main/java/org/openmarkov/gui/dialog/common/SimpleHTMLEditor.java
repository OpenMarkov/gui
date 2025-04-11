package org.openmarkov.gui.dialog.common;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * TODO: Document this class
 *
 * @author jrico
 */
public final class SimpleHTMLEditor extends JPanel {
    
    private static final List<String> FONT_SIZES = Arrays.asList("12", "16", "20", "24", "32");
    
    private final JEditorPane editorPane;
    
    public SimpleHTMLEditor(Frame owner, String initialHTMLContent) {
        this.setSize(800, 600);
        
        this.editorPane = new JEditorPane();
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        this.editorPane.setContentType("text/html");
        this.editorPane.setEditorKit(htmlEditorKit);
        this.editorPane.setText(initialHTMLContent);
        this.editorPane.setEditable(true);
        
        JToolBar toolBar = new JToolBar();
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
        
        var commonComponents = new CommonComponents(this, this.editorPane, htmlEditorKit, toolBar);
        
        JButton copyUI = new JButton("\uD83D\uDDD2");
        copyUI.addActionListener(new DefaultEditorKit.CopyAction());
        JButton cutUI = new JButton("✂");
        cutUI.addActionListener(new DefaultEditorKit.CutAction());
        JButton pasteContentUI = new JButton("\uD83D\uDCCB");
        pasteContentUI.addActionListener(new DefaultEditorKit.PasteAction());
        
        JButton makeBoldUI = SimpleHTMLEditor.createMakeBoldUI(commonComponents);
        JButton makeItalicUI = SimpleHTMLEditor.createMakeItalicUI(commonComponents);
        JButton makeUnderStrikedUI = SimpleHTMLEditor.createMakeUnderStrikedUI(commonComponents);
        JComboBox<String> changeFontSizeUI = SimpleHTMLEditor.createChangeFontSizeUI(commonComponents);
        JComboBox<String> changeFamilyFontUI = SimpleHTMLEditor.createChangeFamilyFontUI(commonComponents);
        JButton changeForegroundColorUI = SimpleHTMLEditor.createChangeForegroundColorUI(commonComponents);
        
        JButton setLeftAlignmentUI = SimpleHTMLEditor.createAlignmentUI(commonComponents, StyleConstants.ALIGN_LEFT, "align-left", "/htmleditor/leftalign.png");
        JButton setCenterAlignmentUI = SimpleHTMLEditor.createAlignmentUI(commonComponents, StyleConstants.ALIGN_CENTER, "align-center", "/htmleditor/centeralign.png");
        JButton setRightAlignmentUI = SimpleHTMLEditor.createAlignmentUI(commonComponents, StyleConstants.ALIGN_RIGHT, "align-right", "/htmleditor/rightalign.png");
        JButton setJustifyAlignmentUI = SimpleHTMLEditor.createAlignmentUI(commonComponents, StyleConstants.ALIGN_JUSTIFIED, "align-justify", "/htmleditor/justifyalign.png");
        
        toolBar.add(copyUI);
        toolBar.add(cutUI);
        toolBar.add(pasteContentUI);
        toolBar.add(SimpleHTMLEditor.createVerticalSeparator());
        toolBar.add(setLeftAlignmentUI);
        toolBar.add(setCenterAlignmentUI);
        toolBar.add(setRightAlignmentUI);
        toolBar.add(setJustifyAlignmentUI);
        toolBar.add(SimpleHTMLEditor.createVerticalSeparator());
        toolBar.add(makeBoldUI);
        toolBar.add(makeItalicUI);
        toolBar.add(makeUnderStrikedUI);
        toolBar.add(changeForegroundColorUI);
        toolBar.add(changeFontSizeUI);
        toolBar.add(changeFamilyFontUI);
        
        var componentsRequiringSelection = Arrays.asList(copyUI, cutUI);
        componentsRequiringSelection.forEach(component -> component.setEnabled(false));
        this.editorPane.addCaretListener(e -> {
            boolean isSelection = e.getDot() != e.getMark();
            componentsRequiringSelection.forEach(component -> component.setEnabled(isSelection));
        });
        
        this.setLayout(new BorderLayout());
        this.add(toolBar, BorderLayout.PAGE_START);
        this.add(new JScrollPane(this.editorPane), BorderLayout.CENTER);
    }
    
    private static @NotNull JSeparator createVerticalSeparator() {
        JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
        separator1.setMaximumSize(new Dimension(3,10));
        return separator1;
    }
    
    
    public String getHTMLContent(){
        return this.editorPane.getText();
    }
    
    
    private record CommonComponents(JPanel frame, JEditorPane editorPane, HTMLEditorKit htmlEditorKit,
                                    JToolBar toolBar) {
        public void returnFocusToEditor() {
            this.editorPane.requestFocus(false);
        }
    }
    
    
    private static JButton createAlignmentUI(CommonComponents commonComponents, int alignment, String actionName, String resourceImage) {
        URL iconResource = Objects.requireNonNull(SimpleHTMLEditor.class.getResource(resourceImage));
        JButton setAlignmentUI = new JButton(new ImageIcon(iconResource));
        StyledEditorKit.AlignmentAction alignmentAction = new StyledEditorKit.AlignmentAction(actionName, alignment);
        setAlignmentUI.addActionListener(e -> {
            alignmentAction.actionPerformed(e);
            commonComponents.returnFocusToEditor();
        });
        return setAlignmentUI;
    }
    
    private static JButton createChangeForegroundColorUI(CommonComponents commonComponents) {
        JColorChooser colorChooser = new JColorChooser();
        var foregroundColorButton = new JButton(new ImageIcon(Objects.requireNonNull(SimpleHTMLEditor.class.getResource("/htmleditor/changecolor.png"))));
        foregroundColorButton.addActionListener(e -> {
            var color = JColorChooser.showDialog(colorChooser, "Choose your foreground color",
                                                 colorChooser.getColor(), false);
            if (color == null) return;
            new StyledEditorKit.ForegroundAction("foreground-color", color).actionPerformed(e);
            commonComponents.returnFocusToEditor();
        });
        return foregroundColorButton;
    }
    
    private static JComboBox<String> createChangeFamilyFontUI(CommonComponents commonComponents) {
        var fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        var fontNames = Arrays.stream(fonts).map(Font::getName).toList().toArray(new String[0]);
        JComboBox<String> fontNameBox = new JComboBox<>(fontNames);
        SimpleHTMLEditor.packComboBox(fontNameBox);
        fontNameBox.addActionListener(e -> {
            String requestedFont = Objects.requireNonNull(fontNameBox.getSelectedItem()).toString();
            new StyledEditorKit.FontFamilyAction("font-change", requestedFont).actionPerformed(e);
            commonComponents.returnFocusToEditor();
        });
        return fontNameBox;
    }
    
    private static JButton createMakeBoldUI(CommonComponents commonComponents) {
        JButton boldButton = new JButton("B");
        StyledEditorKit.BoldAction boldAction = new StyledEditorKit.BoldAction();
        boldButton.setFont(boldButton.getFont().deriveFont(Font.BOLD));
        boldButton.addActionListener(e -> {
            boldAction.actionPerformed(e);
            commonComponents.returnFocusToEditor();
        });
        return boldButton;
    }
    
    private static JButton createMakeItalicUI(CommonComponents commonComponents) {
        JButton italicButton = new JButton("I");
        italicButton.setFont(italicButton.getFont().deriveFont(Font.ITALIC));
        StyledEditorKit.ItalicAction italicAction = new StyledEditorKit.ItalicAction();
        italicButton.addActionListener(e -> {
            italicAction.actionPerformed(e);
            commonComponents.returnFocusToEditor();
        });
        return italicButton;
    }
    
    private static JButton createMakeUnderStrikedUI(CommonComponents commonComponents) {
        JButton understrikeButton = new JButton("U");
        Map<TextAttribute, Object> attrs = (Map<TextAttribute, Object>) understrikeButton.getFont().getAttributes();
        attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        understrikeButton.setFont(understrikeButton.getFont().deriveFont(attrs));
        StyledEditorKit.UnderlineAction underlineAction = new StyledEditorKit.UnderlineAction();
        understrikeButton.addActionListener(e -> {
            underlineAction.actionPerformed(e);
            commonComponents.returnFocusToEditor();
        });
        return understrikeButton;
    }
    
    private static JComboBox<String> createChangeFontSizeUI(CommonComponents commonComponents) {
        var sizes = SimpleHTMLEditor.FONT_SIZES.stream().map(size -> size + " px.")
                                               .toList();
        JComboBox<String> fontSizeBox = new JComboBox<>(sizes.toArray(new String[0]));
        SimpleHTMLEditor.packComboBox(fontSizeBox);
        fontSizeBox.addActionListener(new StyledEditorKit.StyledTextAction("font-resize") {
            @Override public void actionPerformed(ActionEvent e) {
                String fontSizeString = Objects.requireNonNull(fontSizeBox.getSelectedItem()).toString();
                fontSizeString = fontSizeString.substring(0, fontSizeString.indexOf(' '));
                int size = Integer.parseInt(fontSizeString);
                new StyledEditorKit.FontSizeAction("font-resize", size).actionPerformed(e);
                commonComponents.returnFocusToEditor();
            }
        });
        return fontSizeBox;
    }
    
    
    private static <T> Stream<T> getItemToStream(int count, IntFunction<? extends T> give) {
        return IntStream.range(0, count).mapToObj(give);
    }
    
    /**
     * Packs a combo box to the smallest size of its contained elements.
     */
    private static void packComboBox(JComboBox<?> comboBox) {
        FontMetrics fm = comboBox.getFontMetrics(comboBox.getFont());
        int maxWidth = SimpleHTMLEditor.getItemToStream(comboBox.getItemCount(), comboBox::getItemAt)
                                       .map(Object::toString)
                                       .mapToInt(fm::stringWidth)
                                       .max()
                                       .orElse(0);
        comboBox.setMaximumSize(new Dimension(maxWidth + 30, comboBox.getPreferredSize().height));
    }
    
}