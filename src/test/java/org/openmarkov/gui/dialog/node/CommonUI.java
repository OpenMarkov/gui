package org.openmarkov.gui.dialog.node;

import org.assertj.swing.fixture.AbstractWindowFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.runner.RunWith;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(GUITestRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@EnabledIf("thereIsGraphicEnviroment")
public abstract class CommonUI<TWindowFixture extends AbstractWindowFixture<TWindowFixture, ?, ?>> {
    
    protected TWindowFixture window;
    
    protected abstract TWindowFixture setUpWindow();
    
    @Before
    public void beforeTest() {
        window = setUpWindow();
        window.show();
        window.target().toFront();
    }
    
    @After
    public void afterTest() {
        window.cleanUp();
    }
    
    public static boolean thereIsGraphicEnviroment() {
        return !GraphicsEnvironment.isHeadless();
    }
    
}
