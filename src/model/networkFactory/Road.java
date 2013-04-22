package model.networkFactory;

public abstract class Road {

	abstract public Road getNext();
	abstract public void setNext(Road r);
	abstract public boolean isRoad();
	abstract public boolean isBottleneck();
}