package model;

public class Point {
	
	double time;
	double flow;
	Point previous, next;
	
	public Point(double time, double flow) {
		this.time = time;
		this.flow = flow;
		previous = null;
		next = null;
	}

}
