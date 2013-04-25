package model;

import graphics.Plots;

import java.util.Iterator;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import model.demandFactory.Point;

/**
 * @class Distributor
 * @brief Replace an origin which has fixed out-flows by splitting the total
 *        demand between the paths
 * 
 * @details Don't forget to build() the distributor when you have ended your
 *          network
 * 
 */
public class Distributor {

	private double[] total_demand;
	private Origin O;
	private int nb_out_links;
	private double[][] split_ratio;
	private double[][] leaving_cars;
	private double[][] cumulative_left_cars;
	private double[] initial_cars_on_road;
	private EntryCell[] entries;
	private Sink[] sinks;
	private boolean is_built;

	public Distributor(double[] d, Origin O) {
		this.total_demand = d;
		this.O = O;
		is_built = false;
	}

	/**
	 * @brief Build a distributor from an origin of a given network. It is
	 *        mandatory to call it after having set all the paths. A distributor
	 *        works only on the network it was build for
	 * 
	 * @details This fixes numerous parameters (number of roads ...) according
	 *          to the state of the network.
	 */
	public void build() {
		is_built = true;
		int nb_steps = Discretization.getNb_steps();
		nb_out_links = O.getNbRoads();
		split_ratio = new double[nb_out_links][nb_steps];
		leaving_cars = new double[nb_out_links][nb_steps];
		cumulative_left_cars = new double[nb_out_links][nb_steps];

		entries = new EntryCell[nb_out_links];
		sinks = new Sink[nb_out_links];
		initial_cars_on_road = new double[nb_out_links];

		/*
		 * We compute the initial numbers of cars in the network and remove this
		 * number to the number of cars which arrives at the sinks
		 */
		Iterator<EntryCell> it = O.getIterator();
		int i = 0;
		while (it.hasNext()) {
			EntryCell ec = it.next();
			initial_cars_on_road[i] = Solver.countInitialCars(ec);
			entries[i] = ec;
			sinks[i] = ec.c.getSink();
			sinks[i].setCumulative_cars(-initial_cars_on_road[i], 0);
			i++;
		}
	}

	/**
	 * @brief Run the dynamic at the given step: this means that the flows at
	 *        this step and the density at (step + 1) are computed. The
	 *        cumulative_left_cars also
	 * @details The leaving cars must have been computed but not
	 *          cumulative_left_cars
	 * @param step
	 */
	private void runDynamic(int step) {
		for (int i = 0; i < nb_out_links; i++) {
			double nb_cars = leaving_cars[i][step];
			/* We compute the number of cars leaving */
			if (step == 0)
				cumulative_left_cars[i][step] = nb_cars;
			else
				cumulative_left_cars[i][step] = cumulative_left_cars[i][step - 1]
						+ nb_cars;
			/* We add the cars in the next buffers and run its dynamic */
			entries[i].transfer(nb_cars, step);
			entries[i].runDynamic(step);
		}
	}

	/**
	 * @brief Computes the average travel time of the cars wanting to leave at
	 *        the given step to a given cell considering that enough of the
	 *        future has been computed (states of sinks, cumulative_left_cars)
	 */
	public double computeLinkTT(int cell, int step) {
		double delta_t = Discretization.getDelta_t();

		Point A = new Point(step, cumulative_left_cars[cell][step]);
		// System.out.println("Cumulative left cars at step " + step + " :"
		// + cumulative_left_cars[cell][step]);
		Point B;
		if (step == 0)
			B = new Point(step, 0);
		else
			B = new Point(step, cumulative_left_cars[cell][step - 1]);

		int arrival_previous_step;
		int arrival_this_step;
		int t = step;

		while (B.getY() > sinks[cell].getCumulative_cars(t)) {
			t++;
		}
		arrival_previous_step = t;

		// Point C = new Point(arrival_previous_step, B.getY());

		/*
		 * We find the time at which the sink has accepted the amount of left
		 * cars
		 */
		while (!Discretization.greaterThan(sinks[cell].getCumulative_cars(t),
				A.getY())) {
			t++;
		}
		arrival_this_step = t;

		/*
		 * System.out.println("Cumulative leaving cars: (step-1)" + B.getY() +
		 * " (step)" + A.getY()); System.out.println("Sink cars: (step):" +
		 * B.getY() + " last_arrived " + arrival_this_step + ":" +
		 * sinks[cell].getCumulative_cars(arrival_this_step) +
		 * " & (last_arrived - 1):" +
		 * sinks[cell].getCumulative_cars(arrival_this_step - 1));
		 */

		// Point D = new Point(arrival_this_step, A.getY());
		// TODO: manage a demand equals to zero...
		double cars_demand = leaving_cars[cell][step];
		if (cars_demand == 0)
			System.out.println("0 leaving cars in computeLink");
		/*
		 * If the number of cars leaving is equals to zero, we can't compute the
		 * TT. We force a positive inf-low to be able to get the TT TODO: find a
		 * good cars_demand in order to get the real TT
		 */

		double weighted_travel_time = cars_demand * (arrival_this_step - step);
		double exceeded_time = 0;
		for (int tmp = arrival_previous_step; tmp < arrival_this_step; tmp++)
			exceeded_time += sinks[cell].getCumulative_cars(tmp) - B.getY();

		double average_travel_time = (weighted_travel_time - exceeded_time)
				/ cars_demand; // * * delta_t

		assert average_travel_time >= 0 : "The Average Travel Time should be positive";

		return average_travel_time;
	}

