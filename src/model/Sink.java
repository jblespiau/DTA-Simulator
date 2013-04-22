package model;

import graphics.Plots;


/**
 * @class Sink
 * @brief Represent a Sink with no bottleneck (supply is infinite)
 */
public class Sink implements Cell {

	protected double[] cumulative_cars;

	public Sink() {
		cumulative_cars = new double[Discretization.getNb_steps() + 1];
	}

	@Override
	public String printNetwork(int step) {
		return "[Sink: CC:" + cumulative_cars[step] + "]";
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

	/**
	 * @brief Does nothing
	 */
	@Override
	public void runDynamic(int step) {
	}

	/**
	 * @return Infinite supply
	 */
	@Override
	public double supply(int step) {
		return Double.MAX_VALUE;
	}

	@Override
	public void transfer(double flow, int step) {
		double delta_t = Discretization.getDelta_t();
		cumulative_cars[step + 1] = cumulative_cars[step] + flow * delta_t;
	}

	/**
	 * @return null since there is no following cell
	 */
	@Override
	public Cell getNext() {
		return null;
	}

	/**
	 * @brief Does nothing since there is no constraints on sinks
	 */
	@Override
	public void checkConstraints() {
	}

	void setCumulative_cars(double cumulative_cars, int step) {
		this.cumulative_cars[step] = cumulative_cars;
	}
	
	double getCumulative_cars(int step) {
		return cumulative_cars[step];
	}
	
	void plot (String name, String x, String y) {
		Plots.plotLineChart(cumulative_cars, name, x, y);
	}
}
