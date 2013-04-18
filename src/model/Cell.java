package model;

public abstract class Cell {

	abstract public boolean isOrigin();
	abstract public boolean isOrdinaryCell();
	abstract public boolean isSink();
	
	abstract public Cell getNext();
	abstract public String getCell();
	abstract public String printNetwork(int step);
	
	/*    _______          _______
  	 *   /                        \
	 *  /             --->         \
	 * /                            \
	 *   Demand              Supply
	 */
	abstract public void checkConstraints();
	abstract public double supply(int step);
	abstract public void transfer(double flow, int step);
	abstract public void runDynamic(int step);
	
}
