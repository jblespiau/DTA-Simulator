package model.networkFactory;

public class Bottleneck extends Road {

	private int bottleneck_capacity;

	public Bottleneck(int bottleneck_capacity) {
		super();
		this.bottleneck_capacity = bottleneck_capacity;
	}

	@Override
	public Road getNext() {
		return null;
	}

	@Override
	public boolean isRoad() {
		return false;
	}

	@Override
	public boolean isBottleneck() {
		return true;
	}

	@Override
	public void setNext(Road r) {
	}
}
