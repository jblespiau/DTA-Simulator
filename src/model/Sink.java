package model;

public class Sink extends Cell {

	public String getCell() {
		return "[Sink]";
	}
	
	@Override
	public String printNetwork(int step) {
		return "[Sink]";
	}

	@Override
	public boolean isOrigin() {
		return false;
	}

	@Override
	public boolean isOrdinaryCell() {
		return false;
	}

	@Override
	public boolean isSink() {
		return true;
	}

	/* You have nothing to do with sinks */
	@Override
	public void runDynamic(int step) {

	}

	/* A sink just accept all the flow */
	@Override
	public double supply(int step) {
		return Double.MAX_VALUE;
	}

	/* A sink just accept all the flow */
	@Override
	public void transfer(double flow, int step) {
	}

	@Override
	public Cell getNext() {
		return null;
	}

	@Override
	public void checkConstraints() {
	}
}
