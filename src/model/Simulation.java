package model;

/**
 * @brief Every components of a simulation has to be registered in a simulation
 *        in order to work
 * 
 */
public class Simulation {

	private Discretization time;
	private Origin origin;

	public Simulation(Discretization time) {
		this.time = time;
		origin = new Origin();
	}

	public void setNumber_steps (int step) {
		time.setNb_steps(step);
	}
	
	public void setDelta_t (int delta_t) {
		time.setDelta_t(delta_t);
	}
}
