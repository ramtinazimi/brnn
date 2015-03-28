package BRNN;

import java.awt.Color;
import java.awt.Graphics;


/**
 * Disk class is characterized by a center and a radius. The center is the
 * customer and the radius is the distance between the customer and the closest
 * facility.
 * 
 * @author Ramtin
 * 
 */
public class Disk {

	private Point center;
	private int radius;
	private static final Color COLOR = Color.RED;

	public Disk(Point center, int radius) {
		this.center = center;
		this.radius = radius;
	}

	public Disk(int posX, int posY, int radius) {
		this(new Point(posX, posY), radius);
	}

	public void draw(Graphics g) {

		g.setColor(COLOR);
		g.drawOval((int) center.posX - radius, (int) center.posY - radius,
				2 * radius, 2 * radius);
	}

	public boolean equals(Object o) {
		if (o instanceof Disk) {

			Disk d = (Disk) o;

			if (center.equals(d)) {
				return true;
			}
		}

		return false;
	}

	public Point getCenter(){
		return center;
	}
	public void setCenter(Point center){
		this.center = center;
	}
	
	public int getRadius(){
		return radius;
	}
	public void setRadius(int radius){
		this.radius = radius;
	}
	public int hashCode() {
		return center.getRadius();
	}

	public String toString() {
		return "Disk centered at " + center.toString() + " with radius "
				+ center.getRadius();
	}

}
