import io.InputOutput;

import java.awt.Color;

import graphics.GUI;
import graphics.Plots;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import model.*;
import model.demandFactory.Demand;
import model.demandFactory.Point;

/**
 * @package module
 * @page intro Introduction
 * 
 *       The usual way to build a simultion is: - build a demand function (see
 *       Demand) - fix your time step size and number of steps of the problem
 *       (see Environment) - design your network - do just a simulation with an
 *       Origin in which you put your demand (get with buildDemand) or try to
 *       get the UE with adding a distributor and putting your buildDemand in it
 */
public class DTASolver {

  /**
   * @param args
   */
  public static void main(String[] args) {
    int nb_steps = 20;
    double delta_t = 1;
    Discretization.setDelta_t(delta_t);
    Discretization.setNb_steps(nb_steps);

    /*
     * Automatic discretization of the input demand
     */
    Demand d = new Demand();
    d.add(new Point(0, 1));
    d.add(new Point(1, 2));
    d.add(new Point(2, 3));
    d.add(new Point(3, 4));
    d.add(new Point(4, 5));
    d.add(new Point(5, 6));
    d.add(new Point(6, 5));
    d.add(new Point(7, 4));
    d.add(new Point(8, 3));
    d.add(new Point(9, 2));
    d.add(new Point(10, 1));
    d.display();
    double[] demand = d.buildDemand();

    /* Creation of the network */
    /* We create 3 roads, with 5, 4, 4 cells */
    double l = 1, v = l / delta_t, w = 0.4 * v, jam = 8, f = 3;
    double f_max1 = 2, f_max2 = 1.5, f_max3 = 1;
    Origin O = new Origin();
    EntryCell e;

    /* First road */
    e = new EntryCell(new RoadChunk(l, v, w, f, f, jam, 0));
    O.add_link(e);
    RoadChunk rc = e.cell;
    RoadChunk tmp;
    for (int i = 0; i < 4; i++) {
      tmp = new RoadChunk(l, v, w, f, f, jam, 0);
      rc.setNext(tmp);
      rc = tmp;
    }
    SinkBottleneck SB = new SinkBottleneck(f_max1);
    rc.setNext(SB);

    /* Second road */
    e = new EntryCell(new RoadChunk(l, v, w, f, f, jam, 0));
    O.add_link(e);
    rc = e.cell;
    for (int i = 0; i < 3; i++) {
      tmp = new RoadChunk(l, v, w, f, f, jam, 0);
      rc.setNext(tmp);
      rc = tmp;
    }
    SB = new SinkBottleneck(f_max2);
    rc.setNext(SB);

    /* Third road */
    e = new EntryCell(new RoadChunk(l, v, w, f, f, jam, 0));
    O.add_link(e);
    rc = e.cell;
    for (int i = 0; i < 3; i++) {
      tmp = new RoadChunk(l, v, w, f, f, jam, 0);
      rc.setNext(tmp);
      rc = tmp;
    }
    SB = new SinkBottleneck(f_max3);
    rc.setNext(SB);

    /***********************************************************
     * Checking the initial conditions and running the dynamic *
     ***********************************************************/
    /* Printing of the network to check it is the wanted one */
    System.out.println();
    Solver.printNetwork(O, 0);
    System.out.print("Checking CFL conditions...");
    Solver.checkConstraints(O);
    System.out.println("Done");

    /*********************************
     * Find User Equilibrium
     *********************************/
    System.out.println("\n***Running optimization*** \n");
    Distributor user_finder = new Distributor(demand, O);
    double error = 10E-3;
    int future = 11;
    for (int k = 0; k < future; k++)
      user_finder.findOptimalSplitRatio(k, error);

    if (false) {
      for (int i = 0; i <= future; i++) {
        System.out.println("Time step: " + i);
        Solver.printNetwork(O, i);
        System.out.println();
      }
    }

    // Test to see if the FIFO constrains is verified
    // TODO: compute the TT for empty links
    // double[][] split_ratio;
    double[][] travel_time = new double[O.getNbRoads()][future];
    // split_ratio = user_finder.getSplit_ratio();
    for (int i = 0; i < future; i++) {
      double[] TT = user_finder.computeLinksTT(i);
      for (int cell = 0; cell < O.getNbRoads(); cell++) {
        travel_time[cell][i] = TT[cell];
      }
    }

    /* For plotting */
    /* We plot the travel times */
    XYSeriesCollection dataset = new XYSeriesCollection();
    XYSeries series;
    for (int cell = 0; cell < O.getNbRoads(); cell++) {
      series = new XYSeries("Road " + (cell + 1));
      for (int k = 0; k < future; k++) {
        series.add(k, travel_time[cell][k]);
      }
      dataset.addSeries(series);
    }

    JFreeChart chartTT = ChartFactory.createXYLineChart(null, // title
        "Time steps", // x axis label
        "TravelTime", // y axis label
        dataset, // data
        PlotOrientation.VERTICAL, true, // include legend
        true, // tooltips
        false // urls
        );
    XYPlot plotTT = (XYPlot) chartTT.getPlot();
    ValueAxis yAxis = plotTT.getRangeAxis();
    yAxis.setRange(4, 7);

    Plots.plotLineChartFromCollection(dataset, null, "Time steps",
        "Travel Time");

    plotTT.setBackgroundPaint(Color.white);
    plotTT.setDomainGridlinePaint(Color.lightGray);
    plotTT.setRangeGridlinePaint(Color.lightGray);
    InputOutput.writeChartAsPDF("TravelTime.pdf", chartTT, 300, 432);

    /* We plot the split ratios */
    /* SR[i][k] is the slit ratio of road i, time step k */
    double[][] SR = user_finder.getSplit_ratio();

    XYSeriesCollection datasetSR = new XYSeriesCollection();
    XYSeries seriesSR;
    for (int cell = 0; cell < O.getNbRoads(); cell++) {
      seriesSR = new XYSeries("Road " + (cell + 1));
      for (int k = 0; k < future; k++) {
        seriesSR.add(k, SR[cell][k]);
      }
      datasetSR.addSeries(seriesSR);
    }

    JFreeChart chartSR = ChartFactory.createXYLineChart(null, // title
        "Time steps", // x axis label
        "Split ratio", // y axis label
        datasetSR, // data
        PlotOrientation.VERTICAL, true, // include legend
        true, // tooltips
        false // urls
        );
    Plots.plotLineChartFromCollection(datasetSR, null, "Time steps",
        "Split ratio");

    XYPlot plotSR = (XYPlot) chartSR.getPlot();
    plotSR.setRenderer(plotTT.getRenderer());
    plotSR.setBackgroundPaint(Color.white);
    plotSR.setDomainGridlinePaint(Color.lightGray);
    plotSR.setRangeGridlinePaint(Color.lightGray);
    InputOutput.writeChartAsPDF("SplitRatios.pdf", chartSR, 300, 432);
    /* We combine the two plots into one */
    CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis(
        "Time Steps"));
    plot.setGap(10.0);
    plot.setBackgroundPaint(Color.white);
    // plot.setRenderer(plotTT.getRenderer());
    plot.setFixedLegendItems(plotTT.getLegendItems());
    plot.add(plotTT, 1);
    plot.add(plotSR, 1);
    plot.setOrientation(PlotOrientation.VERTICAL);

    JFreeChart chart = new JFreeChart(
        "CombinedDomainXYPlot Demo",
        JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

    /* We take the legend from the first plot only */
    // chart.addSubtitle(new LegendTitle((XYPlot)(plot.getSubplots().get(0))));

    GUI g = new GUI();
    g.setContentPane(chartPanel);
    g.setVisible(true);

    /*
     * Plots.plotLineChartFromCollection(dataset, null,
     * "Time Steps", "Travel Time");
     */

  }
}