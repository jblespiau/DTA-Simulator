package model.networkFactory;

import java.util.LinkedList;

public class Network {

	private LinkedList<Path> paths;
	
	public Network () {
		paths = new LinkedList<Path>();
	}
	
	public void addPath (Path p) {
		paths.add(p);
	}
}