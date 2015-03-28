package BRNN;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

/**
 * MouseHandler class handles the mouse movements and listens to any clicks
 * being made by the user
 * 
 * Connected to panel/gui so mouse clicks will be immediately displayed on the
 * screen.
 * 
 * @author Ramtin
 * 
 */
public class MouseHandler extends MouseAdapter {

	// *************************************************************************
	// Private variables
	// *************************************************************************
	
	private BRNNGUI panel;

	// Enabled is set to false once AnimationMode is on
	private boolean enabled = true;

	private static MouseHandler mouseHandler;

	private MouseHandler(BRNNGUI panel) {
		this.panel = panel;
	}

	public static MouseHandler getMouseHandler(BRNNGUI panel) {

		if (mouseHandler == null) {
			mouseHandler = new MouseHandler(panel);
		}
		return mouseHandler;
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (!enabled) {
			return;
		}
		Point p = new Point(e.getX(), e.getY());

		//Case: Right mouse click
		if (SwingUtilities.isRightMouseButton(e)) {
			panel.rightMouseClick(p);

		}
		//Case: Left mouse click
		else if (SwingUtilities.isLeftMouseButton(e)) {
			panel.leftMouseClick(p);
		}
		
	}

	public void mouseReleased(MouseEvent e) {

		if (!enabled) {
			return;
		}
		// Erase the "click" highlight
		if (panel.dragged != null) {
			panel.repaint();
		}
		panel.dragged = null;

	}

	public void mouseDragged(MouseEvent e) {

		if (!enabled) {
			return;
		}

		Point p = new Point(e.getX(), e.getY());
		panel.dragged(p);

	}

	public void mouseMoved(MouseEvent e) {

		if (!enabled) {
			return;
		}
		Point p = new Point(e.getX(), e.getY());

		panel.moveMouse(p);

	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}
}
