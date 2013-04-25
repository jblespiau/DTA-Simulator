package model;

import java.util.ArrayList;
import java.util.Iterator;

import model.demandFactory.Demand;

/**
 * @class Origin
 * @brief The origin contains the numbers of cars wanting to leave for each of
 *        its out-links and for each time steps.
 * 
 */
public class Origin {

	public Demand d;
	private ArrayList<EntryCell> L;
	private double[][] cars_leaving;
	private int path_nb;

	public Origin() {
		L = new ArrayList<EntryCell>();
		path_nb = 0;
	}

	public void add_link(EntryCell c) {
		L.add(c);
	}

	public int getNbRoads() {
		return L.size();
	}

	public String getCell() {
		return "[Origin]";
	}

	public String printNetwork(int step) {

		if (step == Discretization.getNb_steps())
			return "[Origin: ? cars]";

		int path = path_nb;
		path_nb = (path_nb + 1) % L.size();
		return "[Origin: " + cars_leaving[path][step] + " cars]";
	}

	public Iterator<EntryCell> getIterator() {
		return L.iterator();
	}

	public void runDynamic(int step) {

		for (int i = 0; i < L.size(); i++) {
			/* We add the flow in the next buffers and run its dynamic */
			L.get(i).transfer(cars_leaving[i][step], step);
			L.get(i).runDynamic(step);
		}
	}

	public double[][] getFlow() {
		return cars_leaving;
	}

	public void setFlow(double[][] flow) {
		this.cars_leaving = flow;
	}
}
