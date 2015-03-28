package BRNN;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import anja.geom.Circle2;
import anja.geom.Intersection;
import anja.geom.Point2;
import anja.geom.Segment2;
import anja.util.GraphicsContext;
import appsSwingGui.topoVoro.Arc2_Sweep;
import appsSwingGui.topoVoro.dcel.DCEL_Edge;
import appsSwingGui.topoVoro.dcel.DCEL_Face;

/**
 * This class implements the cell of arrangement. A cell is basically a face of
 * an arrangement.
 * 
 * But we have additional information provided in the cell class which are
 * essential for the algorithm, such as the dual point of a cell, the label etc.
 * The dual point is placed within the cell and form the dual graph of an
 * arrangement.
 * 
 * @author Ramtin Azimi
 * 
 */
public class Cell {

	// *************************************************************************
	// Private variables
	// *************************************************************************

	/** cell consists of face; face has the arcs/edges surrounding a cell */
	public DCEL_Face face;

	/** this is the color the arcs/edges of the cell should be colored with */
	private Color arcColor = Color.ORANGE;

	/** this is the color the cell should be colored with. */
	private Color cellColor = Color.ORANGE;

	/** a label tells us the number of circles a cell is contained in */
	private int label = -1;

	// *************************************************************************
	// Public variables
	// *************************************************************************
	/** The dual Point of a cell, a point which is located somewhere in the cell */
	public Point dualPoint;

	public Cell(DCEL_Face face, int label) {

		this.face = face;
		this.label = label;
		calculateDualPoint();

	}

	/**
	 * This Constrcutor will be only used for the outer faces.
	 * 
	 * Therefore the label is being set to 0.
	 * 
	 * @param face
	 * @param dualPoint
	 */
	public Cell(DCEL_Face face, Point dualPoint) {
		this.face = face;
		this.label = 0;
		this.dualPoint = dualPoint;

	}

	/**
	 * This cell is being used by any other cell except outer cells.
	 * 
	 * The default value for label is -1.
	 * 
	 * @param face
	 */
	public Cell(DCEL_Face face) {
		this(face, -1);

	}

	/**
	 * This method gets a vertical segment whose x coordinate is guaranteed to
	 * be located within the cells boudings
	 * 
	 * It gets the two vertical lines which bound the cell from left and right.
	 * Then takes the vertical line which is in the middle of those two lines.
	 * This line will be returned. Since we work with segments we give that line
	 * two ending points in the y- direction which will just be the highest and
	 * lowest point of the cell
	 * 
	 * @return
	 */
	public Segment2 getVerticalSegmentOfCell() {

		List<Float> boundings = getBoundingsOfCell();

		// This will be the x-coordinate of the vertical line
		float midPointX = (boundings.get(1) - boundings.get(0)) / 2 + boundings.get(0);

		// Since we are working with segment we give him a source and a target
		// which are basically not too important for the rest of the algorithmF
		Point2 source = new Point2(midPointX, boundings.get(2));
		Point2 target = new Point2(midPointX, boundings.get(3));

		Segment2 segment = new Segment2(source, target);

		return segment;
	}

	
	/**
	 * Getting boundings of cell
	 * 
	 * @return list of points which bound the cell
	 * 			index0: left most bounding point
	 * 			index1: right most bounding point
	 * 			index2: highest bouding point
	 * 			index3: lowest bounding point
	 */
	public List<Float> getBoundingsOfCell() {

		float minXPoint = Float.POSITIVE_INFINITY;
		float maxXPoint = Float.NEGATIVE_INFINITY;
		float minYPoint = Float.POSITIVE_INFINITY;
		float maxYPoint = Float.NEGATIVE_INFINITY;

		for (int i = 0; i < face.edges().size(); i++) {

			DCEL_Edge e = (DCEL_Edge) face.edges().get(i);

			Arc2_Sweep arc = (Arc2_Sweep) e.reference;

			// Looking for most left located point of cell
			if (minXPoint > arc.minX().x) {
				minXPoint = arc.minX().x;
			}
			// Looking for most right located point of cell
			if (maxXPoint < arc.maxX().x) {
				maxXPoint = arc.maxX().x;
			}
			// Looking for highest located point of cell
			if (minYPoint > arc.minY().y) {
				minYPoint = arc.minY().y;
			}
			// Looking for lowest located point of cell
			if (maxYPoint < arc.maxY().y) {
				maxYPoint = arc.maxY().y;
			}

		}

		List<Float> boundings = new ArrayList<Float>();
		boundings.add(minXPoint);
		boundings.add(maxXPoint);
		boundings.add(minYPoint);
		boundings.add(maxYPoint);
		return boundings;

	}

