package info.nfuture.plugin.mazeplugin;

public class MapPoint {
	public int x;
	public int y;
	
	public MapPoint() {
		x=0;
		y=0;
	}
	
	public MapPoint(int ax,int ay) {
		x=ax;
		y=ay;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MapPoint) {
			MapPoint obj = (MapPoint) o;
			if (obj.x == x && obj.y == y) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return x*65536 + y;
	}
	
}
