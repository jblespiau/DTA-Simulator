package model;

import java.util.ArrayList;
import java.util.Iterator;

public class Origin extends Cell {

	public Demand d;
	public double F_out;
	private ArrayList<EntryCell> L;
	private double[][] flows;
	
	public Origin(double f_out) {
		F_out = f_out;
		L = new ArrayList<EntryCell>();
	}
	
	public void add_link(EntryCell c) {
		L.add(c);
	}
	
	public String getCell() {
		return "[Origin]";
	}
	
	@Override
	public String printNetwork(int step) {
		return "[Origin]";
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
			L.get(i).transfer(flows[i][step], step);
			L.get(i).runDynamic(step);
		}
	}

	/* An origin has no offer but only a demand for transit */
	@Override
	public double supply(int step) {
		System.out.println("A request of offer of transit has been done to an origin.");
		System.exit(-1);
		return 0;
	}

	@Override
	public void transfer(double flow, int step) {
		System.out.println("An illegal transfert is done on an origin.");
		System.exit(-1);
	}

	@Override
	public Cell getNext() {
		System.out.println("A request of next cell has been done to an origin.");
		System.exit(-1);
		return new Sink();
	}
	
	public double[][] getFlow() {
		return flows;
	}

	public void setFlow(double[][] flow) {
		this.flows = flow;
	}

	@Override
	public void checkConstraints() {
		// TODO Auto-generated method stub
		
	}	
}
