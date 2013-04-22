package model.demandFactory;

/**
 * @class Point
 * @brief Represent a point (x, y) on a graph
 * 
 * The previous and next point in the graph can be saved in the corresponding
 * attributes.
 *
 */
public class Point {
	
	private double x;
	private double y;
	Point previous, next;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
		previous = null;
		next = null;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
