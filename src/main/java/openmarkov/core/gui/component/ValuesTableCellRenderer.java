/**
 * 
 */
package openmarkov.core.gui.component;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import openmarkov.core.gui.loader.element.IconLoader;
import openmarkov.core.gui.utils.OpenMarkovPreferences;



/**
 * This class is used for painting and colouring the table and the headers
 * 
 * @author jlgozalo
 * @version 1.0 15/08/2009
 */
public class ValuesTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * default serial ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * first color to use in header rows
	 */
	private static final Color TABLE_HEADER_TEXT_COLOR_1 =
		OpenMarkovPreferences.getColor(
			OpenMarkovPreferences.TABLE_HEADER_TEXT_COLOR_1,
			OpenMarkovPreferences.OPENMARKOV_COLORS,
			Color.BLACK);
	/**
	 * second color to use in header rows
	 */
	private static final Color TABLE_HEADER_TEXT_COLOR_2 =
		OpenMarkovPreferences.getColor(
			OpenMarkovPreferences.TABLE_HEADER_TEXT_COLOR_2,
			OpenMarkovPreferences.OPENMARKOV_COLORS ,
			Color.BLACK);
	/**
	 * third color to use in header rows
	 */
	private static final Color TABLE_HEADER_TEXT_COLOR_3 =
		OpenMarkovPreferences.getColor(
			OpenMarkovPreferences.TABLE_HEADER_TEXT_COLOR_3,
			OpenMarkovPreferences.OPENMARKOV_COLORS,
			Color.BLACK);
	
	private boolean [] marked;

	/**
	 * to define the first editable row of the table
	 */
	private int firstEditableRow;
	
	private JLabel jLabelIcon;
	
	private IconLoader iconLoader;

	/**
	 * constructor for the renderer
	 * 
	 * @param firstEditableRow
	 *            value of the first editable row
	 * @param marked boolean array with the columns with (1)/without (0) mark. 
	 * The array only has to contain indexes for the editables columns
	 */
	public ValuesTableCellRenderer(int firstEditableRow, boolean [] marked) {
		this.marked = marked;

		this.firstEditableRow = firstEditableRow;
	}

	/**
	 * headers rows are displayed in a gray background color with red an blue
	 * foreground alternatively non headers rows are displayed in an alternative
	 * cyan and light gray background color with black foreground color the
	 * first two column are in gray
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
													boolean isSelected,
													boolean hasFocus, int row,
													int column) {
		if ( marked == null ){
			marked = new boolean[table.getColumnCount()-2];
		}
		
		setHorizontalAlignment( SwingConstants.CENTER );
		setCellFonts( table, value, isSelected, hasFocus, row, column );
		setCellColors( table, value, isSelected, hasFocus, row, column );
		setCellBorders( table, value, isSelected, hasFocus, row, column );
		
		
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN)
				&& (row >= firstEditableRow) && !hasFocus && marked[column-1])
			return jLabelIcon;
		else
			return super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column );
	}

	/**
	 * set cell fonts
	 * 
	 * @param table -
	 *            table where the cell is located
	 * @param value -
	 *            the value of the cell in edition
	 * @param isSelected -
	 *            true if the cell is selected by the user
	 * @param hasFocus -
	 *            true if the cell has the focus by the user
	 * @param row -
	 *            row of the cell
	 * @param column -
	 *            column of the cell
	 */
	private void setCellFonts(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int column) {

		Font sansboldFont = new Font( "SansSerif", Font.BOLD, 30 );
		Font sansFont = new Font( "SansSerif", Font.PLAIN, 14 );
		if ((column < ValuesTable.FIRST_EDITABLE_COLUMN)
			& (row < firstEditableRow)) { // PARENTS CELLS
			setFont( sansboldFont );
		}
		if ((column < ValuesTable.FIRST_EDITABLE_COLUMN)
			& (row >= firstEditableRow)) { // NODE STATES CELLS
			setFont( sansboldFont );
		}
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN)
			& (row < firstEditableRow)) { // HEADER CELLS
			setFont( sansboldFont );
		}
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN)
			& (row >= firstEditableRow)) { // DATA CELLS
			setFont( sansFont );
		}
	}

	/**
	 * set cell colors
	 * 
	 * @param table -
	 *            table where the cell is located
	 * @param value -
	 *            the value of the cell in edition
	 * @param isSelected -
	 *            true if the cell is selected by the user
	 * @param hasFocus -
	 *            true if the cell has the focus by the user
	 * @param row -
	 *            row of the cell
	 * @param column -
	 *            column of the cell
	 */
	private void setCellColors(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int column) {

		if ((column < ValuesTable.FIRST_EDITABLE_COLUMN)
			& (row < firstEditableRow)) { // PARENTS CELLS
			// set alternate colors
			switch (row % 3) {
			case 0:
				setBackground( new Color(220,220,220));
				setForeground( TABLE_HEADER_TEXT_COLOR_1 );
				break;
			case 1:
				setBackground( new Color(220,220,220));
				setForeground( TABLE_HEADER_TEXT_COLOR_2 );
				break;
			case 2:
				setBackground( new Color(220,220,220));
				setForeground( TABLE_HEADER_TEXT_COLOR_3 );
				break;
			default:
				break;
			}
		}
		if ((column < ValuesTable.FIRST_EDITABLE_COLUMN)
			& (row >= firstEditableRow)) { // NODE STATES CELLS
			//setBackground( Color.LIGHT_GRAY );
			setBackground( new Color(220,220,220));
			setForeground( Color.BLACK );
		}
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN)
			& (row < firstEditableRow)) { // HEADER CELLS
			switch (row % 3) {
			case 0:
				setBackground( new Color(220,220,220));
				break;
			case 1:
				setBackground( new Color(220,220,220));
				break;
			case 2:
				setBackground( new Color(220,220,220));
				break;
			default:
				break;
			}
			switch (row % 2) {
			case 0:
				if (column % 2 == 0) {
					setForeground( new Color (128,0,64) );
				} else {
					setForeground( Color.BLUE.darker() );
				}
				break;
			case 1:
				if (column % 2 == 0) {
					setForeground( Color.BLUE.darker() );
				} else {
					setForeground( new Color (128,0,64).darker() );
				}
				break;
			default:
				break;
			}
		}
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN) && firstEditableRow >=0
			&& (row >= firstEditableRow )) {
						
			/*if (row % 2 == 0) {
				setBackground( new Color( 180, 230, 225 ) );
				setBackground( new Color( 200, 230, 232 ) );
				
				
			} else {
				setBackground( new Color( 115, 210, 200 ) );
				setBackground( new Color( 214, 244, 246 ) );
			}*/
			if(table.getValueAt(row, column) != null)
				getJLabelIcon().setText(table.getValueAt(row, column).toString());
			setBackground( Color.WHITE );
			setForeground( Color.BLACK );

			if (hasFocus) {
				if (table.isCellEditable( row, column )) {
					// setForeground(UIManager.getColor("Table.focusCellForeground"));
					// setBackground(UIManager.getColor("Table.focusCellBackground"));
					setForeground( Color.BLUE );
					setBackground( Color.YELLOW );
					
				}
			}
			
		}

	}


	// ESCA-JAVA0173: not considering unused parameters for the method.
	/**
	 * set cell borders
	 * 
	 * @param table -
	 *            table where the cell is located
	 * @param value -
	 *            the value of the cell in edition
	 * @param isSelected -
	 *            true if the cell is selected by the user
	 * @param hasFocus -
	 *            true if the cell has the focus by the user
	 * @param row -
	 *            row of the cell
	 * @param column -
	 *            column of the cell
	 */
	private void setCellBorders(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int column) {

		setBorder( new LineBorder(Color.BLACK, 5) );
		if (hasFocus) {
			if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN)
				& (row >= firstEditableRow)) {
				setBorder( UIManager
					.getBorder( "Table.focusCellHighlightBorder" ) );
			} else {
				setBorder( UIManager
					.getBorder( "Table.focusCellHighlightBorder" ) );
			}
		} 
	}

	/**
	 * @return the firstEditableRow
	 */
	public int getFirstEditableRow() {

		return firstEditableRow;
	}

	/**
	 * @param firstEditableRow
	 *            the firstEditableRow to set
	 */
	public void setFirstEditableRow(int firstEditableRow) {

		this.firstEditableRow = firstEditableRow;
	}
	
	/**
	 * 
	 * @param column index of the column to mark
	 */
	public void setMark(int column) {
		if ( column < marked.length){
			marked [column] = true;
		}
		
	}
	/**
	 * 
	 * @param column index of the column to unmark
	 */
	public void unMark(int column) {
		if ( column < marked.length){
			marked [column] = false;
		}
		
	}
	/**
	 * This method initialises jButtonApply.
	 * 
	 * @return a new Apply button.
	 */
	private JLabel getJLabelIcon() {

		if (jLabelIcon == null) {
			iconLoader = new IconLoader();
		
			jLabelIcon = new JLabel();
			jLabelIcon.setName("jButtonApply");
			jLabelIcon.setOpaque(true);
			jLabelIcon.setIcon(iconLoader
				.load(IconLoader.ICON_UNCERTAINTY));
			jLabelIcon.setText("Prueba");
			jLabelIcon.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelIcon.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelIcon.setIconTextGap(0);
			jLabelIcon.setBackground(Color.WHITE);
			//jButtonApply.setMnemonic(getStringResource().getString(
				//"OKCancelApplyHorizontalDialog.jButtonApply.Mnemonic").charAt(0));
			/*jButtonApply.addActionListener(new ActionListener() {

				@SuppressWarnings("unused")
				public void actionPerformed(ActionEvent e) {

					if (doOkClickBeforeHide()) {
						selectedButton = APPLY_BUTTON;
					}
				}
			});*/
		}
		return jLabelIcon;
	}

}