	/**
	 * Creating many vertical lines in a cell so it looks like the cell is
	 * filled with a color
	 * 
	 * @return all the vertical line segments which fill up the cell
	 */
	public List<Segment2> verticalLinesinCell() {

		List<Float> boundings = getBoundingsOfCell();
		int partitionPrecision = 1000;
		float distanceBtwLineSegments = (boundings.get(1) - boundings.get(0))
				/ partitionPrecision;

		List<Segment2> segments = new ArrayList<Segment2>();
		double startingPoint = boundings.get(0);
		int i = 0;

		while (startingPoint < boundings.get(1)) {

			startingPoint = boundings.get(0) + i
					* distanceBtwLineSegments;
			Point2 source = new Point2(startingPoint,
					boundings.get(2));
			Point2 target = new Point2(startingPoint,
					boundings.get(3));

			Segment2 segment = new Segment2(source, target);
			i++;
			segments.add(segment);
		}

		return segments;

	}

	public List<Segment2> partitionCellIntoSegments(List<Segment2> verticalLines) {

		List<PointList> inters = new ArrayList<PointList>();
		List<Segment2> realSegments = new ArrayList<Segment2>();

		// Getting intersections of vertical lines with the cells and saving
		// those intersections for every
		// line in a LIST of PointLists
		for (Segment2 s : verticalLines) {
			PointList intersectionsNew = getIntersectionsWithArcs(s);
			PointList listOfInters = new PointList();

			for (Point p : intersectionsNew.points) {
				listOfInters.addPoint(p);
			}
			inters.add(listOfInters);
		}

		// Going through LIST of PointLists and we retrieve the part of the
		// vertical Line
		// which is contained in the cell
		for (PointList list : inters) {

			Collections.sort(list.points, Point.COMPARE_BY_YCoord);
			if (list.getPoints().size() % 2 == 0) {

				for (int j = 0; j < list.getPoints().size(); j = j + 2) {
					Point s = list.getPoints().get(j);
					Point t = list.getPoints().get(j + 1);

					Point2 source = new Point2(s.posX, s.posY);
					Point2 target = new Point2(t.posX, t.posY);

					Segment2 segment = new Segment2(source, target);
					realSegments.add(segment);

				}
			}
		}

		return realSegments;
	}

	/**
	 * Determining the intersections of a segment with the arcs of the cell
	 * 
	 * @param segment
	 * @return list of intersections
	 */
	public PointList getIntersectionsWithArcs(Segment2 segment) {

		PointList intersections = new PointList();

		for (int j = 0; j < face.edges().size(); j++) {
			DCEL_Edge e = (DCEL_Edge) face.edges().get(j);
			Arc2_Sweep arc = (Arc2_Sweep) e.reference;

			Circle2 d = new Circle2(new Point2(arc.centre.x, arc.centre.y),
					(int) arc.radius);

			Intersection inter = d.intersection(segment);

			for (int i = 0; i < inter.list.length(); i++) {
				Point2 p = (Point2) inter.list.getValueAt(i);

				// Check whether point lies also on arc:
				// If yes: it is an intersection with an arc and we add it
				// to our list
				if (arc.liesOn(p)) {
					intersections.addPoint(new Point(p.x, p.y));
				}
			}
		}

		return intersections;
	}

