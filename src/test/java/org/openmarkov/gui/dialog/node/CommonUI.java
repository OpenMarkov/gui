package org.openmarkov.gui.dialog.node;

import org.assertj.swing.fixture.AbstractWindowFixture;
import org.junit.After;
import org.junit.Before;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class CommonUI<TWindowFixture extends AbstractWindowFixture<TWindowFixture, ?, ?>> {
    
    protected TWindowFixture window;
    
    protected abstract TWindowFixture setUpWindow();
    
    @Before
    public void beforeTest() {
        window = setUpWindow();
        window.show();
    }
    
    @After
    public void afterTest() {
        window.cleanUp();
    }
    
    
}
