package io;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Formatter;

import org.jfree.chart.JFreeChart;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class InputOutput {

  static public void printTable(double[][] table) {

    for (int i = 0; i < table.length; i++) {
      for (int j = 0; j < table[0].length; j++)
        System.out.printf("%9.2f ", table[i][j]);
      System.out.println();
    }
  }

  public static void printTable(double[] table) {
    for (int i = 0; i < table.length; i++) {
      System.out.printf("%9.2f \n", table[i]);
    }
  }

  public static void printControl(double[] table, int C) {
    int T = table.length / C;
    assert (T * C == table.length) : "The control vector and the number of commodities does not match";

    for (int k = 0; k < T; k++) {
      System.out.print("Time step " + k + " ");
      for (int i = 0; i < C; i++)
        // System.out.printf("%9.5f", table[i]);
        System.out.print(table[i] + " ");
      System.out.println();
    }
  }

  public static void tableToFile(double[][] table, String file_name) {
    Formatter formatter = null;
    try {
      formatter = new Formatter(file_name);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    formatter.format("Size of the matrix: " + table[0].length + "x"
        + table.length + "\n");
    for (int i = 0; i < table.length; i++) {
      for (int j = 0; j < table[0].length; j++)
        formatter.format("%6.2f ", table[i][j]);
      formatter.format("\n");
    }

    formatter.close();
  }

  public static void tableToFile(double[] control, String file_name) {
    Formatter formatter = null;
    try {
      formatter = new Formatter(file_name);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < control.length; i++) {
      formatter.format("%6.2f \n", control[i]);
    }

    formatter.close();
  }

  static public Writer Writer(String file_name) {

    Writer writer = null;
    try {
      writer = new FileWriter(file_name);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return writer;
  }

  static public BufferedWriter BufferedWriter(String file_name) {
    BufferedWriter bw = new BufferedWriter(Writer(file_name));

    return bw;
  }

  static public Reader Reader(String file_name) {

    Reader reader = null;
    try {
      reader = new FileReader(file_name);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return reader;
  }

  static public void close(Closeable stream) {
    try {
      if (stream != null)
        stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Writes a chart to a a file in PDF format.
   */
  public static void writeChartAsPDF(String file_name, JFreeChart chart,
      int width, int height, FontMapper mapper) {

    File file = new File(file_name);
    OutputStream out = null;
    try {
      out = new BufferedOutputStream(new FileOutputStream(file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return;
    }

    Rectangle pagesize = new Rectangle(width, height);
    Document document = new Document(pagesize, 50, 50, 50, 50);
    try {
      PdfWriter writer = PdfWriter.getInstance(document, out);
      // document.addAuthor("JFreeChart");
      // document.addSubject("Demonstration");
      document.open();
      PdfContentByte cb = writer.getDirectContent();
      PdfTemplate tp = cb.createTemplate(width, height);
      Graphics2D g2 = tp.createGraphics(width, height, mapper);
      Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
      chart.draw(g2, r2D);
      g2.dispose();
      cb.addTemplate(tp, 0, 0);
    } catch (DocumentException de) {
      System.err.println(de.getMessage());
    }
    document.close();
    close(out);
  }

  public static void writeChartAsPDF(String file_name, JFreeChart chart,
      int width, int height){
    writeChartAsPDF(file_name, chart, width, height, new DefaultFontMapper());
  }
}