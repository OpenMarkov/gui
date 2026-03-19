package org.openmarkov.gui.dialog.node;

import org.assertj.swing.fixture.AbstractWindowFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.DisabledIf;

import java.awt.*;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class CommonUI<TWindowFixture extends AbstractWindowFixture<TWindowFixture, ?, ?>> {
    
    protected TWindowFixture window;
    
    protected abstract TWindowFixture setUpWindow();
    
    @BeforeEach
    public void beforeTest() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        window = setUpWindow();
        window.show();
        window.target().toFront();
    }
    
    @AfterEach
    public void afterTest() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        window.cleanUp();
    }
    
}
