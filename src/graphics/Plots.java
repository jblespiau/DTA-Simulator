package graphics;

import model.Discretization;
import model.RoadChunk;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryStepRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Plots {

	static public XYSeries XYDensity(RoadChunk c, int nb_steps, String name) {
		double delta_t = Discretization.getDelta_t();
		XYSeries result = new XYSeries(name);
		for (int i = 0; i < nb_steps + 1; i++) {
			result.add((double) i * delta_t, c.density[i]);
		}
		return result;
	}

	/** 
	 * @brief Open a windows and display the LineChart of a given dataset
	 */
	static public void plotLineChartFromCollection (XYSeriesCollection dataset, String name, String x, String y) {
		JFreeChart chart = ChartFactory.createXYLineChart(name, // title
				x, // x axis label
				y, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		GUI g = new GUI();
		g.setContentPane(chartPanel);
		g.setVisible(true);
	}
	
	
	/**
	 * @brief Plot the LineChart from a double[]
	 * @param data data[i] = f(i)
	 * @param name Title of the graph
	 * @param x Name of the abscissa
	 * @param y Name of the ordinate
	 */
	static public void plotLineChart(double[] data, String name, String x, String y) {
		double delta_t = Discretization.getDelta_t();
		XYSeries s = new XYSeries(name);
		for (int i = 0; i < data.length; i++) {
			s.add((double) i * delta_t, data[i]);
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s);

		JFreeChart chart = ChartFactory.createXYLineChart(name, // title
				x, // x axis label
				y, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		/*
		 * GUI g = new GUI(); g.setContentPane(chartPanel); g.setVisible(true);
		 */

		GUI g2 = new GUI();
		g2.setContentPane(chartPanel);
		g2.setVisible(true);
	}
	
	/** @brief TODO */
	static public void plotStepChart(double[] data, String name, String x, String y) {
		double delta_t = Discretization.getDelta_t();
		XYSeries s = new XYSeries(name);
		for (int i = 0; i < data.length; i++) {
			s.add((double) i * delta_t, data[i]);
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s);

		JFreeChart chart = ChartFactory.createXYStepChart(name, // title
				x, // x axis label
				y, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);
		final CategoryItemRenderer renderer = new CategoryStepRenderer(true);
        final CategoryAxis domainAxis = new CategoryAxis("Category");
        final ValueAxis rangeAxis = new NumberAxis("Value");
        final CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset(
                "Series ", "Type ", new double[3][3]);

        final CategoryPlot plot = new CategoryPlot(dataset2, domainAxis, rangeAxis, renderer);
        final JFreeChart chart2 = new JFreeChart("Category Step Chart", plot);
		ChartPanel chartPanel = new ChartPanel(chart2);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		/*
		 * GUI g = new GUI(); g.setContentPane(chartPanel); g.setVisible(true);
		 */

		GUI g2 = new GUI();
		g2.setContentPane(chartPanel);
		g2.setVisible(true);
	}
}
