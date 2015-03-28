package BRNN;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import anja.geom.Circle2;
import anja.geom.Point2;
import anja.util.SimpleList;
import appsStandalone.fcvd.voronoi.VoronoiDiagram;
import appsStandalone.fcvd.voronoi.VoronoiException;
import appsSwingGui.topoVoro.Arc2_Sweep;
import appsSwingGui.topoVoro.ArcSweep;
import appsSwingGui.topoVoro.dcel.DCEL;
import appsSwingGui.topoVoro.dcel.DCEL_Edge;
import appsSwingGui.topoVoro.dcel.DCEL_Face;

/**
 * This is the main algorithm for BRNN-problem. In a set of customers (red
 * points) and facilities (blue points) this algorithms delivers the best places
 * to locate a new facility
 * 
 * The step-by-step algorithm can be seen in the run method which then uses
 * various other methods in this class to determine the solution.
 * 
 * Note: I'd like to make a methodology point here. We sometimes vary between
 * some names. A cell and a face are basically the same and we use those two
 * words as synonyms. Also a cell will be equivalent to a node in the dual graph
 * so when talking about the dual graph, you can basucally think of a node as a
 * cell or vice versa.
 * 
 * @author Ramtin Azimi
 * 
 * 
 */
public class BRNNAlgorithm {

	// *************************************************************************
	// Public variables
	// *************************************************************************

	public VoronoiDiagram voronoi;

	public DualGraph dual;

	public List<Disk> disks;

	/** the final cells which where the facility will be located */
	public ArraySet<Cell> maxCells;

	public PointList customers;

	public PointList facilities;

	/**
	 * The walkthrough of the bfs is being saved in this list in order to
	 * visualize it in the animation
	 */
	public ArrayList<LabelData> datas;

	// *************************************************************************
	// Public variables
	// *************************************************************************

	private DCEL arrangement;

	public void runAlgorithm(PointList customers, PointList facilities) {

		this.customers = customers;
		this.facilities = facilities;

		computeVoronoi();

		computeDisks();

		computeArrangement();

		Set<Cell> dualGraphsStartingCells = createDualGraph();

		// Running the bfs for each connectivity graph/starting cell
		for (Cell c : dualGraphsStartingCells) {
			bfs(c);
		}

		// Calculating the cells with the biggest labels --> those will be the
		// ones where the new facility should be located
		maximumCells();

	}

	/**
	 * Determines in a set of points the closest point to the customer
	 * 
	 * @param customer
	 *            the customer for which the closest blue point is calculated
	 * 
	 * @return closest facility (blue point) to a given customer (red point)
	 */
	public Point locateClosestFacility(Point customer) {

		double x = customer.posX;
		double y = customer.posY;

		appsStandalone.fcvd.geometry.Point p = new appsStandalone.fcvd.geometry.Point(
				x, y);
		if (voronoi != null) {
			// Localizing in which area of the voronoi the point lies
			p = voronoi.pointLocation(p);
		}
		Point facility = new Point((int) p.getX(), (int) p.getY());

		return facility;
	}

	/**
	 * For each red point its disk will be computed.
	 * 
	 * Ths disk is characterized by a center and a radius. The center will be
	 * the red point. The radius will be the distance from the red point to the
	 * closes blue point
	 * 
	 */
	public void computeDisks() {

		List<Disk> diskList = new ArrayList<Disk>();

		for (Point customer : customers.getPoints()) {

			// Calculating closest facility to the customer
			Point facility = locateClosestFacility(customer);
			// Squared distance to the closest facility
			int dist = customer.distanceSquared(facility);

			diskList.add(new Disk(customer, (int) Math.sqrt(dist)));

		}

		disks = diskList;
	}

	/**
	 * Computing the arrangement for the given circles.
	 * 
	 * The arrangement A is a set of faces with corresponding edges and vertices
	 * 
	 */
	public void computeArrangement() {

		SimpleList list = new SimpleList();

		// adding the disks to list in order to sweep them
		for (Disk d : disks) {

			Circle2 disk = new Circle2(new Point2(d.getCenter().posX,
					d.getCenter().posY), d.getRadius());
			list.add(disk);
		}

		// Sweeping the disks
		ArcSweep arc = new ArcSweep();
		arrangement = arc.sweep(list);
		arrangement.generateFaces();

	}