	/**
	 * Calculating the dual point of a cell if it is not a outer cell
	 * 
	 * @return
	 */
	public void calculateDualPoint() {

		Point dualPoint = new Point();

		// Go through all edges of the cell
		for (int i = 0; i < face.edges().size(); i++) {

			DCEL_Edge e = (DCEL_Edge) face.edges().get(i);

			//
			if (e.reference instanceof Arc2_Sweep) {

				// In order to get the dual point we do the following:
				// 1. Calculate a segment which is fully contained in the cell
				// 1.1 First get a vertical segment whose x coordinate are
				// within cell x- coordinates
				// 1.2 Calculate the intersections of this segment with the
				// cell's arcs
				// 1.3 Sort the intersections with respect to their y
				// coordinates
				// 1.4 Take the first two coordingates.
				// 1.5 Those two coordinates will be the starting and the ending
				// point of the vertical line. This vertical line is fully
				// contained in the cell
				// 2. Now take the center of the segment

				// Step 1.1
				Segment2 segment = getVerticalSegmentOfCell();
				// Step 1.2
				PointList intersections = getIntersectionsWithArcs(segment);
				// Step 1.3
				Collections.sort(intersections.points, Point.COMPARE_BY_YCoord);
				// Step 1.4
				Point2 startingPoint = new Point2(intersections.getPoints()
						.get(0).posX, intersections.getPoints().get(0).posY);
				Point2 endingPoint = new Point2(intersections.getPoints()
						.get(1).posX, intersections.getPoints().get(1).posY);
				// Step 1.5
				Segment2 segmentInCell = new Segment2(startingPoint,
						endingPoint);
				segment = segmentInCell;
				// Step 2
				dualPoint = new Point((int) segmentInCell.center().x,
						(int) segmentInCell.center().y, Color.GREEN);

			} else if (e.reference instanceof Circle2) {

				Circle2 circle = (Circle2) e.reference;
				dualPoint = new Point(circle.centre.x, circle.centre.y,
						Color.GREEN);
			}
		}

		this.dualPoint = dualPoint;
	}

	/**
	 * For filling up the cell with a color
	 * 
	 * @param g
	 */
	public void drawCell(Graphics2D g) {

		Stroke oldStroke = g.getStroke();
		DCEL_Edge e = (DCEL_Edge) face.edges().get(0);
		// If the cell is a circle we fill the circle with the Java API
		if (e.reference instanceof Circle2) {
			Circle2 c = (Circle2) e.reference;
			g.setColor(cellColor);
			g.fillOval((int) (c.centre.x - c.radius),
					(int) (c.centre.y - c.radius), (int) (2 * c.radius),
					(int) (2 * c.radius));
		}
		// In case it is an arbitary cell we approximate that cell with 1000
		// vertical line segments
		else {
			List<Segment2> verticalLines = verticalLinesinCell();
			List<Segment2> segmentsInCell = partitionCellIntoSegments(verticalLines);

			for (Segment2 s : segmentsInCell) {
				GraphicsContext gc = new GraphicsContext();
				gc.setForegroundColor(cellColor);
				s.draw(g, gc);
			}
		}

		g.setStroke(oldStroke);
	}

	/**
	 * Drawing the arcs of a cell.
	 * 
	 * In use when the points are being dragged since it would take too much
	 * computation time to fill up the cell while dragging points
	 * 
	 * @param g
	 */
	public void drawArcs(Graphics2D g) {

		Stroke oldStroke = g.getStroke();
		GraphicsContext gc = new GraphicsContext();
		gc.setForegroundColor(arcColor);
		gc.setStroke(new BasicStroke(4));

		for (int j = 0; j < face.edges().size(); j++) {

			DCEL_Edge edge = (DCEL_Edge) face.edges().get(j);
			if (edge.reference instanceof Arc2_Sweep) {
				Arc2_Sweep arc = (Arc2_Sweep) edge.reference;
				arc.draw(g, gc);
			} else if (edge.reference instanceof Circle2) {
				Circle2 circle = (Circle2) edge.reference;
				circle.draw(g, gc);
			}
		}

		g.setStroke(oldStroke);
	}

