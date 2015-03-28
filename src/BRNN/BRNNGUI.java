package BRNN;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;

import BRNN.LabelData.BFSLabelRotationThread;
import SteelCheckBox.SteelCheckBox;
import anja.swinggui.JRepeatButton;
import appsStandalone.fcvd.voronoi.VoronoiDiagram;

/**
 * This class implements the GUI of the panel and the algorithm. The
 * actionlistener for the buttons are also initalized in here,
 * 
 * The Gui class is a Singleton, so only one instance can be created.
 * 
 * @author Ramtin Azimi
 * @date Winter term 2014/15
 * 
 */
public class BRNNGUI extends JPanel {

	// *************************************************************************
	// Private variables
	// *************************************************************************

	private static final long serialVersionUID = 1L;

	/** boolean for determining whether the animation is running or not */
	private boolean animationInProgress = false;

	/** boolean for determining whether the animation has been started */
	private boolean bfsAni = false;

	/** boolean for determining whether the animation has been paused */
	private boolean aniPaused = false;

	/** Symbol for the play button in the animation mode */
	private String play = "<html> <center>\u25BA<br />Play</html>";

	/** Symbol for the pause button in the animation mode */
	private String pause = "<html> <center>&#9612;&#9612;<br />Pause</html>";

	/** animationSpeed regulator */
	private int aniSpeed = 2;

	private int stepCounter = 0;

	/** if the user goes over the point with the mouse the point pops up */
	private Point pointed;

	/** private JTextfields */
	private JTextField txtField_progress = new JTextField(30);
	
	/** private checkboxes */
	private JCheckBox checkBox_showDual = new JCheckBox("Dual Graph");
	private JCheckBox checkBox_showLabels = new JCheckBox("Labels");
	private JCheckBox checkBox_showVoronoi = new JCheckBox("Voronoi Region");
	private JCheckBox checkBox_showDisks = new JCheckBox("Disks");
	private JCheckBox checkBox_hideCustomers = new JCheckBox("Customers");
	private JCheckBox checkBox_hideFacilities = new JCheckBox("Facilties");
	private JCheckBox checkBox_showNewFacility = new JCheckBox(
			"New facility's location");
	private SteelCheckBox checkBox_runAlgorithm = new SteelCheckBox();

	/** private buttons */
	private JRadioButton button_customer = new JRadioButton("Customer");
	private JRadioButton button_facility = new JRadioButton("Facility");
	private JButton button_clear = new JButton("Clear");
	private JToggleButton button_playAnimation = new JToggleButton(play);
	private JButton button_animationMode = new JButton("Animation mode");
	private JButton button_aniSpeedUp = new JButton(
			"<html> <center>\u25BA\u25BA<br />Speed up</html>");
	private JButton button_aniSpeedDown = new JButton(
			"<html> <center>\u25c4\u25c4<br />Speed down</html>");
	private JButton button_reset = new JButton(
			"<html> <center>\u21A9<br />Reset</html>");
	private JButton button_runExample = new JButton("Display:");
	private JButton button_exit = new JButton(
			"<html> <center>Exit<br />animation mode</html>");
//	private JRepeatButton button_zoomIn = new JRepeatButton();
//	private JRepeatButton button_zoomOut = new JRepeatButton();

	/** private north and south panel */
	private JPanel panel_north = new JPanel();
	private JPanel panel_south = new JPanel();
	private String[] examples = { "Example1", "Example2", "Example3" };

	/** private comboBox for displaying the examples */
	private JComboBox<String> comboBox_ex = new JComboBox<String>(examples);

	/** private labels */
	private Label label_runAlgo = new Label("Run Algorithm:");
	private Label label_off = new Label("OFF");
	private Label label_on = new Label("ON");
	private Label label_space = new Label(" ");
	private Label label_show = new Label("Show: ");
	private Label label_addNew = new Label("Add new: ");

	/** point lists which should be visiualized by the gui */
	private PointList customersList = new PointList(Color.RED);
	private PointList facilitiesList = new PointList(Color.BLUE);

	private Graphics2D g;

	private BRNNAlgorithm algorithm;

	private static BRNNGUI panel;

	/** saving the point which is being dragged */
	public Point dragged;