	/**
	 * @brief Computes all the Average Travel Time for each pools of cars that
	 *        left at the given step
	 */
	public double[] computeLinksTT(int step) {

		double[] result = new double[nb_out_links];

		/*
		 * We compute the number of cars leaving.
		 */
		boolean[] demand_was_zero = new boolean[nb_out_links];
		for (int i = 0; i < nb_out_links; i++)
			demand_was_zero[i] = false;

		for (int i = 0; i < nb_out_links; i++) {
			double leaving = split_ratio[i][step] * total_demand[step];
			if (Discretization.equals(leaving, 0)) {
				// TODO: find a good cars_demand in order to get the real TT
				demand_was_zero[i] = true;
				System.out
						.println("No leaving cars. We input a small arbitrary inflow");
				leaving = 1;
			}
			if (split_ratio[i][step] == 0)
				if (!demand_was_zero[i])
					System.out.println("Bug à discretization.equals");

			leaving_cars[i][step] = leaving;

			/*
			 * Moreover we put some cars after that step to make sure the sink
			 * will be filled up
			 */
			if (step + 1 < Discretization.getNb_steps())
				leaving_cars[i][step + 1] = 1;
		}

		/*
		 * Compute the needed steps in order that all the cars that wanted to
		 * enter at time step has effectively arrived at their corresponding
		 * sink
		 */
		int time = step; // The current step which is computed
		boolean all_flows_exited;
		do {
			/*
			 * We run the dynamic (compute the flows at step $time, and the
			 * density at step ($time + 1)
			 */
			assert time < Discretization.getNb_steps() - 1 : "The Travel Time can't be computed because there "
					+ "lacks steps to forward simulate";
			runDynamic(time);

			/* We check if everyone has arrived to its destination */
			all_flows_exited = true;
			for (int i = 0; i < nb_out_links; i++) {

				all_flows_exited = all_flows_exited
						&& (Discretization.greaterThan(
								sinks[i].getCumulative_cars(time + 1),
								cumulative_left_cars[i][step]));
			}
			time++;
		} while (!all_flows_exited);

		System.out.println("Forward simulation needed: " + time);
		for (int i = 0; i < nb_out_links; i++) {
			result[i] = computeLinkTT(i, step);
		}

		// TODO: check this is without consequences
		for (int i = 0; i < nb_out_links; i++) {
			if (demand_was_zero[i]) {
				leaving_cars[i][step] = 0;
			}
		}
		return result;
	}

	/**
	 * @brief Put values on the links for the split ratio at the given step
	 */
	private void initializeSplitRatio(int step) {
		double remaining_ratio = 1;
		for (int i = 0; i < nb_out_links - 1; i++) {
			double home_made_coef = ((double) nb_out_links - i)
					/ ((double) nb_out_links * ((double) nb_out_links + 1) / 2);
			split_ratio[i][step] = home_made_coef;
			remaining_ratio -= home_made_coef;
		}
		split_ratio[nb_out_links - 1][step] = remaining_ratio;
	}

	/*
	 * The coefficient used for the gradient descent: new splitRatio =
	 * magic_coef * gradient * (objectif - value)
	 */
	private double magic_coef = 0.1;

	/*
	 * private double[] gradientComputation(double[] TT, int step) {
	 * 
	 * for (int i = 0; i < nb_out_links; i++) { // We add the cars in the next
	 * buffers leaving_cars[i][step] = split_ratio[i][step] * total_demand[step]
	 * (1 + magic_coef); }
	 * 
	 * double[] marginal_TT = computeLinksTT(step);
	 * 
	 * double[] gradient = new double[nb_out_links]; for (int i = 0; i <
	 * nb_out_links; i++) { gradient[i] = (marginal_TT[i] - TT[i]) /
	 * (split_ratio[i][step] * magic_coef); }
	 * 
	 * return gradient; }
	 */

	/**
	 * @brief Compute the average of the travel times of used roads
	 */
	private double averageTT(double[] TT, int step) {
		double result = 0;
		double nb_roads_used = 0;
		// TODO: better define and put roads to empty
		for (int i = 0; i < nb_out_links; i++) {
			if (!Discretization.equals(split_ratio[i][step], 0)) {
				result += TT[i];
				nb_roads_used++;
			}
		}
		return result / nb_roads_used;
	}

	/**
	 * @return True if the travel times are equals within the roads TODO: accept
	 *         a travel time higher if empty road
	 * 
	 *         TODO: Accept optimal when empty roads have a huge latency
	 */
	private boolean isOptimal(double target, double[] TT, int step) {
		for (int i = 0; i < nb_out_links; i++) {
			if (Discretization.equals(leaving_cars[i][step], 0)) {
				if (TT[i] <= target + Discretization.getEpsilon())
					return false;
			} else if (!Discretization.equals(target, TT[i]))
				return false;
		}

		return true;
	}