	/**
	 * Drawing the Dual point of a cell.
	 * 
	 * Depending of whether the label of the cell should be shown or not the
	 * dual point varies in size
	 * 
	 * @param g
	 * @param showLabel
	 *            boolean whether labels should be shown or not
	 */
	public void drawDual(Graphics2D g, boolean showLabel) {

		if (showLabel) {
			// Setting a big size for the radius
			dualPoint.setRadius(false);
			dualPoint.draw(g, Color.WHITE);

			// Place the label number within the boundings of dual point
			g.setFont(new Font("default", Font.BOLD, 12));
			String string = Integer.toString(getLabel());
			int stringWidth = (int) Math.floor(g.getFontMetrics()
					.getStringBounds(string, g).getWidth());
			int height = 20;
			g.drawString(string, (int) (dualPoint.posX - stringWidth / 2),
					(int) (dualPoint.posY + height / 4));
		} else {
			dualPoint.setRadius(true);
			dualPoint.draw(g, Color.WHITE);
		}

	}
	
	/** Computing the label of a cell, or basically: The number of circles a cell
	 * is contained in
	 * 
	 * @param Cell
	 * 
	 */
	public int computeLabel(Cell predecessor) {

		// We have two cells: 1. The cell we are leaving and the cell we are
		// entering.Lets call them appropiately cellLeave and cellEnter

		DCEL_Edge edge = getConnectingEdge(predecessor);

		// if (edge == null) {
		// //
		// System.err.println("ERROR: Cells are not neighbors; Should not have happened");
		// return;
		// }

		Point2 pointInRoot = new Point2(predecessor.dualPoint.posX,
				predecessor.dualPoint.posY);
		if (edge.reference instanceof Arc2_Sweep) {
			Arc2_Sweep arc = (Arc2_Sweep) edge.reference;
			// There is an edge/arc between the two cells
			// We get the midpoint of this arc. Lets call this point:
			// CenterOfArc
			Point2 centerOfArc = arc.centre;

			// And we calculate the distance from the midpoint of the arc to the
			// dualPoint in CellLeave
			float dist2 = (float) centerOfArc.distance(pointInRoot);

			// if (cellLeave.label == 0) {
			// this.label = 1;
			// }
			// dist1<= dist2 means the arc is bended into the direction we are
			// going to --> entering a new disk
			if (Math.round(arc.radius * 1000) / 1000 <= Math
					.round(dist2 * 1000) / 1000) {
				// System.out.print("**Mode: Entering a new Cell +1| CellLeaveLabel: "+cellLeave.label);
				// this.setLabel(cellLeave.label + 1);
				return 1;
			}
			// leaving a disk
			else {
				// System.out.print("***Mode: Leaving a cell: -1| CellLeaveLabel: "+cellLeave.label);
				// this.setLabel(cellLeave.label - 1);
				return -1;
			}
		} else if (edge.reference instanceof Circle2) {
			// System.out.print("**Mode: CirlceMode Label will be 1");
			// this.setLabel(1);
			return 1;
		}

		return 0;

	}

	/**
	 * Getting the edge which connects this cell and the given cell as a
	 * parameter
	 * 
	 * @param c
	 *            cell with which the common edge is being looked for
	 * @return the common edge
	 * 
	 */
	public DCEL_Edge getConnectingEdge(Cell c) {

		@SuppressWarnings("unchecked")
		Iterator<DCEL_Edge> it = (Iterator<DCEL_Edge>) face.edges().iterator();

		while (it.hasNext()) {

			DCEL_Edge e = it.next();

			if ((e.getLeftFace().equals(this.face) && e.getRightFace().equals(
					c.face))
					|| (e.getLeftFace().equals(c.face) && e.getRightFace()
							.equals(this.face))) {

				return e;
			}

		}

		System.err
				.println("Edge Not Found-Error: This should not have happened");
		return null;
	}

	public int hashCode() {

		return face.hashCode();
	}

	public void setLabel(int label) {
		if (this.label == 0) {
			// System.out.println("Label is 0. So it is an outerface and we dont change it anymore");
			return;
		} else if (label > this.label) {
			// System.out.println("Changing label:"+this.label+"-->"+label);
			this.label = label;
		}

	}

	public int getLabel() {
		return label;
	}

	

	public String toString() {

		return face.toString();
	}

	public boolean equals(Object o) {

		if (o instanceof Cell) {

			Cell c = (Cell) o;

			if (face.equals(c.face)) {

				return true;
			}
		} else {
			System.out.println("FACES NOT EQUALS");
		}
		return false;
	}

}