	/**
	 * private constructor makes sure that the constructor cannot be called and
	 * using this class will be forced to use the getBRNNGUI method.
	 * 
	 * This guarantees that only one instance of the gui will be created
	 * throught the algorithm.
	 * 
	 * @param algorithm
	 */
	private BRNNGUI(BRNNAlgorithm algorithm) {
		this.algorithm = algorithm;

		// Some settings for the buttons how they should be intialized at the
		// starting point
		defaultButtonSettings();

		// creating the panel and buttons
		createGUI();

		// Connecting the gui to the mouse listener so they can communicate with
		// each other
		addMouseListener(MouseHandler.getMouseHandler(this));
		addMouseMotionListener(MouseHandler.getMouseHandler(this));

		// For being able to go on the dual graph nodes with the mouse and the
		// labels appear
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * Makes sure that the same gui which has been created already is being
	 * returned or if none has been created yet a new one will be created.
	 * 
	 * @param algorithm
	 * @return the same instance of the gui which has been already created
	 */
	public static BRNNGUI getBRNNGUI(BRNNAlgorithm algorithm) {
		if (panel == null) {
			panel = new BRNNGUI(algorithm);
		}
		return panel;
	}

	/**
	 * Default button settings at the beginnig of running the applet
	 */
	public void defaultButtonSettings() {

		// Default selected checkboxes
		checkBox_runAlgorithm.setSelected(true);
		checkBox_showNewFacility.setSelected(true);

		txtField_progress.setHorizontalAlignment(JTextField.CENTER);
	

		// Images for the icons
//		ImageIcon zoomOutIcon = createImageIcon("BRNN/zoom_out.png");
//		ImageIcon zoomInIcon = createImageIcon("BRNN/zoom_in.png");
//		ImageIcon clearIcon = createImageIcon("BRNN/clear3.png");
//		button_zoomIn.setIcon(zoomInIcon);
//		button_zoomOut.setIcon(zoomOutIcon);
//		button_clear.setIcon(clearIcon);

		// Disenabling some buttons
		checkBox_showLabels.setEnabled(false);
		button_clear.setEnabled(false);
		button_animationMode.setEnabled(false);

		// Visibility setting
		button_playAnimation.setVisible(false);
		button_aniSpeedDown.setVisible(false);
		button_aniSpeedUp.setVisible(false);
		button_reset.setVisible(false);
		button_exit.setVisible(false);
		txtField_progress.setVisible(false);

		// Setting dimension of the icon buttons
//		Dimension dim = new Dimension(26, 26);
//		button_zoomIn.setPreferredSize(dim);
//		button_zoomOut.setPreferredSize(dim);
//		button_clear.setPreferredSize(dim);

		// Setting the foreground
		button_customer.setForeground(Color.RED);
		button_facility.setForeground(Color.BLUE);
		checkBox_hideCustomers.setForeground(Color.RED);
		checkBox_hideFacilities.setForeground(Color.BLUE);

		// Setting the font of the buttons
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
		checkBox_showDual.setFont(font);
		checkBox_showLabels.setFont(font);
		checkBox_showDisks.setFont(font);
		checkBox_showVoronoi.setFont(font);
		checkBox_showNewFacility.setFont(font);
		button_customer.setFont(font);
		button_facility.setFont(font);
		Font fontBold = new Font(Font.SANS_SERIF, Font.BOLD, 13);
		label_addNew.setFont(fontBold);
		label_show.setFont(fontBold);

		checkBox_runAlgorithm.setRised(true);
		checkBox_runAlgorithm.setColored(true);
		setToolTipText();

	}

	/**
	 * Setting ToolTipText for buttons which mean when one hovers over the
	 * button with the mouse a description of the functionality of button will
	 * be shown
	 */
	public void setToolTipText() {

		button_aniSpeedDown.setToolTipText("Decrease Speed");
		button_aniSpeedUp.setToolTipText("Increase Speed");
		button_reset.setToolTipText("Reset animation");
		button_animationMode.setToolTipText("Step-by-step animation");
		button_exit.setToolTipText("Return to start screen");
//		button_zoomIn.setToolTipText("Zoom in");
//		button_zoomOut.setToolTipText("Zoom out");
		button_clear.setToolTipText("Clear screen");
		checkBox_runAlgorithm
				.setToolTipText("Compute best location for new facility");
		button_runExample.setToolTipText("Display chosen example");
	}

	/**
	 * Loading the images
	 * 
	 * @param path
	 *            path where the image is saved
	 * @return the image
	 */
	public static ImageIcon createImageIcon(String path) {
		Image look = null;
		try {
			look = ImageIO.read(BRNNGUI.class.getClassLoader()
					.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ImageIcon(look);
	}

	/**
	 * Adding the buttons to the north panel
	 */
	public void createNorthPanel() {

		// Designing north panel

		label_runAlgo.setForeground(Color.BLACK);

		panel_north.add(label_runAlgo);
		panel_north.add(label_space);
		panel_north.add(label_off);
		panel_north.add(checkBox_runAlgorithm);
		panel_north.add(label_on);
		panel_north.add(label_space);
		panel_north.add(label_space);
		panel_north.add(button_animationMode);
		panel_north.add(label_space);
		panel_north.add(label_space);
		panel_north.add(button_runExample);
		panel_north.add(comboBox_ex);
		panel_north.add(label_space);
		panel_north.add(label_space);
//		panel_north.add(button_zoomIn);
//		panel_north.add(button_zoomOut);
		panel_north.add(button_clear);
		animationNorthPanel();
		this.add(panel_north, "North");
	}

	/**
	 * Adding the buttons to the north panel when in animation mode
	 */
	public void animationNorthPanel() {

		panel_north.add(button_aniSpeedDown);
		// panel_north.add(txtField_animationSpeed);
		panel_north.add(button_playAnimation);
		panel_north.add(button_aniSpeedUp);
		panel_north.add(button_reset);
		panel_south.add(txtField_progress);
		txtField_progress.setEditable(false);
		panel_north.add(button_exit);
	}

	/**
	 * Adding buttons to the south panel
	 */
	public void createSouthPanel() {

		panel_south.add(label_addNew);
		panel_south.add(button_customer);
		panel_south.add(button_facility);
		panel_south.add(new Label("	"));

		panel_south.add(label_show);
		panel_south.add(checkBox_showVoronoi);
		panel_south.add(checkBox_showDisks);
		panel_south.add(checkBox_showDual);
		panel_south.add(checkBox_showLabels);
		panel_south.add(checkBox_showNewFacility);
		this.add(panel_south, "South");
	}

	/**
	 * Creating the first main parts of the gui
	 */
	public void createGUI() {
		setLayout(new BorderLayout());

		// Add the button controls
		ButtonGroup group = new ButtonGroup();
		group.add(button_facility);
		group.add(button_customer);

		createNorthPanel();
		createSouthPanel();

		// Initialize the radio buttons
		button_customer.doClick();
		registerListeners();
	}

	public void clear() {
		customersList.clear();
		facilitiesList.clear();
		algorithm.clear();
		repaint();
	}

	public void paintComponent(Graphics graph) {

		Graphics2D g = (Graphics2D) graph;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setStroke(new BasicStroke(1));
		super.paintComponent(graph);

		this.g = g;

		drawMaxCell();

		drawAllPoints();
		// Drawing the voronoi diagram
		drawVoronoi();

		drawDisks();
		drawDualGraph();

		drawRotationAnimation();

	}

	// *************************************************************************
	// Draw Methods
	// *************************************************************************
	/**
	 * Visualization of the bfs in the animation
	 */
	public void drawRotationAnimation() {

		if (bfsAni) {

			// Drawing the dual point for the outer cell first
			drawAnimatedOuterCell();

			// The movements of the bfs are saved in datas and we draw the moves
			// one by one
			for (LabelData md : algorithm.datas) {
				md.draw(g);
			}
		}
	}

	/**
	 * Drawing the dual point for the outer cell for animation purposes
	 */
	public void drawAnimatedOuterCell() {

		Point outerDual = algorithm.calculateOuterCellDualPoint();
		double xPos = outerDual.posX;
		double yPos = outerDual.posY;
		String string = "" + 0;
		g.setFont(new Font("default", Font.BOLD, 12));
		g.setColor(Color.WHITE);
		g.fillOval((int) xPos - 10, (int) yPos - 10, 2 * 10, 2 * 10);
		g.setColor(Color.BLACK);
		g.drawOval((int) xPos - 10, (int) yPos - 10, 2 * 10, 2 * 10);
		int stringWidth = (int) Math.floor(g.getFontMetrics()
				.getStringBounds(string, g).getWidth());
		g.drawString(string, (int) (xPos - stringWidth / 2),
				(int) (yPos + 20 / 4));
	}

	/**
	 * Drawing cells with maximum labels where the nex facility should be placed
	 */
	public void drawMaxCell() {

		if (algorithm.maxCells != null && checkBox_showNewFacility.isSelected()) {

			for (Cell c : algorithm.maxCells) {

				if (dragged != null) {
					c.drawArcs(g);
				} else {
					c.drawCell(g);
				}

			}
		}
	}

	public void drawDualGraph() {

		if (checkBox_showDual.isSelected() & algorithm != null
				& checkBox_runAlgorithm.isSelected()) {

			checkBox_showLabels.setEnabled(true);
			if (algorithm.dual != null) {
				algorithm.dual.draw(g, checkBox_showLabels.isSelected());
			}

		} else {
			checkBox_showLabels.setEnabled(false);
			checkBox_showLabels.setSelected(false);
		}
	}

	public void drawDisks() {

		if (checkBox_showDisks.isSelected() && algorithm.disks != null) {

			for (Disk disk : algorithm.disks) {
				disk.draw(g);

			}
		}
	}

	public void drawVoronoi() {
		if (checkBox_showVoronoi.isSelected()) {
			VoronoiDiagram diagram = algorithm.voronoi;

			if (diagram != null) {
				diagram.paint(g);
			}
		}
	}

	/**
	 * Drawing all points, so the customers and the facilities
	 */
	public void drawAllPoints() {

		if (!checkBox_hideCustomers.isSelected()) {
			customersList.draw(g);
		}
		if (!checkBox_hideFacilities.isSelected()) {
			facilitiesList.draw(g);
		}
		// Highlight the point which is selected
		if (dragged != null) {
			dragged.drawHighlight(g);
		}
		if (pointed != null) {
			pointed.drawBoundings(g);
		}

	}

	// *************************************************************************
	// Mouse functionalities
	// *************************************************************************
	
	/**
	 * Moving the mouse while dragging a point
	 * 
	 * @param p
	 */
	public void moveMouse(Point p) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		pointed = null;
		if (pointAlreadyExists(p)) {
			pointed = searchPoint(p);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		repaint();
	}

	/**
	 * Deletes a point if point is clicked with the right mouse
	 * 
	 * @param p
	 *            mouse locations
	 */
	public void rightMouseClick(Point p) {

		if (pointAlreadyExists(p)) {
			deletePoint(p);
		}

		pointed = null;
		algorithm.clear();
		buttonsCheck();
		runAlgorithm();
		repaint();
	}

	/**
	 * Enabling buttons depending on number of customers/facilities
	 */
	public void buttonsCheck() {

		if (customersList.getSize() + facilitiesList.getSize() >= 1) {
			button_clear.setEnabled(true);
		} else {
			button_clear.setEnabled(false);
		}

		if (customersList.getSize() >= 1 && facilitiesList.getSize() >= 1) {
			button_animationMode.setEnabled(true);
		} else {
			button_animationMode.setEnabled(false);
		}
	}

	/**
	 * When a left mouse click occurs, the user will be able to place a new
	 * point if the point does not exist yet. If the point already exists, he
	 * can choose to drag it.
	 * 
	 * @param p
	 *            the location where the mouse has been clicked
	 */
	public void leftMouseClick(Point p) {

		if (!pointAlreadyExists(p)) {
			addPoint(p);
		} else {

			if (customersList.contains(p)) {
				dragged = customersList.search(p);
			} else {
				dragged = facilitiesList.search(p);
			}
			// Highlight the clicked point
			repaint();
		}

		buttonsCheck();
		runAlgorithm();
	}

	
	// *************************************************************************
	// POINT METHODS
	// *************************************************************************
	
	/**
	 * Checks whether entered point already exists
	 * 
	 * @param p
	 *            entered point by mouse click
	 * @return true if the point already exists else false
	 */
	public boolean pointAlreadyExists(Point p) {
		if (customersList.contains(p) || facilitiesList.contains(p)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param p
	 *            Clicked location by mouse
	 * @return point in the lists
	 */
	public Point searchPoint(Point p) {

		if (customersList.contains(p)) {
			return customersList.search(p);
		} else {
			return facilitiesList.search(p);
		}
	}

	/**
	 * For dragging the point
	 * 
	 * @param p
	 *            the point which has been clicked and should be dragged
	 */
	public void dragged(Point p) {

		if (dragged != null && !collisionExists(p)) {
			dragged.posX = p.posX;
			dragged.posY = p.posY;

		}
		runAlgorithm();
		repaint();
	}

	
	/**
	 * Deletes the points when the user has clicked on it with a right mouse
	 * click
	 * 
	 * @param p
	 */
	public void deletePoint(Point p) {

		customersList.remove(p);
		facilitiesList.remove(p);
	}

	/**
	 * Guarantess that two points cannnot be placed or dragged onto each other.
	 * This would not make any sense for the algorithm.
	 * 
	 * @param p
	 * 
	 * @return true if collision exists
	 */
	public boolean collisionExists(Point p) {
		if (!(customersList.collisionExists(p) || facilitiesList
				.collisionExists(p))) {
			return false;
		}
		return true;
	}

	/**
	 * adding a new point to the lists when the user has been using the left
	 * mouse click
	 * 
	 * @param p
	 */
	public void addPoint(Point p) {

		if (!collisionExists(p)) {

			if (button_facility.isSelected()) {
				p.color = Color.BLUE;
				facilitiesList.addPoint(p);
			} else {
				p.color = Color.RED;
				customersList.addPoint(p);
			}
		}
		repaint();
		moveMouse(p);
	}

	// *************************************************************************
	// ACTIONLISTENER FOR BUTTONS
	// *************************************************************************

	/**
	 * The action listener for the buttons will be registered
	 */
	public void registerListeners() {

		CheckBoxListener l = new CheckBoxListener();
		checkBox_showVoronoi.addActionListener(l);
		checkBox_showDisks.addActionListener(l);
		checkBox_hideCustomers.addActionListener(l);
		checkBox_hideFacilities.addActionListener(l);
		checkBox_showDual.addActionListener(l);
		checkBox_showLabels.addActionListener(l);
		checkBox_showNewFacility.addActionListener(l);
		
		button_runExample.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				clear();
				if (comboBox_ex.getSelectedIndex() == 0) {
					example1();
				} else if (comboBox_ex.getSelectedIndex() == 1) {
					example2();
				} else if (comboBox_ex.getSelectedIndex() == 2) {
					example3();
				}

				buttonsCheck();
				if (checkBox_runAlgorithm.isSelected()) {
					runAlgorithm();
				}
			}

		});

		button_aniSpeedDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (LabelData.animationSpeed > 1) {
					LabelData.animationSpeed--;
				}

				

			}

		});

