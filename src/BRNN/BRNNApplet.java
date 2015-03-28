package BRNN;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JApplet;

/**
 * The applet for use with the BRNN algorithm
 * 
 * @author Ramtin Azimi
 * @date Winter term 2014/15
 * 
 */
public class BRNNApplet extends JApplet implements Runnable {


	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_WIDTH = 1000;
	private static final int DEFAULT_HEIGHT = 600;
	
	public void init() {
		try {
			javax.swing.SwingUtilities.invokeAndWait(this);
		} catch (Exception e) {
			System.err.println("Applet didn't successfully complete");
		}
	}

	public void run() {

		this.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		BRNNAlgorithm algorithm = new BRNNAlgorithm();
		BRNNGUI panel = BRNNGUI.getBRNNGUI(algorithm);

		// Build the panel
		panel.setBackground(Color.WHITE);
		this.add(panel, "Center");
	}

}
