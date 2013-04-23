package model;

/**
 * @class EntryCell
 * @brief An EntryCell contains a buffer and an OrdinaryCell. The buffer holds
 *        the cars wanting to go to the cell but which can't because of the
 *        limited in-flow
 * 
 */
public class EntryCell implements Cell, PlottableCell {

	public RoadChunk c;
	double[] cars_demand = new double[Discretization.getNb_steps()];
	double[] cars_out = new double[Discretization.getNb_steps()];
	double[] buffer = new double[Discretization.getNb_steps() + 1];

	public EntryCell(RoadChunk c) {
		this.c = c;
	}

	public EntryCell(RoadChunk c, double buffer) {
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
	public String printNetwork(int step) {
		return "[buffer: " + buffer[step] + " cars]" + c.printNetwork(step);
	}

	@Override
	public double supply(int step) {
		System.out
				.println("A request of offer of transit has been done to an EntryCell");
		System.exit(-1);
		return 0;
	}

	// We take into account the demand and this will be used in the Dynamic
	@Override
	public void transfer(double nb_cars, int step) {
		cars_demand[step] = nb_cars;
	}

	/*
	 * The buffer[step] must be completely independent of every values
	 * calculated after step
	 */
	@Override
	public void runDynamic(int step) {
		double delta_t = Discretization.getDelta_t();
		/*
		 * cars_demand[step] is what is at the buffer at time step while
		 * cars_out where the cars that left at the givens step
		 */
		if (step == 0)
			buffer[step] = cars_demand[step];
		else
			buffer[step] = buffer[step - 1] + cars_demand[step]
					- cars_out[step - 1];

		double demand = buffer[step] / delta_t;
		assert demand >= 0 : "the demand should be positive";
		/* We ask for the supply of the outgoing link */
		double supply = c.supply(step);

		assert supply >= 0 : "the supply should be positive";

		/* We set the incoming flow */
		double out_flow = Math.min(supply, demand);
		c.transfer(out_flow, step);

		cars_out[step] = out_flow * delta_t;
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

	@Override
	public double[] cumulativeDensity(int to_step) {
		return c.cumulativeDensity(to_step);
	}
}