	/**
	 * computing the Voronoi Diagram for the blue points/facilities
	 * 
	 */
	public void computeVoronoi() {

		VoronoiDiagram diagram = new VoronoiDiagram();

		for (Point p : facilities.getPoints()) {

			try {
				diagram.insertPoint(new appsStandalone.fcvd.geometry.Point(p
						.posX, p.posY));
			} catch (VoronoiException e) {
				e.printStackTrace();
			}

		}

		voronoi = diagram;
	}

	/**
	 * The dual point of the outer cell will be computed. Note: Every
	 * connectivity graph has one outer face. All outer cells will be
	 * represented by the SAME SINGLE dual point
	 * 
	 * @return dual point of outer cell
	 * 
	 */
	public Point calculateOuterCellDualPoint() {

		double minCenter = Double.POSITIVE_INFINITY;
		Disk disk = null;

		// Going through all disks and comparing the highest y-coordinate of
		// every disk and taking the minimum
		// The minium - 30 will be the position for the dual point's
		// y-coordinate
		for (Disk d : disks) {

			if (d.getCenter().posY - d.getRadius() < minCenter) {
				minCenter = d.getCenter().posY - d.getRadius();
				disk = d;
			}
		}

		Point center = new Point(disk.getCenter().posX, minCenter - 30, Color.GREEN);
		return center;

	}

	/**
	 * creating the dual graph of the arrangement
	 * 
	 * @param arrangement
	 * @return
	 * 
	 */
	public Set<Cell> createDualGraph() {

		dual = new DualGraph();

		// getting outer faces of arrangement: getOuterFacesOfArrangement().
		// Goal: Finding the outer Face of the arrangement A.
		// Problem: We have different outer faces for different connected
		// components.
		// Fact: First, notice that every connected component has an one and
		// only outer face which is share by all the faces which have a
		// connected edge to the outer face.
		// Approach: Given the arrangement A, we look at all the edges in A
		// Now, we determine the connected component and its outerFace by
		// finding the leftMostEdges and deleting all the Edges which can be
		// reached by this Edge by a walk through. We delete all edges from the
		// edgeList and everything which is left is not part of this connected
		// component. Now, we start doing the same procedure again: Get the
		// leftMostEdge so forth...
		List<DCEL_Face> outerFaces = getOuterFacesOfArrangement();

		ArraySet<Cell> outerCells = new ArraySet<Cell>();

		// We determine one dualPoint for all outer cells.
		// Discretely the outer faces are different though graphically the user
		// thinks the outer face is only one
		Point outerCellDual = calculateOuterCellDualPoint();

		// We go through all edges of the Arrangement
		for (int i = 0; i < arrangement.edgeList.size(); i = i + 1) {

			Cell cellA;
			Cell cellB;

			DCEL_Edge edge = (DCEL_Edge) arrangement.edgeList.get(i);

			// every edge has one left face and one right face.
			// each face/cell will correspond to one node in the dual graph
			// Since the two faces are connected by the edge they will also have
			// an edge connecting them in the dualgraph
			DCEL_Face leftFace = edge.getLeftFace();
			DCEL_Face rightFace = edge.getRightFace();

			// We need to determine whether the face is an outer face or not
			// We have already determined all the outerfaces of the arrangement
			// in outerFaces

			if (edge.reference instanceof Arc2_Sweep) {

				// test whether either the left or the right face are an
				// outerface
				if (outerFaces.contains(leftFace)) {
					// left face is an outerface then assign the dualpoint for
					// the outerface to it
					cellA = new Cell(leftFace, outerCellDual);
					outerCells.add(cellA);
				} else {
					cellA = new Cell(leftFace);
				}
				if (outerFaces.contains(rightFace)) {
					// right face is an outerface then assign the dualpoint for
					// the outerface to it
					cellB = new Cell(rightFace, outerCellDual);
					outerCells.add(cellB);
				} else {
					cellB = new Cell(rightFace);
				}
			}
			// case: when it is a cirlce
			else {

				// if the edge is instance of circle2 it means that the face is
				// a circle
				// this means the left Face got to be an outerface and the right
				// face will be a the inner face
				cellA = new Cell(leftFace, outerCellDual);
				outerCells.add(cellA);
				cellB = new Cell(rightFace);
				i++;
			}

			// adding the cells to the dual graph
			dual.add(cellA);
			dual.add(cellB);
			// connecting the cells with an edge
			dual.add(cellA, cellB);

		}

		datas = new ArrayList<LabelData>();

		return outerCells;
	}