	public void findOptimalSplitRatio(int step) {
		if (!is_built) {
			System.out.println("You tried to run the UserEquilibrium optimal "
					+ "without building your distributor.");
			return;
		}

		/* Initialization of some split ratio for the given time step */
		initializeSplitRatio(step);

		/*
		 * for (int i = 0; i < sinks.length; i++) { sinks[i].plot("Sink " + i,
		 * "time", "Arrived cars"); }
		 */

		/*
		 * if (step == 1) for (int i = 0; i < cumulative_left_cars.length; i++)
		 * { Plots.plotLineChart(cumulative_left_cars[i], "Cumulative " + i,
		 * "time", "Cumulative cars"); Plots.plotLineChart(leaving_cars[i],
		 * "Leaving cars " + i, "time", "Leaving cars"); }
		 */

		final int max_attempts = 100;

		// For plotting
		double[][] TT_at_steps = new double[nb_out_links][max_attempts];
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series[] = new XYSeries[nb_out_links];
		for (int i = 0; i < nb_out_links; i++) {
			series[i] = new XYSeries("Road " + i);
		}

		double average;
		double[] TT;
		int attempts = 1;
		do {
			if (attempts % 100 == 0)
				magic_coef /= 10;
			if (attempts == max_attempts) {
				System.out.println("Impossible to get it " + max_attempts
						+ " attempts");
				return;
			}

			System.out.println("Attempts " + attempts);

			/* We compute the according travel times */
			TT = computeLinksTT(step);
			average = averageTT(TT, step);

			for (int i = 0; i < nb_out_links; i++)
				System.out.println("TT Path " + i + ": " + TT[i]);
			System.out.println("ATT " + average);

			/** Exponential update **/
			double alpha_max = 0.1;
			for (int i = 0; i < nb_out_links; i++) {
				if (TT[i] != 0)
					alpha_max = Math.min(alpha_max, split_ratio[i][step]
							/ TT[i]);
			}
			System.out.println("Alphamax = " + alpha_max);

			double sum_split_ratio = 0;
			for (int i = 0; i < nb_out_links; i++) {
				split_ratio[i][step] *= Math.exp(- TT[i]);
				sum_split_ratio += split_ratio[i][step];

			}
			// Normalisation
			for (int i = 0; i < nb_out_links; i++) {
				split_ratio[i][step] = split_ratio[i][step] / sum_split_ratio;
				System.out.println("Modified split ratio " + i + " :"
						+ split_ratio[i][step]);
			}
			System.out.println();

			// Plot
			for (int i = 0; i < nb_out_links; i++) {
				series[i].add(attempts, TT[i]);
			}
			for (int i = 0; i < nb_out_links; i++) {
				TT_at_steps[i][attempts] = TT[i];
			}

			attempts++;
		} while (!isOptimal(average, TT, step));

		// TODO: put 1 to 1 and 0 to 0
		System.out.println("Converged after" + attempts + "\n");
		for (int i = 0; i < nb_out_links; i++) {
			System.out.println("Travel time for " + i + ", step " + step
					+ " is: " + TT[i] + " for ratio " + split_ratio[i][step]);
		}
		System.out.println("**********************************");

		for (int i = 0; i < nb_out_links; i++) {
			dataset.addSeries(series[i]);
		}
		Plots.plotLineChartFromCollection(dataset, "Convergence step " + step,
				"10*steps", "Travel Time");

		magic_coef = 0.1;

	}

	public double[][] getSplit_ratio() {
		return split_ratio;
	}
}

/* We compute the according gradient */
// gradient = gradientComputation(TT, step);

/* We affect the new ratio */
// double remaining_split_ratio = 1;

// TODO: Manage null gradient
// for (int i = 0; i < nb_out_links - 1; i++) {
// split_ratio[i][step] += gradient[i] * (average - TT[i]);
// remaining_split_ratio -= split_ratio[i][step];
// }
// split_ratio[nb_out_links - 1][step] = remaining_split_ratio;

/**
 * // Formula for Walid double sum_split_ratio = 0; for (int i = 0; i <
 * nb_out_links; i++) { sum_split_ratio += split_ratio[i][step]; }
 * 
 * double theta = 1; for (int i = 0; i < nb_out_links; i++) {
 * split_ratio[i][step] = split_ratio[i][step] - Math.min(split_ratio[i][step],
 * (double) 1.0 / (double) attempts * (TT[i] - theta * (1 / split_ratio[i][step]
 * + 1 / (sum_split_ratio - 1))) ); System.out.println("Link " + i + ":" +
 * split_ratio[i][step]); }
 * 
 * // Normalisation sum_split_ratio = 0; for (int i = 0; i < nb_out_links; i++)
 * { sum_split_ratio += split_ratio[i][step]; } for (int i = 0; i <
 * nb_out_links; i++) { split_ratio[i][step] = split_ratio[i][step] /
 * sum_split_ratio; System.out.println("Split ratio " + i + " :" +
 * split_ratio[i][step]); }
 **/
