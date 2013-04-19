package model;

import graphics.GUI;

import java.util.TreeSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @class Demand
 * @brief Represents a piecewise demand of flow. To use it, first add the Points
 *        on your demand graph and then call the build function
 * 
 */
public class Demand {

	TreeSet<Point> values;

	public Demand() {
		values = new TreeSet<Point>(new PointComparator());
	}

	public void add(Point p) {
		values.add(p);
	}

	/* Is needed when we will integrate the piecewise demand
	public void build() {
		Iterator<Point> it = values.iterator();
		Point previous = null, current = null;

		if (it.hasNext())
			previous = it.next();

		while (it.hasNext()) {
			current = it.next();
			previous.next = current;
			current.previous = previous;
			previous = current;
		}
	} */

	/* To be fast we assume that at time delta_t * step the value will be constant
	 * and being the one of the previous point
	 */
	public double getFlow(int step) {
		double delta_t = Environment.getDelta_t();
		double t1 = delta_t * step;
		Point previous = values.floor(new Point (t1, 0));
		//Point next = values.ceiling(new Point (t1, 0));
		return delta_t * previous.flow;
	}

	public double[] getFlows() {
		int steps = Environment.getNb_steps();
		double[] result = new double[steps];

		for (int i = 0; i < steps; i++)
			result[i] = getFlow(i);
		return result;
	}

	public XYSeries XYDensity(String name) {
		int nb_steps = Environment.getNb_steps();
		double delta_t = Environment.getDelta_t();

		XYSeries result = new XYSeries(name);
		
		for (int i = 0; i < nb_steps + 1; i++) {
			result.add(i * delta_t , getFlow(i));
		}
		return result;
	}
	
	public void display() {
		GUI g = new GUI();
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(XYDensity("Demand"));
		
		JFreeChart chart = ChartFactory.createXYStepChart(
				"Graphe of the Demand", // chart title
				"X", // x axis label
				"Y", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL,
				true, // include legend
				true, // tooltips
				false // urls
		);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		g.setContentPane(chartPanel);
		g.setVisible(true);
	}
}
