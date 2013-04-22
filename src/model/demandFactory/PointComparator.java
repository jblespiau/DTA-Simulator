package model.demandFactory;

import java.util.Comparator;


/**
 * @class PointComparator
 * @brief Compare two point according to their abscissa
 */
public class PointComparator implements Comparator<Point> {

	@Override
	public int compare(Point a, Point b) {
		
		double diff = a.getX() - b.getX();
		if ( diff < 0.)
			return -1;
		else if (diff == 0)
			return 0;
		else return +1;
	}

}
