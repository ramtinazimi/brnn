package BRNN;

import java.awt.Graphics2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Straightforward undirected graph implementation. 
 * 
 * @author Ramtin Azimi
 * 
 *         Created Winter term 2014/15. For use in BRNN code.
 * 
 */

public class DualGraph {

	//Adjacency List: Node(Cell) --> Neighbors
	Map<Cell, Set<Cell>> adjList = new HashMap<Cell, Set<Cell>>();

	//List of nodes
	Set<Cell> theNodeSet = Collections.unmodifiableSet(adjList.keySet());

	/**
	 * Add a node. If node is already in graph then no change.
	 * 
	 * @param node
	 *            the node to add
	 */
	public void add(Cell node) {

		if (theNodeSet.contains(node)) {
			return;
		}

		adjList.put(node, new ArraySet<Cell>());
		// theNodeSet.add(node);
	}

	/**
	 * Add a link. If the link is already in graph then no change.
	 * 
	 * @param nodeA
	 *            one end of the link
	 * @param nodeB
	 *            the other end of the link
	 * @throws NullPointerException
	 *             if either endpoint is not in graph
	 */
	public void add(Cell nodeA, Cell nodeB) {

		adjList.get(nodeA).add(nodeB);
		adjList.get(nodeB).add(nodeA);

	}

	/**
	 * Remove node and any links that use node. If node not in graph, nothing
	 * happens.
	 * 
	 * @param node
	 *            the node to remove.
	 */
	public void remove(Cell node) {
		if (!adjList.containsKey(node))
			return;
		for (Cell neighbor : adjList.get(node))
			adjList.get(neighbor).remove(node); // Remove "to" links
		adjList.get(node).clear(); // Remove "from" links
		adjList.remove(node); // Remove the node
		theNodeSet.remove(node);
	}

	/**
	 * Remove the specified link. If link not in graph, nothing happens.
	 * 
	 * @param nodeA
	 *            one end of the link
	 * @param nodeB
	 *            the other end of the link
	 * @throws NullPointerException
	 *             if either endpoint is not in graph
	 */
	public void remove(Cell nodeA, Cell nodeB) throws NullPointerException {

		adjList.get(nodeA).remove(nodeB);
		adjList.get(nodeB).remove(nodeA);
	}

	
	/**
	 * Returns an unmodifiable Set view of the nodes contained in this graph.
	 * The set is backed by the graph, so changes to the graph are reflected in
	 * the set.
	 * 
	 * @return a Set view of the graph's node set
	 */
	public Set<Cell> nodeList() {
		return theNodeSet;
	}

	/**
	 * drawing the dual graph
	 * 
	 * @param g
	 * @param showLabels
	 */
	public void draw(Graphics2D g, boolean showLabels) {

		for (Map.Entry<Cell, Set<Cell>> entry : adjList.entrySet()) {

			Cell c = entry.getKey();
			Set<Cell> set = entry.getValue();
			Iterator<Cell> iter = set.iterator();

			while (iter.hasNext()) {
				Cell neighbor = (Cell) iter.next();

				g.drawLine((int) c.dualPoint.posX, (int) c.dualPoint.posY,
						(int) neighbor.dualPoint.posX,
						(int) neighbor.dualPoint.posY);

				c.drawDual(g, showLabels);
				neighbor.drawDual(g, showLabels);
				
			}
		}
	}

}