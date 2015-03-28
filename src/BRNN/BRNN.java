package BRNN;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;

/**
 * Main class for starting the BRNN program
 * 
 * 
 * @supervisor: Elmar Langetepe
 * @topic: Bichromatic Reverse Nearest Neighbor - Locating a new faciliy in a
 *         city with customers and facilities
 * @category: Computational Geometry - project group - BA-INF 051
 * @author: Ramtin Azimi Garakani
 * 
 * @date: Winter term 2014/2015
 * 
 */

public class BRNN {

	// *************************************************************************
	// Private variables
	// *************************************************************************
	
	//Title of the project
	private static final String TITLE = "Bichromatic Reverse Nearest Neighbor";
	
	private static final int DEFAULT_WIDTH = 1000;
	
	private static final int DEFAULT_HEIGHT = 600;

	public static void main(String[] args) {

		JFrame frame = new JFrame(); // Create window
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT); // Set window size
		frame.setTitle(TITLE); // Set window title
		frame.setLayout(new BorderLayout()); // Specify layout manager
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Specify closing
																// behavior
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);

		BRNNAlgorithm algorithm = new BRNNAlgorithm();
		BRNNGUI panel = BRNNGUI.getBRNNGUI(algorithm);
		panel.setBackground(Color.WHITE);

		frame.add(panel, "Center"); // Place panel into window
		frame.setVisible(true); // Show the window

		BRNNApplet applet = new BRNNApplet(); // Create applet
		applet.setVisible(true); // Applet initialization

	}

}
