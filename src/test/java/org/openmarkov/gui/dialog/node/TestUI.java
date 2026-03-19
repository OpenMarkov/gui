package org.openmarkov.gui.dialog.node;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(GUITestRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestUI {
    
    private DialogFixture dialog;
    private ProbNet net;
    
    @Before
    public void setUp() {
        this.net = new ProbNet();
        ProbNet net = this.net;
        net.addNode(new Variable("TestNode"), NodeType.CHANCE);
        Node nodeToTest = net.getNode("TestNode");
        CommonNodePropertiesDialog nodePropertiesDialog = new CommonNodePropertiesDialog(null, nodeToTest, false, false);
        dialog = new DialogFixture(GuiActionRunner.execute(() -> nodePropertiesDialog));
        dialog.show();
    }
    
    @After
    public void after() {
        dialog.cleanUp();
    }
    
    @Test
    public void test1() {
        assertNotNull(net.getNode("TestNode"));
        assertNull(net.getNode("ChangedNodeName"));
        dialog.textBox("jTextFieldNodeName").setText("ChangedNodeName");
        dialog.button("jButtonApply").click();
        assertNull(net.getNode("TestNode"));
        assertNotNull(net.getNode("ChangedNodeName"));
    }
    
    @Test
    public void test2() {
        assertNotNull(net.getNode("TestNode"));
        assertNull(net.getNode("ChangedNodeName"));
        dialog.textBox("jTextFieldNodeName").setText("ChangedNodeName");
        dialog.button("jButtonApply").click();
        assertNull(net.getNode("TestNode"));
        assertNotNull(net.getNode("ChangedNodeName"));
    }
    
    
    
}
