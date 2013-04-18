package model;

public class OrdinaryCell extends Cell {

	/* Constants */
	public double l, v, w, F_in, F_out, jam_density;
	public Cell next;
	public int identifier;

	/* Variables */
	public double[] flow_in = new double[Environment.getNb_steps() + 1];
	public double[] flow_out = new double[Environment.getNb_steps() + 1];
	public double[] density = new double[Environment.getNb_steps() + 1];
	private double supply_change;
	private double demande_change;

	public OrdinaryCell(double l, double v, double w, double f_in,
			double f_out, double jam_capacity, double density_init) {
		this.l = l;
		this.v = v;
		this.w = w;
		F_in = f_in;
		F_out = f_out;
		this.jam_density = jam_capacity;
		density[0] = density_init;
		for (int i = 0; i < Environment.getNb_steps() + 1; i++) {
			flow_in[i] = -1;
			flow_out[i] = -1;
		}
		supply_change = -F_in / w + jam_density;
		demande_change = F_out / v;
		identifier = Environment.getNb();
	}

	@Override
	public String toString() {
		return "Cell: " + identifier + "\n" + "F_in=" + F_in + "\n" + "F_out="
				+ F_out + "\n" + "v=" + v + "\n" + "w=" + w + "\n"
				+ "jam_density=" + jam_density + "\n" +"\n"
				+ "supply_change=" + supply_change + "\n" + "demande_change=" + demande_change;
	}

	public boolean isCongested(int step) {
		return density[step] > supply_change;
	}

	public Cell getNext() {
		return next;
	}

	public void setNext(Cell next) {
		this.next = next;
	}

	public String getCell() {
		return "[" + (flow_in[0] < 0 ? "?" : flow_in[0]) + " (max: " + F_in
				+ ")>(" + l + ", " + v + ", " + w + ", " + jam_density + ")"
				+ (isCongested(0) ? "J" : "") + "|"
				+ (flow_out[0] < 0 ? "?" : flow_out[0]) + "(max: " + F_out
				+ ")]";
	}

	@Override
	public String printNetwork(int step) {
		return "[" + (flow_in[step] < 0 ? "?" : flow_in[step]) + " (max:" + F_in + ")|"
				+ density[step] + "(/" + jam_density + ")"
				+ (isCongested(step) ? "J" : "") + "|"
				+ (flow_out[step] < 0 ? "?" : flow_out[step]) + "(max:" + F_out + ")]";
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
	public void runDynamic(int step) {
		/* If we run this function, flow_in[step] is already correct */
		assert flow_in[step] != -1;
		
		double t = Environment.getDelta_t();
		double demand = Math.max(0,
				Math.min(F_out, v * density[step]));
		assert demand >= 0 : "the demand should be positive";
		/* We ask for the supply of the outgoing link */
		double supply = next.supply(step);
		assert supply >= 0 : "the supply should be positive";

		/* Modify the density and move the flow accordingly */
		double out_flow = Math.min(supply, demand);
		flow_out[step] = out_flow;
		next.transfer(out_flow, step);
		
		/* We update the density if the cell */
		assert density[step] - t / l * out_flow >= 0; // Check the out_flow is possible
		density[step+1] = density[step] + t / l * (flow_in[step] - out_flow);
		
		assert density[step+1] >= 0 : "The density should be positive";
		assert density[step+1] < jam_density : "The density exceed the jam_density.";
		
		/* We run its dynamic */
		next.runDynamic(step);
	}

	@Override
	public double supply(int step) {
		return Math.max(0, Math.min(F_in, w * (jam_density - density[step]) ));
	}

	@Override
	public void transfer(double flow, int step) {
		//double t = Environment.getDelta_t();
		flow_in[step] = flow;
		//density[step+1] = density[step+1] + t / l * flow;
		//assert density[step+1] < jam_density : "The density exceed the jam_density.";
	}

	@Override
	public void checkConstraints() {
		
		assert density[0] >= 0 : "Cell " + identifier + "the initial density must be greater than 0" + density[0];
		//System.out.println(toString());
		double delta_t = Environment.getDelta_t();
		assert v <= l / delta_t : "Cell " + identifier + ": CLF condition " + v
				+ " <= " + l + " / " + delta_t + " not respected";
		assert w <= l / delta_t : "Cell " + identifier + ": CLF condition " + w
				+ " <= " + l + " / " + delta_t + " not respected";
		assert F_in < w * jam_density : "Cell " + identifier
				+ ": We should have F_out < w * jam_density";

		if (!next.isSink())
			assert F_in == F_out : "Cell "
					+ identifier
					+ ": F_in and F_out should be the same except for the bottleneck";

		assert  demande_change <= supply_change : "Cell "
				+ identifier
				+ ": " + demande_change
				+ "<="
				+ supply_change
				+ ": The density of free-flow should be smaller than the density of jammed flow.";
		next.checkConstraints();
		//System.out.println("OK");
	}
}
