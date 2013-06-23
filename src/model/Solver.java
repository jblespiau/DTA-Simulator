package model;

import java.util.Iterator;


/**
 * @class Solver
 * @brief Contains the useful functions that can be applied to the network
 * 
 */
public class Solver {

	/**
	 * @brief Checks the Courant–Friedrichs–Lewy conditions necessary to ensure
	 *        the numerical computation is correct.
	 */
	public static void checkConstraints(Origin O) {
		Iterator<EntryCell> i = O.getIterator();

		do {
			Cell c = i.next();
			c.checkConstraints();
		} while (i.hasNext());
	}

	/* For full information about the network */
	/*
	 * public static void DFS(Origin O) { Iterator<EntryCell> i =
	 * O.getIterator();
	 * 
	 * do { Cell c = i.next(); System.out.print(O.getCell()); DFS(c);
	 * System.out.println();
	 * 
	 * } while (i.hasNext()); }
	 * 
	 * static void DFS(Cell C) { if (C == null) return;
	 * 
	 * System.out.print("--->" + C.getCell()); DFS(C.getNext());
	 * 
	 * }
	 */

	/**
	 * @brief Prints the state of the network (density at step and flow the
	 *        flows that will change between cell between step and step +1
	 * @param O
	 *            The Origin of the networks
	 * @param step
	 *            The step you want to print (time 0 = initial conditions)
	 */
	public static void printNetwork(Origin O, int step) {
		Iterator<EntryCell> i = O.getIterator();

		while (i.hasNext()) {
			Cell c = i.next();
			System.out.print(O.printNetwork(step));
			printNetwork(c, step);
			System.out.println();

		}
	}

	private static void printNetwork(Cell C, int step) {
		if (C == null)
			return;

		System.out.print("->" + C.printNetwork(step));
		printNetwork(C.getNext(), step);

	}

	/**
	 * @return The total numbers of cars in the road at time 0. It does NOT take
	 *         into account the buffer that should be empty at time 0
	 */
	public static double countInitialCars(EntryCell E) {
		return E.cell.countInitialCars();
	}

}
