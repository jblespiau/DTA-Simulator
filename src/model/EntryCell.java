package model;

public class EntryCell extends Cell {

	public OrdinaryCell c;
	private double[] buffer = new double[Environment.getNb_steps() + 1];

	public EntryCell(OrdinaryCell c) {
		this.c = c;
	}
	
	public EntryCell(OrdinaryCell c, double buffer) {
		this.c = c;
		this.buffer[0] = buffer;
	}
	
	@Override
	public boolean isOrigin() {
		return false;
	}

	@Override
	public boolean isOrdinaryCell() {
		return true;
	}

	@Override
	public boolean isSink() {
		return false;
	}

	@Override
	public String getCell() {
		return "[buffer: " + buffer[0] + "]" + c.getCell();
	}
	
	@Override
	public String printNetwork(int step) {
		return "[buffer: " + buffer[step] + "]" + c.printNetwork(step);
	}

	@Override
	public double supply(int step) {
		System.out.println("A request of offer of transit has been done to an EntryCell");
		System.exit(-1);
		return 0;
	}

	// At time t we add the flow in buffer[t] than will then be emptied
	@Override
	public void transfer(double flow, int step) {
		buffer[step] += flow;
	}

	/* In the case of an EntryCell, we just have to compute the flow_in */
	@Override
	public void runDynamic(int step) {
		double demand = buffer[step];
		assert demand >= 0 : "the demand should be positive";
		/* We ask for the supply of the outgoing link */
		double supply = c.supply(step);
		
		assert supply >= 0 : "the supply should be positive";

		/* We set the incoming flow */
		double out_flow = Math.min(supply, demand);
		c.transfer(out_flow, step);
		
		buffer[step+1] = buffer[step] - out_flow;
		assert buffer[step + 1] >= 0 : "the buffer should be positive";
		
		/* We run its dynamic */
		c.runDynamic(step);
	}

	@Override
	public Cell getNext() {
		return c.getNext();
	}

	@Override
	public void checkConstraints() {
		assert buffer[0] >= 0 : "the initial buffer size should be greater than 0";
		c.checkConstraints();		
	}
}
