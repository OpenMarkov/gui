package org.openmarkov.core.gui.dialog.common;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;




/**
 * This class tests the PotentialsTablePanelTest class (not the visual
 * behaviour).
 * 
 * @author jlgozalo
 * @version 1.0
 */
public class PotentialsTablePanelTest {

	PotentialsTablePanel panel = null;


	@Before
	public void setUp() throws Exception {
		ProbNet probNet = new ProbNet();
		
		panel = new PotentialsTablePanel(probNet.addVariable(
				new Variable("A", 2), NodeType.CHANCE));
		
	}

	/**
	 * test to verify the getColumnsIdsSpreedSheetStyle method
	 */
	//@SuppressWarnings("deprecation")
	@Test
	public void testGetColumnsIdsSpreedSheetStyle () {

	    String[] columnIds = null;
	    int numColumns = 0;
	    columnIds = ValuesTable.getColumnsIdsSpreedSheetStyle( numColumns);
	    assertEquals(0,columnIds.length);
		numColumns = 1;
	    columnIds = ValuesTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(1,columnIds.length);
		assertEquals("A",columnIds[0]);
		numColumns = 2;
	    columnIds = ValuesTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(2,columnIds.length);
		assertEquals("B",columnIds[1]);
		numColumns = 3;
	    columnIds = ValuesTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(3,columnIds.length);
		assertEquals("C",columnIds[2]);
		numColumns = 26;
	    columnIds = ValuesTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(26,columnIds.length);
		assertEquals("Z",columnIds[25]);
		numColumns = 27;
	    columnIds = ValuesTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(27,columnIds.length);
		assertEquals("AA",columnIds[26]);
		numColumns = 52;
	    columnIds = ValuesTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(52,columnIds.length);
		assertEquals("AZ",columnIds[51]);
		numColumns = 53;
	    columnIds = ValuesTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(53,columnIds.length);
		assertEquals("BA",columnIds[52]);
		numColumns = 702;
	    columnIds = ValuesTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(702,columnIds.length);
		assertEquals("ZZ",columnIds[701]);
		
		//not implemented
		//TODO review if has to be implemented 
		/*numColumns = 702;
	    columnIds = NodePotentialTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(703,columnIds.length);
		assertEquals("AAA",columnIds[702]); //AAA
		numColumns = 729;
	    columnIds = NodePotentialTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(729,columnIds.length);
		assertEquals("ABA",columnIds[728]); //ABA
		numColumns = 1405;
	    columnIds = NodePotentialTable.getColumnsIdsSpreedSheetStyle( numColumns);
		assertEquals(1405,columnIds.length);
		assertEquals("BAA",columnIds[1405]);//BAA
		*/
		
	}


}
