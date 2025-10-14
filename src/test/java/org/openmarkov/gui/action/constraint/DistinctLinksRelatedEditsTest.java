/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.action.constraint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openmarkov.core.exception.ConstraintViolatedException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.DistinctLinks;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.action.base.PNESupport;
import org.openmarkov.core.action.base.linkEdits.AddLinkEdit;
import org.openmarkov.core.action.base.linkEdits.InvertLinkEdit;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DistinctLinksRelatedEditsTest {
    
    private ProbNet influenceDiagram;
    
    @BeforeEach public void setUp() {
        influenceDiagram = ConstraintsTests.getInfuenceDiagram();
    }
    
    @Disabled
    @Test
    public void testUndoableEditWillHappen() throws ConstraintViolatedException, DoEditException.CannotInvertLink {
        PNESupport pNESupport = new PNESupport(false);
        PNConstraint constraint = new DistinctLinks();
        
        influenceDiagram.addConstraint(constraint);
        pNESupport.addUndoableEditListener(constraint);
        
        // do legal AddLink: add an directed link between U and A
        Variable vU = influenceDiagram.getVariable("U");
        Variable vA = influenceDiagram.getVariable("A");
        // creates an undirected link from node A to D
        AddLinkEdit legalEdit = new AddLinkEdit(influenceDiagram, vU, vA, true);
        pNESupport.announceEdit(legalEdit);
        legalEdit.doEdit();
        
        // do ilegal LinkAdd. Add an directed link between A and U
        AddLinkEdit ilegalAdd = new AddLinkEdit(influenceDiagram, vA, vU, true);
        try {
            ilegalAdd.doEdit(influenceDiagram);
            fail();
        } catch (ConstraintViolatedException e) {
            // The constraint should have faild
        }
        
        Variable vD = influenceDiagram.getVariable("D");
        // do ilegal InvertLink. Add an directed link between D and U
        InvertLinkEdit ilegalInvertLinkEdit = new InvertLinkEdit(influenceDiagram, vU, vD, true);
        try {
            ilegalAdd.doEdit(influenceDiagram);
            fail();
        } catch (ConstraintViolatedException e) {
            // The constraint should have failed
        }
        
        // do legal invert link: create undirected link between U and D
        InvertLinkEdit legalInvertLinkEdit = new InvertLinkEdit(influenceDiagram, vU, vD, false);
        
        pNESupport.announceEdit(legalInvertLinkEdit);
        legalInvertLinkEdit.doEdit();
        
        
        // do ilegal LinkEdit. Add an undirected link between U and D
        AddLinkEdit ilegalLinkEdit = new AddLinkEdit(influenceDiagram, influenceDiagram.getVariable("U"),
                                                     influenceDiagram.getVariable("D"), false);
        try {
            ilegalLinkEdit.doEdit(influenceDiagram);
            fail();
        } catch (ConstraintViolatedException e) {
            // The constraint should have failed
        }
        
    }
    
}