		button_aniSpeedUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (LabelData.animationSpeed < 9) {
					LabelData.animationSpeed++;
				}

				

			}

		});

		button_reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				stepCounter = 0;
				animationInProgress = false;
				showAnimationStep(0);
				repaint();
				runAlgorithm();

			}

		});

		button_animationMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				algorithm.clear();
				repaint();
				setAnimationMode(true);
			}
		});
		button_clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
				repaint();
			}

		});

		checkBox_runAlgorithm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (checkBox_runAlgorithm.isSelected()) {
					// algorithm.runAlgorithm(customersList, facilitiesList);
					runAlgorithm();
				} else {
					algorithm.clear();

				}
				repaint();
			}
		});

		button_exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				animationInProgress = false;
				setAnimationMode(false);
			}

		});

		button_playAnimation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (button_playAnimation.isSelected()) {
					aniPaused = false;

					LabelData.animationSpeed = aniSpeed;
					button_playAnimation.setText(pause);

					if (!animationInProgress) {
						AnimationStepThread ani = new AnimationStepThread();
						ani.start();
					}

				} else {

					aniPaused = true;
					aniSpeed = LabelData.animationSpeed;
					LabelData.animationSpeed = 0;
					button_playAnimation.setText(play);
				}

			}

		});
	}

	private class CheckBoxListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();
		}
	}

	/**
	 * runs the algorithm
	 */
	public void runAlgorithm() {

		if (checkBox_runAlgorithm.isSelected()) {

			if (customersList.getSize() >= 1 && facilitiesList.getSize() >= 1) {
				algorithm.runAlgorithm(customersList, facilitiesList);

			}
		}
	}

	/**
	 * Visualizing the labels when hovered over the dual points with the mouse
	 */
	public String getToolTipText(MouseEvent event) {

		Point p = new Point(event.getX(), event.getY());

		if (algorithm.dual == null) {
			return null;
		}
		for (Cell c : algorithm.dual.theNodeSet) {

			if (p.equals(c.dualPoint)) {

				return "Label:" + c.getLabel();

			}
		}

		return null;

	}

	// *************************************************************************
	// THREE EXAMPLES TO TRY OUT THE ALGORTHMS
	// *************************************************************************

	/**
	 * Easy and really simple example
	 */
	public void example1() {

		List<Point> customers = new ArrayList<Point>(
				Arrays.asList(new Point(444.0, 374.0), new Point(578.0, 319.0),
						new Point(345.0, 260.0)));
		List<Point> facilities = new ArrayList<Point>(Arrays.asList(new Point(
				360.0, 368.0), new Point(585.0, 208.0)));

		customersList.points = customers;
		facilitiesList.points = facilities;
	}

	/**
	 * A more complicated example
	 */
	public void example2() {
		List<Point> customers = new ArrayList<Point>(Arrays.asList(new Point(
				259.0, 197.0), new Point(182.0, 235.0),
				new Point(688.0, 240.0), new Point(762.0, 278.0), new Point(
						205.0, 420.0), new Point(265.0, 405.0), new Point(
						300.0, 407.0)));
		List<Point> facilities = new ArrayList<Point>(Arrays.asList(new Point(
				267.0, 254.0), new Point(424.0, 266.0),
				new Point(637.0, 329.0), new Point(264.0, 358.0), new Point(
						384.0, 474.0), new Point(622.0, 422.0)));

		customersList.points = customers;
		facilitiesList.points = facilities;
	}

	/**
	 * More interesting example with different connectivity graphs
	 */
	public void example3() {
		List<Point> customers = new ArrayList<Point>(Arrays.asList(new Point(
				326.0, 462.0), new Point(630.0, 232.0),
				new Point(147.0, 398.0), new Point(354.0, 341.0), new Point(
						674.0, 312.0), new Point(770.0, 266.0), new Point(
						452.0, 411.0)));
		List<Point> facilities = new ArrayList<Point>(Arrays.asList(new Point(
				348.0, 415.0), new Point(291.0, 428.0),
				new Point(732.0, 206.0), new Point(609.0, 354.0)));

		customersList.points = customers;
		facilitiesList.points = facilities;
	}

	// *************************************************************************
	// START: SETTING UP THE ANIMATION
	// *************************************************************************

	/**
	 * The different stages/steps which the animation goes through will be set
	 * up
	 * 
	 * @param step
	 *            The stage of the algorithm which should be visiualized
	 * @return instance of
	 */
	public BFSAnimationThread showAnimationStep(int step) {

		BFSAnimationThread animationOfLabelComputation = null;
		switch (step) {

		case 0:
			txtField_progress.setText("Initial scene");
			checkBox_hideCustomers.setSelected(false);
			checkBox_hideFacilities.setSelected(false);
			checkBox_showVoronoi.setSelected(false);
			checkBox_showDisks.setSelected(false);
			checkBox_showNewFacility.setSelected(false);
			checkBox_showDual.setSelected(false);
			break;
		case 1:
			txtField_progress.setText("Voronoi Diagram of blue points");
			checkBox_hideCustomers.setSelected(true);
			checkBox_showVoronoi.setSelected(true);
			break;
		case 2:
			checkBox_hideCustomers.setSelected(false);
			checkBox_showDisks.setSelected(false);
			repaint();
			txtField_progress.setText("Locate red points in Voronoi Diagram");
			break;
		case 3:
			checkBox_showDisks.setSelected(true);
			checkBox_showDual.setSelected(false);
			txtField_progress
					.setText("Disk centered at red point to the closest blue point");
			break;
		case 4:
			checkBox_showVoronoi.setSelected(false);
			checkBox_showDisks.setSelected(true);
			checkBox_showDual.setSelected(true);
			checkBox_showLabels.setSelected(false);
			txtField_progress.setText("Dual graph");
			break;
		case 5:
			checkBox_showNewFacility.setSelected(false);
			txtField_progress
					.setText("Labels of dual graph are being computed...");
			animationOfLabelComputation = new BFSAnimationThread();
			animationOfLabelComputation.start();
			break;
		case 6:
			checkBox_showNewFacility.setSelected(true);
			txtField_progress
					.setText("New facility should be located within the yellow region");
			break;
		}

		return animationOfLabelComputation;
	}

	/**
	 * The buttons settings will be set depending on whether the user is in
	 * animation mode or in the normal mode
	 * 
	 * @param mode
	 */
	public void setAnimationMode(boolean mode) {

		LabelData.animationSpeed = 2;
		if (mode) {
			button_playAnimation.setText(play);
		} else {
			button_playAnimation.setText(pause);
		}

		stepCounter = 0;
		// button_prev.setEnabled(!mode);
		// button_next.setEnabled(mode);
		checkBox_runAlgorithm.setSelected(true);
		txtField_progress.setText("Initial scene");

		button_clear.setVisible(!mode);
		button_runExample.setVisible(!mode);
//		button_zoomIn.setVisible(!mode);
//		button_zoomOut.setVisible(!mode);
		button_animationMode.setVisible(!mode);
		comboBox_ex.setVisible(!mode);
		checkBox_runAlgorithm.setVisible(!mode);
		checkBox_showDisks.setVisible(!mode);
		checkBox_showDual.setVisible(!mode);
		checkBox_showLabels.setVisible(!mode);
		checkBox_showVoronoi.setVisible(!mode);
		checkBox_showNewFacility.setVisible(!mode);
		label_runAlgo.setVisible(!mode);
		label_space.setVisible(!mode);
		label_off.setVisible(!mode);
		label_on.setVisible(!mode);
		label_show.setVisible(!mode);
		button_customer.setVisible(!mode);
		button_facility.setVisible(!mode);
		label_addNew.setVisible(!mode);

		
		button_aniSpeedDown.setVisible(mode);
		button_aniSpeedUp.setVisible(mode);
		button_reset.setVisible(mode);
		button_exit.setVisible(mode);
		button_playAnimation.setVisible(mode);
		txtField_progress.setVisible(mode);

		checkBox_showDisks.setSelected(false);
		checkBox_showLabels.setSelected(false);
		checkBox_showDual.setSelected(false);
		checkBox_showVoronoi.setSelected(false);
		checkBox_showNewFacility.setSelected(!mode);

		runAlgorithm();
		MouseHandler mouseHandler = MouseHandler.getMouseHandler(this);
		mouseHandler.setEnabled(!mode);
	}

	/**
	 * Private class for starting a thread.
	 * 
	 * The thread controlls the the walk through of the bfs and guarantees that
	 * every move is displayed after each other
	 * 
	 * @author Ramtin
	 * 
	 */
	private class BFSAnimationThread extends Thread {

		public void run() {

			bfsAni = true;
			for (LabelData ld : algorithm.datas) {

				if (!animationInProgress) {
					bfsAni = false;
					repaint();
					return;
				}
				BFSLabelRotationThread ani = ld.startAnimation();
				ld.gui = BRNNGUI.panel;
				repaint();

				try {
					ani.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			txtField_progress
					.setText("Labels of dual graph: Computation done!");
			repaint();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			bfsAni = false;

		}
	}

	/**
	 * Thread for starting the main animation
	 * 
	 * @author Ramtin
	 * 
	 */
	private class AnimationStepThread extends Thread {

		@Override
		public void run() {

			algorithm.runAlgorithm(customersList, facilitiesList);

			animationInProgress = true;
			for (int i = stepCounter; i < 6; i++) {

				// If resetc clicked animationInProgress will be false and we
				// break out of thread
				if (!animationInProgress) {
					break;
				}

				if (!aniPaused) {
					stepCounter++;
				}

				// The BFSAnimationThread will be returneed by showAnimation
				// when stepCounter is 5
				BFSAnimationThread bfsLabelComputation = showAnimationStep(stepCounter);

				// Starting second therad: BFSAnimation
				if (bfsLabelComputation != null) {
					try {
						bfsLabelComputation.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				repaint();

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}

			button_playAnimation.setSelected(false);
			button_playAnimation.setText(play);
			stepCounter = 0;
			animationInProgress = false;
			repaint();
		}

	}

	// *************************************************************************
	// END: SETTING UP THE ANIMATION
	// *************************************************************************

	public boolean getAnimationState() {
		return animationInProgress;
	}

	
}
