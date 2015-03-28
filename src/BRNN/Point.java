package BRNN;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Comparator;

public class Point {

	// *************************************************************************
	// Public variables
	// *************************************************************************
	public double posX;
	public double posY;
	public Color color;

	//Default radius is used for mostly everything
	public static int DEFAULT_RADIUS = 4;
	//Big radius is only used for dual point when labels should be visualized
	public static int BIGRADIUS = 10;

	// *************************************************************************
	// Private variables
	// *************************************************************************
	private int radius = DEFAULT_RADIUS;

	public Point(double posX, double posY, Color color, int radius) {
		this.posX = posX;
		this.posY = posY;
		this.color = color;
		this.radius = radius;
	}

	public Point(double posX, double posY, Color color) {

		this(posX, posY, color, DEFAULT_RADIUS);
	}

	public Point() {
		this(0, 0);
	}

	// DEFAULT_COLOR will be red
	public Point(double posX, double posY) {
		this(posX, posY, Color.RED);
	}

//	public void setPosX(double posX) {
//		this.posX = posX;
//	}
//
//	public void setPosY(double posY) {
//		this.posY = posY;
//	}
//
//	public void setColor(Color color) {
//		this.color = color;
//	}
//
//	public double getX() {
//		return posX;
//	}
//
//	public double getY() {
//		return posY;
//	}
//
//	public Color getColor() {
//		return color;
//	}

	public void draw(Graphics g) {

		g.setColor(color);
		if (color == Color.RED) {
			g.fillOval((int) posX - radius, (int) posY - radius, 2 * radius,
					2 * radius);

		} else {
			g.fillRect((int) posX - radius, (int) posY - radius, 2 * radius,
					2 * radius);
		}
	}

	public void draw(Graphics g, Color color) {

		g.setColor(color);
		g.fillOval((int) posX - radius, (int) posY - radius, 2 * radius,
				2 * radius);
		g.setColor(Color.BLACK);
		g.drawOval((int) posX - radius, (int) posY - radius, 2 * radius,
				2 * radius);
	}

	public void drawHighlight(Graphics g) {
		g.setColor(Color.BLACK);
		if (color == Color.RED) {
			g.fillOval((int) posX - radius, (int) posY - radius, 2 * radius,
					2 * radius);
		} else {
			g.fillRect((int) posX - radius, (int) posY - radius, 2 * radius,
					2 * radius);
		}
	}

	public void drawBoundings(Graphics g) {
		g.setColor(Color.BLACK);
		if (color == Color.RED) {
			g.drawOval((int) posX - radius, (int) posY - radius, 2 * radius,
					2 * radius);
		} else {
			g.drawRect((int) posX - radius, (int) posY - radius, 2 * radius,
					2 * radius);
		}
	}

	/**
	 * Determines whether two points collide
	 * 
	 * @param p
	 * 
	 * @return true if they collide, otherwise false
	 */
	public boolean collide(Point p) {
		int abs = distanceSquared(p);

		return (abs <= Math.pow(2 * radius, 2));
	}

	/**
	 * 
	 * Returns the squared distance between two points
	 * 
	 * @param p
	 * 
	 * @return the distance squared
	 */
	
	public int distanceSquared(Point p) {
		double xDiff = this.posX - p.posX;
		double yDiff = this.posY - p.posY;
		int abs = (int) (Math.pow(xDiff, 2) + Math.pow(yDiff, 2));

		return abs;

	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(boolean radiusMode) {

		if (radiusMode) {
			radius = DEFAULT_RADIUS;
		} else {
			radius = BIGRADIUS;
		}
	}

	public boolean equals(Object obj) {

		if (obj instanceof Point) {
			Point p = (Point) obj;
			double xDiff = Math.abs(posX - p.posX);
			double yDiff = Math.abs(posY - p.posY);

			if (xDiff < radius && yDiff < radius) {
				return true;
			}
		}

		return false;
	}

	public int hashCode() {
		return (int) posX;
	}

	public String toString() {
		return "(X,Y) = (" + posX + "," + posY + ")";
	}

	/**
	 * Comparae points by their y-Coordinates
	 */
	public static Comparator<Point> COMPARE_BY_YCoord = new Comparator<Point>() {
		public int compare(Point first, Point second) {
			return (int) (first.posY - second.posY);
		}
	};

	/**
	 * Compare points by their x-Coordinates
	 */
	public static Comparator<Point> COMPARE_BY_XCoord = new Comparator<Point>() {
		public int compare(Point first, Point second) {
			return (int) (first.posX - second.posX);
		}
	};
}
