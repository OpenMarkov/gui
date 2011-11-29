package org.openmarkov.core.gui.action;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    
    MoveNodeEditTest.class,
    
    NodePartitionedIntervalEditTest.class
        })
        
/** @author mkpalacio 
 * */
public class EditTests {

    /** Runs the test suite using the textual runner. */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

	public static Test suite() {
		return new JUnit4TestAdapter(EditTests.class);
	}

}

