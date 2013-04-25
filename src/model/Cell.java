package model;

/**
 * @class Cell
 * @brief All the cells composing the networks have to implement some mandatory
 *        functions
 */
public interface Cell {
	/*
	 * _______ _______ / \ / ---> \ / \ Demand Supply
	 */

	abstract public boolean isOrigin();

	abstract public boolean isOrdinaryCell();

	abstract public boolean isSink();

	abstract public void setNext(Cell next);
	abstract public Cell getNext();

	abstract public String printNetwork(int step);

	/**
	 * @param step
	 * @return the supply of the cell at time @step
	 */
	abstract public double supply(int step);

	/**
	 * @brief Set the in-flow at time step to flow
	 * @param flow
	 * @param step
	 */
	abstract public void transfer(double flow, int step);

	/**
	 * @brief Compute the dynamic at time step (in and out-flows at time step
	 *        and the density at time (step + 1)
	 * @param step
	 */
	abstract public void runDynamic(int step);

	/**
	 * @brief Checks the Courant–Friedrichs–Lewy conditions to be sure the
	 *        discretization is ok
	 */
	abstract void checkConstraints();
}