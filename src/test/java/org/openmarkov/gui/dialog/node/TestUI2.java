package org.openmarkov.gui.dialog.node;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestUI2 extends CommonUI<DialogFixture> {
    
    private ProbNet net;
    
    @Override protected DialogFixture setUpWindow() {
        this.net = new ProbNet();
        ProbNet net = this.net;
        net.addNode(new Variable("TestNode"), NodeType.CHANCE);
        Node nodeToTest = net.getNode("TestNode");
        CommonNodePropertiesDialog nodePropertiesDialog = new CommonNodePropertiesDialog(null, nodeToTest, false, false);
        return new DialogFixture(GuiActionRunner.execute(() -> nodePropertiesDialog));
    }
    
    @Test
    public void test1() {
        assertNotNull(this.net.getNode("TestNode"));
        assertNull(this.net.getNode("ChangedNodeName"));
        this.window.textBox("jTextFieldNodeName").setText("ChangedNodeName");
        this.window.button("jButtonApply").click();
        assertNull(this.net.getNode("TestNode"));
        assertNotNull(this.net.getNode("ChangedNodeName"));
    }
    
    @Test
    public void test2() {
        assertNotNull(this.net.getNode("TestNode"));
        assertNull(this.net.getNode("ChangedNodeName"));
        this.window.textBox("jTextFieldNodeName").setText("ChangedNodeName");
        this.window.button("jButtonApply").click();
        assertNull(this.net.getNode("TestNode"));
        assertNotNull(this.net.getNode("ChangedNodeName"));
    }
    
}











