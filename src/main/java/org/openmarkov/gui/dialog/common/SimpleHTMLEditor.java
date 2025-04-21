package org.openmarkov.gui.dialog.common;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * TODO: Document this class
 *
 * @author jrico
 */
public final class SimpleHTMLEditor extends JPanel {
    
    private static final int EDITOR_DIMENSION_MIN_HEIGHT = 300;
    private static final float UI_BUTTONS_FONT_SIZE = 14.0f;
    private static final List<String> FONT_SIZES = IntStream.rangeClosed(0,120).filter(n->n%2==0 && n>0).mapToObj(String::valueOf).toList();
    private static final int DEFAULT_FONT_SIZE_SELECTION = 12;
    
    private final JEditorPane editorPane;
    private final JToolBar toolBar;
    
    public SimpleHTMLEditor(String initialHTMLContent) {
        this.setSize(800, 600);
        
        this.editorPane = new JEditorPane();
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        this.editorPane.setContentType("text/html");
        this.editorPane.setEditorKit(htmlEditorKit);
        this.editorPane.setText(initialHTMLContent);
        this.editorPane.setCaretPosition(0);
        this.editorPane.setEditable(true);
        ClickOnHyperlinkAction clickOnHyperlinkAction = new ClickOnHyperlinkAction(this.editorPane);
        this.editorPane.addMouseListener(clickOnHyperlinkAction);
        this.editorPane.addMouseMotionListener(clickOnHyperlinkAction);
        this.editorPane.addKeyListener(clickOnHyperlinkAction);
        
        this.toolBar = new JToolBar();
        this.toolBar.setRollover(true);
        this.toolBar.setFloatable(false);
        
        var commonComponents = new CommonComponents(this, this.editorPane, htmlEditorKit, this.toolBar);
        
        JButton copyUI = SimpleHTMLEditor.createJButton("\uD83D\uDDD2");
        copyUI.addActionListener(new DefaultEditorKit.CopyAction());
        
        JButton cutUI = SimpleHTMLEditor.createJButton("✂");
        cutUI.addActionListener(new DefaultEditorKit.CutAction());
        JButton pasteContentUI = SimpleHTMLEditor.createJButton("\uD83D\uDCCB");
        pasteContentUI.addActionListener(new DefaultEditorKit.PasteAction());
        
        URL iconResource = Objects.requireNonNull(SimpleHTMLEditor.class.getResource("/htmleditor/hyperlink_add.png"));
        JButton addHyperlinkUI = new JButton(new ImageIcon(iconResource));
        addHyperlinkUI.addActionListener(new SimpleHTMLEditor.AddHyperlinkAction());
        
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
        
        this.toolBar.add(copyUI);
        this.toolBar.add(cutUI);
        this.toolBar.add(pasteContentUI);
        this.toolBar.add(addHyperlinkUI);
        this.toolBar.add(SimpleHTMLEditor.createVerticalSeparator());
        this.toolBar.add(setLeftAlignmentUI);
        this.toolBar.add(setCenterAlignmentUI);
        this.toolBar.add(setRightAlignmentUI);
        this.toolBar.add(setJustifyAlignmentUI);
        this.toolBar.add(SimpleHTMLEditor.createVerticalSeparator());
        this.toolBar.add(makeBoldUI);
        this.toolBar.add(makeItalicUI);
        this.toolBar.add(makeUnderStrikedUI);
        this.toolBar.add(changeForegroundColorUI);
        this.toolBar.add(changeFontSizeUI);
        this.toolBar.add(changeFamilyFontUI);
        
        var componentsRequiringSelection = Arrays.asList(copyUI, cutUI, addHyperlinkUI);
        componentsRequiringSelection.forEach(component -> component.setEnabled(false));
        this.editorPane.addCaretListener(e -> {
            boolean isSelection = e.getDot() != e.getMark();
            componentsRequiringSelection.forEach(component -> component.setEnabled(isSelection));
        });
        
        this.setLayout(new BorderLayout());
        this.add(this.toolBar, BorderLayout.PAGE_START);
        this.add(new JScrollPane(this.editorPane), BorderLayout.CENTER);
    }
    
    private static @NotNull JButton createJButton(String contents){
        JButton button = new JButton(contents);
        button.setFont(button.getFont().deriveFont(UI_BUTTONS_FONT_SIZE));
        return button;
    }
    
    
    private static @NotNull JSeparator createVerticalSeparator() {
        JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
        separator1.setMaximumSize(new Dimension(3, 10));
        return separator1;
    }
    
    
    public String getHTMLContent() {
        return this.editorPane.getText();
    }
    
    public void focusOnEditor() {
        this.editorPane.requestFocus(false);
    }
    
    public Dimension minimumDimensions() {
        return new Dimension(this.toolBar.getWidth(), this.toolBar.getHeight() + SimpleHTMLEditor.EDITOR_DIMENSION_MIN_HEIGHT);
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
        JButton boldButton = SimpleHTMLEditor.createJButton("B");
        StyledEditorKit.BoldAction boldAction = new StyledEditorKit.BoldAction();
        boldButton.setFont(boldButton.getFont().deriveFont(Font.BOLD));
        boldButton.addActionListener(e -> {
            boldAction.actionPerformed(e);
            commonComponents.returnFocusToEditor();
        });
        return boldButton;
    }
    
    private static JButton createMakeItalicUI(CommonComponents commonComponents) {
        JButton italicButton = SimpleHTMLEditor.createJButton("I");
        italicButton.setFont(italicButton.getFont().deriveFont(Font.ITALIC));
        StyledEditorKit.ItalicAction italicAction = new StyledEditorKit.ItalicAction();
        italicButton.addActionListener(e -> {
            italicAction.actionPerformed(e);
            commonComponents.returnFocusToEditor();
        });
        return italicButton;
    }
    
