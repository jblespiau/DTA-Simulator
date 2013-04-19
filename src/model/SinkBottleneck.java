package model;

public class SinkBottleneck extends Sink {

	private double bottleneck_capacity;
	
	public SinkBottleneck(double bottleneck_capacity) {
		super();
		this.bottleneck_capacity = bottleneck_capacity;
	}

	@Override
	public double supply(int step) {
		return bottleneck_capacity;
	}

	@Override
	public String printNetwork(int step) {
		return "[BottleneckSink: " + bottleneck_capacity + "]";
	}
}
