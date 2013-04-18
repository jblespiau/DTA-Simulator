package model;

public class Demand {

	double flow[];
	
	public Demand(double[] flow) {
		this.flow = flow;
	}


	public double getFlow(int step) {
		return flow[step];
	}

}
