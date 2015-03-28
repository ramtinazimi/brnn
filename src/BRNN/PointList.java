package BRNN;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PointList {

	private Color pointColor;
	List<Point> points;

	public PointList(Color pointColor, List<Point> points) {
		this.points = points;
		this.pointColor = pointColor;
		setPointsColor(pointColor);		
	}
	
	public PointList(List<Point> points){
		this(Color.RED, points);
	}

	public PointList(Color pointColor) {
		this(pointColor, new ArrayList<Point>());
	}
	
	public PointList(){
		this(Color.BLACK);
	}

	public void setList(List<Point> points){
		this.points = points;
	}
	
	public void setPointsColor(Color color){
		
		for(Point p: points){
			p.color = color;
		}
	}
	
	public boolean collisionExists(Point p){
		
		for (Point point : points) {
			if (point.collide(p)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void addPoint(Point p) {

		p.color = pointColor;
		points.add(p);

	}

	public void setPointList() {

	}


	public void remove(Point p){
		points.remove(p);
	}
	
	public void clear(){
		points.clear();
	}
	
	public List<Point> getPoints(){
		return points;
	}
	
	public void draw(Graphics g){
		
		setPointsColor(pointColor);
		for(Point p: points){
			p.draw(g);
		}
	}
	
	public int getSize(){
		return points.size();
	}
	
	public boolean contains(Point p){
		return points.contains(p);
	}
	
	/**
	 * Search point in the pointList
	 * @param p
	 * @return point if the point has been found otherwise null
	 */
	public Point search(Point p){
		
		for(Point point: points){
			if(point.equals(p)){
				
				return point; 
			}
		}
		
		return null;
	}
	
	public void removeAll(Point p){
		
		for (Iterator<Point> iterator = points.iterator(); iterator.hasNext();) {
		    Point point = iterator.next();
		    if (point.equals(p)) {
		        // Remove the current element from the iterator and the list.
		        iterator.remove();
		    }
		}
	}
	
	public boolean equals(Object o){
		
		if(o instanceof PointList){
			
			PointList pointsList = (PointList)o;
			
			for(Point p: pointsList.getPoints()){
				
				if(!points.contains(p)){
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	public int hasCode(){
		return points.size();
	}

	
	public String toString(){
		return points.toString();
	} 
}
