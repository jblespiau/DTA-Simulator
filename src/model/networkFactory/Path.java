package model.networkFactory;

public class Path {

	private Road begin, end;
	
	public Path ()  {
		begin = null;
		end = null;
	}
	
	public Path (Road r)  {
		begin = r;
		end = r;
	}
	
	public void addRoad(Road r) {
		if (begin == null) {
			begin = r;
			end = r;
		} else {
			end.setNext(r);
		}
	}
}
