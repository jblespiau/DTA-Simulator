package model;

import java.util.Iterator;

/**
 * @class Solver
 * @brief Contains the useful functions that can be applied to the network
 *
 */
public class Solver {

	/**
	 * @brief Checks the Courant–Friedrichs–Lewy conditions necessary to
	 * ensure the numerical computation is correct.
	 */
	public static void checkConstraints (Origin O) {
		Iterator<EntryCell> i = O.getIterator();
		
		do {
			Cell c = i.next();
			c.checkConstraints();
		} while (i.hasNext());
	}

	/* For full information about the network */
	/*
	public static void DFS(Origin O) {
		Iterator<EntryCell> i = O.getIterator();
		
		do {
			Cell c = i.next();
			System.out.print(O.getCell());
			DFS(c);
			System.out.println();
			
		} while (i.hasNext());
	}
	
	static void DFS(Cell C) {
		if (C == null)
			return;
		
		System.out.print("--->" + C.getCell());
		DFS(C.getNext());

	} */
	
	/* Simple and clearer details of the network */
	public static void printNetwork(Origin O, int step) {
		Iterator<EntryCell> i = O.getIterator();
		
		while (i.hasNext()) {
			Cell c = i.next();
			System.out.print(O.printNetwork(step));
			printNetwork(c, step);
			System.out.println();
			
		} 
	}
	
	public static void printNetwork(Cell C, int step) {
		if (C == null)
			return;
		
		System.out.print("->" + C.printNetwork(step));
		printNetwork(C.getNext(), step);

	}


}
