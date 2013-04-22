package model;

import graphics.Plots;

import java.util.Iterator;

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
	 *        the given step to a given cell considering that the enough of the
	 *        future has been computed (states of sinks, cumulative_left_cars)
	 */
	double computeLinkTT(int cell, int step) {
		double delta_t = Discretization.getDelta_t();

		Point A = new Point(step, cumulative_left_cars[cell][step]);

		Point B;
		if (step == 0)
			B = new Point(step, 0);
		else
			B = new Point(step, cumulative_left_cars[cell][step - 1]);

		int arrival_previous_step;
		int arrival_this_step;
		int t = step;

		while (B.getY() < sinks[cell].getCumulative_cars(t)) {
			t++;
		}
		arrival_previous_step = t;
		// Point C = new Point(t * delta_t, B.getY());

		/*
		 * We find the time at which the sink has accepted the amount of left
		 * cars
		 */
		while (!Discretization.greaterThan(sinks[cell].getCumulative_cars(t),
				A.getY())) {
			t++;
		}
		arrival_this_step = t;
		System.out.println("Cumulative cars: before:" + B.getY()
				+ " this step:" + A.getY());
		System.out.println("Sink cars: t1:" + B.getY() + " t2:"
				+ arrival_this_step + " & (t2 - 1):"
				+ sinks[cell].getCumulative_cars(arrival_this_step - 1));
		// Point D = new Point(t * Discretization.getDelta_t(), A.getY());

		double cars_demand = split_ratio[cell][step] * total_demand[step];
		double weighted_travel_time = cars_demand * (arrival_this_step - step);
		double exceeded_time = 0;
		for (int tmp = arrival_previous_step; tmp < arrival_this_step; tmp++)
			exceeded_time += sinks[cell].getCumulative_cars(tmp);

		double average_travel_time = (weighted_travel_time - exceeded_time)
				/ cars_demand * delta_t;

		System.out.println("In the output of the computeLinkTT "
				+ average_travel_time + "\n");
		return average_travel_time;
	}

	/**
	 * @brief Computes all the Average Travel Time for each pools of cars that
	 *        left at the given step TODO: optimize to only calculate the adding
	 *        cars twice
	 */
	double[] computeLinksTT(int step) {

		double[] result = new double[nb_out_links];
		/*
		 * Compute the needed steps in order that all the cars that wanted to
		 * enter at time step has effectively arrived at their corresponding
		 * sink
		 */
		int time = step; // The current step which is computed
		boolean all_flows_exited;

		/*
		 * We compute the number of cars leaving.
		 */
		for (int i = 0; i < nb_out_links; i++) {
			double leaving = split_ratio[i][step] * total_demand[step];
			leaving_cars[i][step] = leaving;
			if (i == 1)
				System.out.println("Leaving cars 1: step: " + step + " " + leaving);
			/*
			 * Moreover we put some cars after that step to make sure the sink
			 * will be filled up
			 */
			if (step + 1 < Discretization.getNb_steps())
				leaving_cars[i][step + 1] = 1;
		}

		do {
			/*
			 * We run the dynamic (compute the flows at step $time, and the
			 * density at step ($time + 1)
			 */
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

		for (int i = 0; i < nb_out_links; i++) {
			result[i] = computeLinkTT(i, step);
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

	private double[] gradientComputation(double[] TT, int step) {

		for (int i = 0; i < nb_out_links; i++) {
			/* We add the cars in the next buffers */
			leaving_cars[i][step] = split_ratio[i][step] * total_demand[step]
					* (1 + magic_coef);
		}

		double[] marginal_TT = computeLinksTT(step);

		double[] gradient = new double[nb_out_links];
		for (int i = 0; i < nb_out_links; i++) {
			gradient[i] = (marginal_TT[i] - TT[i])
					/ (split_ratio[i][step] * magic_coef);
		}

		return gradient;
	}

	/**
	 * @brief Compute the average of the travel times
	 */
	private double averageTT(double[] TT) {
		double result = 0;
		for (int i = 0; i < nb_out_links; i++)
			result += TT[i];
		return result / (double) nb_out_links;
	}

	/**
	 * @return True if the travel times are equals within the roads TODO: accept
	 *         a travel time higher if empty road
	 *         TODO: Accept optimal when empty roads have a huge latency
	 */
	private boolean isOptimal(double target, double[] TT) {
		for (int i = 0; i < nb_out_links; i++) {
			if (!Discretization.equals(target, TT[i]))
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
		double[] gradient;

		/* Initialization of some split ratio for the given time step */
		initializeSplitRatio(step);

		/* We compute the according travel times */
		double[] TT = computeLinksTT(step);
		for (int i = 0; i < sinks.length; i++) {
			//sinks[i].plot("Sink " + i, "time", "Arrived cars");
		}

		/*
		for (int i = 0; i < cumulative_left_cars.length; i++) {
			Plots.plotLineChart(cumulative_left_cars[i], "Cumulative " + i,
					"time", "Cumulative cars");
			Plots.plotLineChart(leaving_cars[i], "Leaving cars " + i, "time",
					"Leaving cars");
		} */

		double average = averageTT(TT);
		System.out.println("Average attemps 1 " + average);
		int attempts = 1;
		while (!isOptimal(average, TT)) {
			if (attempts == 2)
				return;
			if (attempts % 100 == 0)
				magic_coef /= 10;
			if (attempts == 10000) {
				System.out.println("Impossible to get it");
				return;
			}
			/* We compute the according gradient */
			gradient = gradientComputation(TT, step);

			/* We affect the new ratio */
			double remaining_split_ratio = 1;

			// TODO: Manage null gradient
			for (int i = 0; i < nb_out_links - 1; i++) {
				split_ratio[i][step] += gradient[i] * (average - TT[i]);
				remaining_split_ratio -= split_ratio[i][step];
			}
			split_ratio[nb_out_links - 1][step] = remaining_split_ratio;

			TT = computeLinksTT(step);
			average = averageTT(TT);
			System.out.println("Average attemps " + attempts + " " + average);
			attempts++;
		}
		magic_coef = 0.1;

		for (int i = 0; i < nb_out_links - 1; i++) {
			System.out.println("Travel time for " + i + " is: " + TT[i]
					+ " for ratio " + split_ratio[i][step]);
		}
	}
}
