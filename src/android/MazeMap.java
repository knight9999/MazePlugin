package info.nfuture.plugin.mazeplugin;

import java.util.ArrayList;
import java.util.List;

public class MazeMap {
	int map_w = 20;
	int map_h = 20;
	
	public static int kabe = 1;
	public static int road = 0;
	
	List<Integer> map;
	
	public MazeMap(int w,int h) {
		map_w = w;
		map_h = h;
		setup();
	}
	
	
	@SuppressWarnings("serial")
	public void setup() {
		map = new ArrayList<Integer>(map_w*map_h) {
			{
			for (int i=0;i<map_w*map_h;i++) {
				add(kabe);
			}
		}};
	}
	
	public void clear() {
		for (int i=0;i<map_w*map_h;i++) {
			map.set(i,kabe); 
		}
	}

	public int getValue(int x,int y) {
		return map.get(x+y*map_w);
	}

	public void setValue(int x,int y,int value) {
		map.set(x+y*map_w,value);
	}

	public int getValueEx(int x,int y) {
		if (0<=x && x<map_w) {
			if (0<=y && y<map_h) {
				return getValue(x,y);
			} else if (y<0) {
				return getValueEx(x,y+map_h);
			} else {
				return getValueEx(x,y-map_h);
			}
		} else if (x<0) {
			return getValueEx(x+map_w,y);
		} else {
			return getValueEx(x-map_w,y);
		}
	}
	
	public void setValueEx(int x,int y,int value) {
		if (0<=x && x<map_w) {
			if (0<=y && y<map_h) {
				setValue(x,y,value);
			} else if (y<0) {
				setValueEx(x,y+map_h,value);
			} else {
				setValueEx(x,y-map_h,value);
			}
		} else if (x<0) {
			setValueEx(x+map_w,y,value);
		} else {
			setValueEx(x-map_w,y,value);
		}
	}

	public void correct(MapPoint point) { // mapに収まる値に修正する。
		while (point.x<0) {
			point.x += this.map_w;
		}
		while (point.x>=this.map_w) {
			point.x -= this.map_w;
		}
		while (point.y<0) {
			point.y += this.map_h;
		}
		while (point.y>=this.map_h) {
			point.y -= this.map_h;
		}
	}
	
	public MapPoint forwardPoint(MapPoint point,int direction) {
		// direction
		// 1:north, 2:east 3:south 4:west
		MapPoint new_point = relativePoint( point , direction , 0, -1 );
		return new_point;
	}

	public MapPoint relativePoint(MapPoint point,int direction,int x,int y) {
		MapPoint new_point = new MapPoint( point.x , point.y );
		switch (direction) {
		case 1:
			new_point.x += x;
			new_point.y += y;
			break;
		case 2:
			new_point.x += -y;
			new_point.y += x;
			break;
		case 3:
			new_point.x += -x;
			new_point.y += -y;
			break;
		case 4:
			new_point.x += y;
			new_point.y += -x;
			break;
		default :
		}
		correct( new_point );
		return new_point;
	}

/*
 	public int[] forwardSquare(MapPoint pos,int direction,int width,int height) {

		int[] result = new int[width*height];
		int cnt = 0;
		for (int i=-(width-1)/2;i<=(width-1)/2;i++) {
			for (int j=0;j>-height;j--) {
				MapPoint target = relativePoint(pos,direction,i,j);
				result[cnt] = this.getValueEx(target.x,target.y);
				cnt += 1;
			}
		}
		return result;
	}
*/
	
	public int turn(int direction,int direct) {
		int new_direction = direction + direct;
		while (new_direction<1) { new_direction += 4; }
		while (new_direction>4) { new_direction -= 4; }
		return new_direction;
	}
}
