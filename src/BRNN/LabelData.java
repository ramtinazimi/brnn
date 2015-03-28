package BRNN;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import anja.geom.Circle2;
import anja.util.GraphicsContext;
import appsSwingGui.topoVoro.Arc2_Sweep;
import appsSwingGui.topoVoro.dcel.DCEL_Edge;

public class LabelData {

	// *************************************************************************
	// Private variables
	// *************************************************************************

	// The cell from which the label starts from
	private Cell origin;
	// The cell to which the label walks to
	private Cell target;

	// current position of the label
	private double xPos;
	private double yPos;

	// connecting edge between the two cells (origin and target)
	private DCEL_Edge connectingEdge;

	private float radius;

	private double sign;

	// If Animation is active it is true, else false
	private boolean active;

	private int labelSign;

	// *************************************************************************
	// Public variables
	// *************************************************************************
	public static int animationSpeed = 2;
	public BRNNGUI gui;

	public LabelData(Cell origin, Cell target, int labelSign) {

		this.origin = origin;
		this.target = target;
		this.labelSign = labelSign;
		connectingEdge = origin.getConnectingEdge(target);
		if (connectingEdge.reference instanceof Arc2_Sweep) {
			Arc2_Sweep arc = (Arc2_Sweep) origin.getConnectingEdge(target).reference;
			this.radius = arc.radius;
		} else {
			Circle2 circle = (Circle2) origin.getConnectingEdge(target).reference;
			this.radius = circle.radius;
		}
		sign = Math.signum(origin.dualPoint.distanceSquared(target.dualPoint)
				- radius * radius);
	}

	public void highlightCrossedArc(Graphics2D g) {

		// Drawing the arc which is being crossed
		Stroke oldStroke = g.getStroke();
		GraphicsContext gc = new GraphicsContext();
		gc.setForegroundColor(Color.YELLOW);
		gc.setStroke(new BasicStroke(6));
		if (active) {

			if (connectingEdge.reference instanceof Arc2_Sweep) {
				Arc2_Sweep arc = (Arc2_Sweep) connectingEdge.reference;
				arc.draw(g, gc);
			} else {
				Circle2 circle = (Circle2) connectingEdge.reference;
				circle.draw(g, gc);
			}

		}

		g.setStroke(oldStroke);
	}

	public void draw(Graphics2D g) {

		highlightCrossedArc(g);
		String string = null;
		g.setColor(Color.BLACK);

		Point currentPos = new Point(xPos, yPos);
		double newSign = Math.signum(currentPos
				.distanceSquared(target.dualPoint) - radius * radius);

		if (Math.sqrt(currentPos.distanceSquared(target.dualPoint)) <= animationSpeed) {
			g.setColor(Color.WHITE);
			string = Integer.toString(target.getLabel());
		} else if (newSign == sign) {
			string = Integer.toString(origin.getLabel());
			g.setColor(Color.WHITE);
			g.fillOval((int) xPos - 10, (int) yPos - 10, 2 * 10, 2 * 10);
		} else {
			string = Integer.toString(target.getLabel());
			g.setColor(Color.WHITE);
			// changed from plus to minus
			if (labelSign > 0) {
				// g.setColor(Color.GREEN);
			} else if (labelSign < 0) {
				// g.setColor(Color.RED);
			}

		}

		g.fillOval((int) xPos - 10, (int) yPos - 10, 2 * 10, 2 * 10);

		// Setting the label number in the cirlce
		g.setColor(Color.BLACK);
		g.drawOval((int) xPos - 10, (int) yPos - 10, 2 * 10, 2 * 10);
		int stringWidth = (int) Math.floor(g.getFontMetrics()
				.getStringBounds(string, g).getWidth());
		int height = 20;
		g.drawString(string, (int) (xPos - stringWidth / 2),
				(int) (yPos + height / 4));

	}

	public class BFSLabelRotationThread extends Thread {

		public void run() {

			xPos = (int) origin.dualPoint.posX;
			yPos = (int) origin.dualPoint.posY;

			active = true;

			while (true) {

				if (!gui.getAnimationState()) {
					break;
				}
				
				//Vectors pointed in the direction of movement
				double vectorX = target.dualPoint.posX - xPos;
				double vectorY = target.dualPoint.posY - yPos;

				double vectorLength = Math.sqrt(vectorX * vectorX + vectorY
						* vectorY);

				//new xPosition and yPosition of label
				xPos += animationSpeed * vectorX / vectorLength;
				yPos += animationSpeed * vectorY / vectorLength;

				gui.repaint();

				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (vectorLength < 2 * animationSpeed) {
					break;
				}

			}
			gui.repaint();
			active = false;

		}
	}

	public BFSLabelRotationThread startAnimation() {
		BFSLabelRotationThread ani = new BFSLabelRotationThread();
		ani.start();

		return ani;
	}
}
