package model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @class Origin
 * @brief The origin contains the numbers of cars wanting to leave for each of
 *        its out-links and for each time steps.
 * 
 */
public class Origin implements Cell {

	public Demand d;
	private ArrayList<EntryCell> L;
	private double[][] cars;
	private int path_nb;

	public Origin() {
		L = new ArrayList<EntryCell>();
		path_nb = 0;
	}

	public void add_link(EntryCell c) {
		L.add(c);
	}

	public String getCell() {
		return "[Origin]";
	}

	@Override
	public String printNetwork(int step) {
		
		if (step == Environment.getNb_steps())
			return "[Origin: ? cars]";
			
		int path = path_nb;
		path_nb = (path_nb + 1) % L.size();
		return "[Origin: " + cars[path][step] + " cars]";
	}

	public Iterator<EntryCell> getIterator() {
		return L.iterator();
	}

	@Override
	public boolean isOrigin() {
		return true;
	}

	@Override
	public boolean isOrdinaryCell() {
		return false;
	}

	@Override
	public boolean isSink() {
		return false;
	}

	@Override
	public void runDynamic(int step) {

		for (int i = 0; i < L.size(); i++) {
			/* We add the flow in the next buffers and run its dynamic */
			L.get(i).transfer(cars[i][step], step);
			L.get(i).runDynamic(step);
		}
	}

	/* An origin has no offer but only a demand for transit */
	@Override
	public double supply(int step) {
		System.out
				.println("A request of offer of transit has been done to an origin.");
		System.exit(-1);
		return 0;
	}

	@Override
	public Cell getNext() {
		System.out
				.println("A request of next cell has been done to an origin.");
		System.exit(-1);
		return new Sink();
	}

	public double[][] getFlow() {
		return cars;
	}

	public void setFlow(double[][] flow) {
		this.cars = flow;
	}

	@Override
	public void transfer(double flow, int step) {
		System.out
				.println("A request of next cell has been done to an origin.");
		System.exit(-1);
	}

	@Override
	public void checkConstraints() {
		// TODO Auto-generated method stub
	}
}
