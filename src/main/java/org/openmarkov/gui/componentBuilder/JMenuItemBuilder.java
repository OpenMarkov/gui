package org.openmarkov.gui.componentBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmarkov.core.exception.UnreacheableException;
import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.gui.toolplugin.ToolPlugin;
import org.openmarkov.gui.window.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class JMenuItemBuilder {
    
    private @NotNull String title;
    private @Nullable Character mnemonic;
    private @Nullable Boolean enabled;
    private @Nullable ThrowingRunnable<? extends Exception> onClick;
    private @NotNull ArrayList<Component> items;
    private @Nullable Boolean isRadioButton;
    private @Nullable Boolean selected;
    
    public JMenuItemBuilder(@NotNull String title) {
        this.title = title;
        this.items = new ArrayList<>();
    }
    
    public JMenuItemBuilder withTitle(@NotNull String title) {
        this.title = title;
        return this;
    }
    
    public JMenuItemBuilder withMnemonic(Character mnemonic) {
        this.mnemonic = mnemonic;
        return this;
    }
    
    public JMenuItemBuilder enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public JMenuItemBuilder onClick(@NotNull ThrowingRunnable<? extends Exception> onClick) {
        this.onClick = onClick;
        return this;
    }
    
    public JMenuItemBuilder withItem(@NotNull Component item) {
        this.items.add(item);
        return this;
    }
    
    public JMenuItemBuilder withItems(@NotNull Collection<? extends @NotNull Component> items) {
        this.items.addAll(items);
        return this;
    }
    
    public JMenuItemBuilder withItems(@NotNull Stream<? extends @NotNull Component> items) {
        this.items.addAll(items.toList());
        return this;
    }
    
    public JMenuItemBuilder asRadio() {
        this.isRadioButton = true;
        return this;
    }
    
    public JMenuItemBuilder selected(Boolean selected) {
        this.selected = selected;
        return this;
    }
    
    @SuppressWarnings("ExtractMethodRecommender")
    public JMenuItem build() {
        JMenuItem jMenuItem;
        if (Boolean.TRUE.equals(isRadioButton)) {
            jMenuItem = new JRadioButtonMenuItem(this.title);
        } else if (this.items.isEmpty()) {
            jMenuItem = new JMenuItem(this.title);
        } else {
            jMenuItem = new JMenu(this.title);
        }
        if (this.selected != null) {
            jMenuItem.setSelected(this.selected);
        }
        if (this.enabled != null) {
            jMenuItem.setEnabled(this.enabled);
        }
        if (this.mnemonic != null) {
            jMenuItem.setMnemonic(this.mnemonic);
        }
        if (this.onClick != null) {
            jMenuItem.addActionListener(e -> {
                try {
                    onClick.run();
                } catch (UnrecoverableException | UnreacheableException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new UnrecoverableException(ex);
                }
            });
        }
        for (Component component : this.items) {
            jMenuItem.add(component);
        }
        return jMenuItem;
    }
    
    @FunctionalInterface
    public interface ThrowingRunnable<E extends Exception> {
        void run() throws E;
    }
    
    
}