	/**
	 * breadth first search to go through the dual graph in order to compute the
	 * labels of the cells
	 * 
	 * @param root
	 *            The root of every connectivity graph: The root is basically
	 *            the outer cell of every connectivity graph where we start our
	 *            walk through
	 */
	public void bfs(Cell root) {

		List<Cell> visitedNodes = new ArrayList<Cell>();

		// Queue data structure for the bfs
		Queue<Cell> queue = new LinkedList<Cell>();
		queue.add(root);

		// List of visited nodes so we don't visit nodes more than once
		visitedNodes.add(root);

		while (!queue.isEmpty()) {

			Cell node = queue.remove();
			Cell neighbor = null;

			// getting all the neighbors of the node
			ArraySet<Cell> neighbors = (ArraySet<Cell>) dual.adjList.get(node);
			Iterator<Cell> iter = neighbors.iterator();

			// visiting the neighbors of the node
			while (iter.hasNext()) {

				neighbor = iter.next();

				// If the neighbbor has not been visited yet
				if (!(visitedNodes.contains(neighbor))) {

					visitedNodes.add(neighbor);

					// The label of the neighbor will be the node's label +1 or
					// -1 depending on whether we enter a new cirlce or leave a
					// cirlce respectively when walking from the node to the
					// neighboring cell
					neighbor.setLabel(node.getLabel()
							+ neighbor.computeLabel(node));

					// Add the walk through datas for animation purposes
					datas.add(new LabelData(node, neighbor, neighbor
							.computeLabel(node)));

					queue.add(neighbor);

				}
			}

		}

		// updating the labels which had been calculated for the cells
		updateLabels(visitedNodes);
		updateDualGraph();

	}

	/**
	 * Updating the labels of the dual graph with the ones we have just computed
	 * by the walkthrough
	 * 
	 * @param visitedNodes
	 */
	public void updateLabels(List<Cell> visitedNodes) {

		for (Cell c : visitedNodes) {

			for (Cell cell : (Set<Cell>) dual.theNodeSet) {

				if (cell.equals(c)) {

					cell.setLabel(c.getLabel());
				}
			}
		}
	}

	/**
	 * Computing the cells with the maximum labels in the arrangement. Those
	 * will be the cells were the new facility should be placed.
	 * 
	 */
	public void maximumCells() {

		int max = -1;
		// list of cells with cutting area of maximum number of cirlces
		maxCells = new ArraySet<Cell>();

		Set<Cell> dualGraph = (Set<Cell>) dual.theNodeSet;

		// determine the maxmimum number of cirlces which have a cutting area
		// together
		// by going through the labels of the cells and remembering the maximum
		// label which has been seen
		for (Cell c : dualGraph) {

			if (c.getLabel() > max) {

				max = c.getLabel();
			}
		}

		// Once we have computed the maximum number, we go through all the cells
		// again and
		// save those cells whose labels are equal to the maxNumber
		for (Cell c : dualGraph) {

			if (c.getLabel() == max) {
				maxCells.add(c);
			}
		}

	}

	/**
	 * Getting the outer faces of the arrangement Note: Each connectivity graph
	 * has an own outer face
	 * 
	 * @return list of outer faces of arrangement
	 */
	public List<DCEL_Face> getOuterFacesOfArrangement() {

		// Retrieving all edges from the arrangement
		// Equal edges will be deleted automatically deleted by the ArraySet
		ArraySet<DCEL_Edge> edgeList = new ArraySet<DCEL_Edge>();

		for (int i = 0; i < arrangement.edgeList.size(); i++) {

			edgeList.add((DCEL_Edge) arrangement.edgeList.get(i));
		}

		return findingOuterFaces(edgeList);

	}

	/**
	 * Going through the edges.
	 * 
	 * We start at the leftMostEdge and get the outerCell. From the outerCell we
	 * go to neighboring faces, basically walking through all the faces which
	 * are in the same connectiy graph
	 * 
	 * @param edgeList
	 *            The edgeList of the arrangement
	 * 
	 * @return List of faces which are in the same connec
	 */
	public List<DCEL_Face> findingOuterFaces(ArraySet<DCEL_Edge> edgeList) {

		List<DCEL_Face> outerFaces = new ArrayList<DCEL_Face>();

		// First we extract all the cells which are cirlces out of the
		// arrangement
		// These circles will be handled separately.
		List<DCEL_Face> circleFaces = cirlceCase(edgeList);

		// Cells which are circles will be added the outerFaces because the edge
		// of this kind of cell will be connected to the outer cell.
		outerFaces.addAll(circleFaces);

		while (!edgeList.isEmpty()) {

			// We get the edge which is on the farthest left in the arrangement
			DCEL_Edge edge = getLeftMostEdge(edgeList);
			// The left face of this edge has to be an outer face
			DCEL_Face face = edge.getLeftFace();
			// the outer face will be added to our list of outer faces
			outerFaces.add(face);

			// Finding the connected component of the face in the arrangement
			findConnectedComponent(edgeList, face);
		}

		return outerFaces;
	}

