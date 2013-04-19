package graphics;

import model.Environment;
import model.RoadChunk;

import org.jfree.data.xy.XYSeries;

public class Plots {

	static public XYSeries XYDensity(RoadChunk c, int nb_steps, String name) {
		double delta_t = Environment.getDelta_t();
		XYSeries result = new XYSeries(name);
		for (int i = 0; i < nb_steps + 1; i++) {
			result.add( (double)i * delta_t , c.density[i]);
		}
		return result;
	}
}
