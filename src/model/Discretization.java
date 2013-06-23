package model;

/**
 * @class Environment
 * @brief Used now to save global variables. It will at the end be the
 *        encapsulation for the global variable of a single simulation
 * 
 */
public class Discretization {

	private static double delta_t;
	private static int nb_steps = 1;
	private static int nb_cell = 0;
	private static double epsilon = 0.00001;

	public static double getDelta_t() {
		return delta_t;
	}

	public static void setDelta_t(double delta_t) {
		Discretization.delta_t = delta_t;
	}

	static int getNb() {
		nb_cell++;
		return nb_cell;
	}

	public static int getNb_steps() {
		return nb_steps;
	}

	public static void setNb_steps(int nb_steps) {
		Discretization.nb_steps = nb_steps;
	}

	public static boolean equals(double a, double b) {
		return Math.abs(a - b) < epsilon;
	}

	/**
	 * @return an optimistic (a >= b) (if a is nearly greater, it returns true)
	 */
	public static boolean greaterThan(double a, double b) {
		return a + epsilon> b;
	}

	public static double getEpsilon() {
		return epsilon;
	}
}
