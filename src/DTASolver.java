import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.tc33.jheatchart.HeatChart;

import model.*;


public class DTASolver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int nb_steps = 10;
		double delta_t = 1;
		Environment.setDelta_t(delta_t);
		Environment.setNb_steps(nb_steps);
		
		double F = 10;
		double[] demand = new double[nb_steps];
		for (int i = 0; i < nb_steps; i++)
			demand[i] = F;
		
		/* We try to have
		 * 1 is jammed
		 * 2 is in free_flow and F_in2 never limiting the flow
		 * 1 is faster than 2 */
		double l1 = 1, v1 = 0.9, w1 = 0.4, f_in1 = 2.5, f_out1 = 2.5, j1=10;
		double l2 = 1, v2 = 1, w2 = 0.6, f_in2 =2, f_out2 = 2, j2=5.8;
		double l3 = 1, v3 = 1, w3 = 0.6, f_in3 =2, f_out3 = 2, j3=5.8;
		
		/* Creation of the network */
		Origin O = new Origin(10);
		OrdinaryCell upper = new OrdinaryCell(l1, v1, w1,f_in1, f_out1, j1, 1);
		EntryCell up_entry = new EntryCell(upper);
		
		OrdinaryCell down1 = new OrdinaryCell(l2, v2, w2,f_in2, f_out2, j2, 2);
		EntryCell down_entry = new EntryCell(down1);
		OrdinaryCell down2 = new OrdinaryCell(l3, v3, w3,f_in3, f_out3, j3, 1);
		
		Sink S = new Sink();
		SinkBottleneck SB = new SinkBottleneck(1.5);
		
		O.add_link(up_entry);
		O.add_link(down_entry);
		down1.setNext(down2);
		upper.setNext(SB);
		down2.setNext(S);
		
		/* Printing of the network to check it is the wanted one */
		Solver.DFS(O);
		System.out.println();
		
		/* Definition of the flows: [i][step] */
		double[][] flows = new double[2][nb_steps];
		
		flows[0][0] = 16;
		flows[0][1] = 5;
		flows[0][2] = 7;
		
		flows[1][0] = 4;
		flows[1][1] = 0.5;
		
		O.setFlow(flows);
		
		/* Checking the initial conditions and running the dynamic */
		System.out.println("Checking CFL conditions...");
		Solver.checkConstraints(O);
		System.out.println("Simulation");
		
		for (int i = 0; i < nb_steps; i++) {
			O.runDynamic(i);
		}
		
		for (int i = 0; i <= nb_steps; i++) {
			Solver.printNetwork(O, i);
			System.out.println();
		}

		/* Print the results */
		double[][] average_TT = new double[nb_steps][1];
		Iterator<EntryCell> it = O.getIterator();
		Cell c = it.next().c;
		int i = 0;
		do {
			OrdinaryCell oc = (OrdinaryCell) c;
			int k;
			for (k = 0; k < nb_steps; k++)
				average_TT[k][i] = oc.l / oc.flow_out[k] * oc.density[k];
				
			c = c.getNext();
			i++;
		} while (!c.isSink());

		System.out.println("Expected Average Travel Time spent in cell 1");
		DecimalFormat format = new DecimalFormat("###.###");
		for (int k = 0; k < nb_steps; k++) {
			for (int j = 0; j < 1; j++) {
				System.out.print("|" + format.format(average_TT[k][j]));
			}
			System.out.println("");
		}
		//GUI plot = new GUI();
		HeatChart map = new HeatChart(average_TT);
		map.setTitle("Average Travel Time");
		map.setXAxisLabel("Index of the cell");
		map.setYAxisLabel("Time step");
		try {
			map.saveToFile(new File("java-heat-chart.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
