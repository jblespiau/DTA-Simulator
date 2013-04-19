package model;

import java.util.Comparator;

public class PointComparator implements Comparator<Point> {

	@Override
	public int compare(Point a, Point b) {
		
		double diff = a.time - b.time;
		if ( diff < 0.)
			return -1;
		else if (diff == 0)
			return 0;
		else return +1;
	}

}