	/**
	 * 
	 * Recursive function!
	 * 
	 * Finding the connected component of a face in the edgeList The connected
	 * component are all faces which are connected to the face by an edge or
	 * there is number of edges which can be crossed to reach the other face
	 * 
	 * All edges which are in the same connected component (connectivity graph)
	 * as face will be deleted form the edgeList. Hence the edges which are left
	 * will therefore be the ones which are not connected to the face in any
	 * way.
	 * 
	 * @param edgeList
	 *            the updated edgelist of the arrangement with the edges left
	 *            which have not yet been assigned to a connectivity graph
	 * @param face
	 *            the face we start looking for the connected components
	 */
	public void findConnectedComponent(ArraySet<DCEL_Edge> edgeList,
			DCEL_Face face) {

		for (int i = 0; i < face.edges().size(); i++) {

			// Going through the edges of the face
			DCEL_Edge edge = (DCEL_Edge) face.edges().get(i);

			if (edgeList.contains(edge)) {

				// remove the edge from the edgeList because it is connected to
				// our face
				edgeList.remove(edge);

				// finding the connected edges of the left face
				findConnectedComponent(edgeList, edge.getLeftFace());
				// finding the connected edges of the right face
				findConnectedComponent(edgeList, edge.getRightFace());
			}
		}
	}

	/**
	 * We extract all faces which are just circles in the arrangement and the
	 * edges of this faces will also be deleted out of the edgeList
	 * 
	 * @param edgeList
	 *            set contains all edges of the arrangement
	 * 
	 * @return List of faces part of the arrangement which are just circles.
	 */
	public List<DCEL_Face> cirlceCase(ArraySet<DCEL_Edge> edgeList) {

		List<DCEL_Face> cirlces = new ArrayList<DCEL_Face>();
		for (Iterator<DCEL_Edge> iterator = edgeList.iterator(); iterator
				.hasNext();) {

			DCEL_Edge edge = (DCEL_Edge) iterator.next();

			if (edge.reference instanceof Circle2) {

				DCEL_Face face = (DCEL_Face) edge.getLeftFace();
				cirlces.add(face);
				iterator.remove();

				iterator.next();

				iterator.remove();
			}
		}
		return cirlces;

	}

	/**
	 * Going through the edgeList and getting the left most edge within those
	 * edges. Left most edge is defined as: Which edges has the lowest
	 * x_coordinate value
	 * 
	 * @param edgeList
	 *           edgeList where the leftmost edge will be extracted from
	 * 
	 * @return the left most edge in the given edgelist
	 */
	public DCEL_Edge getLeftMostEdge(ArraySet<DCEL_Edge> edgeList) {

		Point leftMost = new Point(Float.POSITIVE_INFINITY, 0, Color.CYAN);
		DCEL_Edge leftMostEdge = null;
		for (int i = 0; i < edgeList.size(); i++) {

			DCEL_Edge edge = (DCEL_Edge) edgeList.get(i);

			if (edge.reference instanceof Arc2_Sweep) {

				Arc2_Sweep arc = (Arc2_Sweep) edge.reference;

				Point2 x = arc.midPoint();

				Point p = new Point(x.x, x.y);

				if (p.posX < leftMost.posX) {
					leftMost = p;
					leftMostEdge = edge;
				}
			}
		}

		return leftMostEdge;

	}

	/**
	 * Clears algorithm. Deleting all the precomputed objects.
	 * 
	 */
	public void clear() {
		voronoi = null;
		arrangement = null;
		disks = null;
		dual = null;
		customers = null;
		facilities = null;
		maxCells = null;
	}

	public void updateDualGraph() {

		for (Map.Entry<Cell, Set<Cell>> entry : dual.adjList.entrySet()) {

			Set<Cell> tab = entry.getValue();
			for (Cell c : tab) {

				for (Cell cell : dual.theNodeSet) {

					if (c.equals(cell)) {

						c.setLabel(cell.getLabel());

					}
				}
			}
		}
	}
}
