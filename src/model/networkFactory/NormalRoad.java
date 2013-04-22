package model.networkFactory;

public class NormalRoad extends Road {

	private int length, speed;
	private int v, w, jam_density;
	private Road next;
	
	public NormalRoad(int length, int speed, int v, int w, int jam_density) {
		this.length = length;
		this.speed = speed;
		this.v = v;
		this.w = w;
		this.jam_density = jam_density;
	}
	
	@Override
	public Road getNext() {
		return next;
	}
	
	@Override
	public void setNext(Road next) {
		this.next = next;
	}

	@Override
	public boolean isRoad() {
		return true;
	}

	@Override
	public boolean isBottleneck() {
		return false;
	}
	
}
