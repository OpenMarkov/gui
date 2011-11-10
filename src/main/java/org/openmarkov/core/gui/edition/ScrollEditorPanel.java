package org.openmarkov.core.gui.edition;


import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JScrollPane;
import javax.swing.JViewport;


/**
 * This class is used only to scroll the network panel.
 * 
 * @author jmendoza
 * @version 1.0
 */
public class ScrollEditorPanel extends JScrollPane implements
				EditorPanelSizeListener, MouseWheelListener {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 1399258497770327040L;

	/**
	 * Panel where networks will be edited.
	 */
	private EditorPanel editorPanel = null;

	/**
	 * This is the default constructor.
	 * 
	 * @param newEditorPanel
	 *            panel to scroll.
	 */
	public ScrollEditorPanel(EditorPanel newEditorPanel) {

		editorPanel = newEditorPanel;
		initialize();

	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		editorPanel.addEditorPanelSizeListener(this);
		editorPanel.setFocusable(true);
		setViewportView(editorPanel);
		setOpaque(false);
		setWheelScrollingEnabled(false);
		addMouseWheelListener(this);

	}

	/**
	 * Notifies the increases of size of the network panel so that it can scroll
	 * properly.
	 * 
	 * @param incrLeft
	 *            increase for the left side.
	 * @param incrTop
	 *            increase overhead.
	 * @param incrRight
	 *            increase for the right side.
	 * @param incrBottom
	 *            increase for below.
	 */
	@SuppressWarnings("unused")
	public void sizeChanged(double incrLeft, double incrTop, double incrRight,
							double incrBottom) {

		JViewport currentViewport = getViewport();
		Point position = currentViewport.getViewPosition();

		currentViewport.setViewPosition(new Point((int) Math.round(position
			.getX()
			+ incrLeft), (int) Math.round(position.getY() + incrTop)));

	}

	/**
	 * Invoked when the mouse wheel is rotated.
	 * 
	 * @param e
	 *            wheel mouse event information.
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {

		scrollPanel(getViewport().getViewPosition().getY()
			+ e.getUnitsToScroll() * 10 * editorPanel.getZoom());

	}

	/**
	 * Scrolls the panel to a new position.
	 * 
	 * @param newPosition
	 *            position where the panel must scroll.
	 */
	private void scrollPanel(double newPosition) {

		JViewport currentViewport = getViewport();
		Point actualPosition = currentViewport.getViewPosition();
		double position = newPosition;

		if (position < 0) {
			position = 0;
		} else if ((position + currentViewport.getHeight()) > editorPanel
			.getMaxHeight()) {
			position = editorPanel.getMaxHeight() - currentViewport.getHeight();
		}
		currentViewport.setViewPosition(new Point((int) Math
			.round(actualPosition.getX()), (int) Math.round(position)));

	}
}