    private static JButton createMakeUnderStrikedUI(CommonComponents commonComponents) {
        JButton understrikeButton = SimpleHTMLEditor.createJButton("U");
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
        var defaultFontSizeIndex = SimpleHTMLEditor.FONT_SIZES.indexOf(String.valueOf(SimpleHTMLEditor.DEFAULT_FONT_SIZE_SELECTION));
        if (defaultFontSizeIndex>=0){
            fontSizeBox.setSelectedIndex(defaultFontSizeIndex);
        }
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
    
    static class AddHyperlinkAction extends StyledEditorKit.StyledTextAction {
        
        public AddHyperlinkAction() {
            super("hyperlink-add");
        }
        
        @Override
        public final void actionPerformed(ActionEvent e) {
            JEditorPane editorPane = getEditor(e);
            if (editorPane==null){
                return;
            }
            int start = editorPane.getSelectionStart();
            int end = editorPane.getSelectionEnd();
            if (start == end) {
                JOptionPane.showMessageDialog(editorPane, "No text selected.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String selectedText = editorPane.getSelectedText();
            String url = JOptionPane.showInputDialog(editorPane, "Enter the URL for the hyperlink:", "Add Hyperlink", JOptionPane.QUESTION_MESSAGE);
            if (url == null || url.trim().isEmpty()) {
                return;
            }
            String hyperlinkHTML = "<a href=\"" + AddHyperlinkAction.escapeHTML(url.trim()) + "\">" + AddHyperlinkAction.escapeHTML(selectedText) + "</a>";
            try {
                HTMLDocument htmlDocument = (HTMLDocument) editorPane.getDocument();
                HTMLEditorKit htmlEditorKit = (HTMLEditorKit) editorPane.getEditorKit();
                htmlDocument.remove(start, end - start);
                htmlEditorKit.insertHTML(htmlDocument, start, hyperlinkHTML, 0, 0, HTML.Tag.A);
            } catch (BadLocationException | IOException ex) {
                ex.printStackTrace();
            }
        }
        
        static final Map<Character, String> ESCAPE_CHARACTERS = Map.of(
                '<', "&lt;",
                '>', "&gt;",
                '&', "&amp;",
                '"', "&quot;"
        );
        
        /**
         * Escapes HTML characters that are used when creating a {@code <a href="*location*"><a/>} element.
         *
         * @return The argument String, but with HTML characters escaped.
         */
        private static String escapeHTML(String text) {
            StringBuilder escapedString = new StringBuilder();
            for (char character : text.toCharArray()) {
                var replacement = Optional.ofNullable(AddHyperlinkAction.ESCAPE_CHARACTERS.get(character));
                escapedString.append(replacement.orElseGet(() -> String.valueOf(character)));
            }
            return escapedString.toString();
        }
    }
    
    static class ClickOnHyperlinkAction implements MouseListener, MouseMotionListener, KeyListener{
        
        private boolean holdsControl = false;
        private Point2D mouseLocation = new Point(0,0);
        private JEditorPane editorPane;
        
        ClickOnHyperlinkAction(JEditorPane editorPane) {
            this.editorPane = editorPane;
        }
        
        @Override public void keyTyped(KeyEvent e) {
        
        }
        @Override public void mousePressed(MouseEvent e) {
        
        }
        @Override public void mouseReleased(MouseEvent e) {
        
        }
        @Override public void mouseEntered(MouseEvent e) {
        
        }
        @Override public void mouseExited(MouseEvent e) {
            this.holdsControl=false;
        }
        @Override public void mouseDragged(MouseEvent e) {
        
        }
        
        
        @Override public final void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_CONTROL){
                this.holdsControl = true;
            }
            this.checkSetCursor();
        }
        
        @Override public final void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_CONTROL){
                this.holdsControl = false;
            }
            this.checkSetCursor();
        }
        
        @Override public void mouseClicked(MouseEvent e) {
            getHREFOfSelectedElement().ifPresent(href->{
                try {
                    Desktop.getDesktop().browse(href);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            
        }
        
        @Override public void mouseMoved(MouseEvent e) {
            this.mouseLocation= new Point(e.getX(), e.getY());
            this.checkSetCursor();
        }
        
        private void checkSetCursor(){
            getHREFOfSelectedElement().ifPresent(ignored->{
                this.editorPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
            });
        }
        
        private Optional<URI> getHREFOfSelectedElement() {
            if (!this.holdsControl){
                return Optional.empty();
            }
            if (!(this.editorPane.getDocument() instanceof StyledDocument)) {
                return Optional.empty();
            }
            int pos = this.editorPane.viewToModel2D(this.mouseLocation);
            StyledDocument doc = (StyledDocument) this.editorPane.getDocument();
            Element element = doc.getCharacterElement(pos);
            AttributeSet attributes = element.getAttributes();
            Object attributeA = attributes.getAttribute(HTML.Tag.A);
            Object attributeHREF = attributes.getAttribute(HTML.Tag.HTML);
            Object attributeWithLink = Optional.ofNullable(attributeA).orElse(attributeHREF);
            if (attributeWithLink==null){
                return Optional.empty();
            }
            String href = attributeWithLink.toString();
            if (href.startsWith("href=")){
                href=href.substring("href=".length());
            }
            try {
                return Optional.of(new URL(href).toURI());
            } catch (URISyntaxException | MalformedURLException e) {
                return Optional.empty();
            }
        }
        
    }
    
}