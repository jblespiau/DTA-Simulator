package model;

public class Environment {

	private static double delta_t;
	private static int nb_steps = 1;
	private static int nb_cell = 0;
	
	public static double getDelta_t() {
		return delta_t;
	}

	public static void setDelta_t(double delta_t) {
		Environment.delta_t = delta_t;
	}
	
	static int getNb() {
		nb_cell++;
		return nb_cell;
	}

	public static int getNb_steps() {
		return nb_steps;
	}

	public static void setNb_steps(int nb_steps) {
		Environment.nb_steps = nb_steps;
	}
}
