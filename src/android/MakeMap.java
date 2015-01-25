package info.nfuture.plugin.mazeplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MakeMap {
	public MazeMap map;
	public MapPoint pos;
	List<MapPoint> stack;
	
	public MakeMap(MazeMap amap) {
		map = amap;
	}
	
	public void make(int init_x,int init_y,int max_count) {
		stack = new ArrayList<MapPoint>();
		pos = new MapPoint();
		pos.x = init_x;
		pos.y = init_y;
		map.setValue(pos.x,pos.y,0);
		
		int count = 0;
		int res;
		do {
			res = makeStep();
		} while (res==1 && count++ < max_count);

		showMap();
	}

	public int makeStep() {
		List<MapPoint> candidates = getCandidates();
		if (candidates.size() == 0) {
			System.out.println("Candidatesがありません");
			while (stack.size()>0) {
				System.out.println( "stack size = " + String.valueOf( stack.size() ) );
				Random rnd = new Random();
				int ran = rnd.nextInt( stack.size() );
				MapPoint newPos = stack.get(ran);
				pos.x = newPos.x;
				pos.y = newPos.y;
				System.out.println("x = " + pos.x + ", y = " + pos.y );
				List<MapPoint> nextCandidates = getCandidates();
				if (nextCandidates.size()>0) {
					System.out.println("次の候補に移ります");
					return 1;
				}
				stack.remove(ran);
			}
			System.out.println("次の候補がないため、終了します");
			return 0;
		}
		System.out.println("次のCandidtesは" + String.valueOf( candidates.size() ) + "つあります。");

		if (candidates.size()>=2) {
			stack.add( new MapPoint(pos.x,pos.y) );
		}
		
		Random rnd = new Random();
		int ran = rnd.nextInt( candidates.size() );
		System.out.println("ran = " + String.valueOf( ran ) );
		
		MapPoint nextPos = candidates.get( ran );
		pos.x = nextPos.x;
		pos.y = nextPos.y;
		map.setValue(pos.x,pos.y,0); // 現在の位置を道にする
		
		System.out.println("x = "+String.valueOf( pos.x ) + ", y = " + String.valueOf(pos.y) );
		
		return 1;
	}

	public List<MapPoint> getCandidates() {
		List<MapPoint> candidates = new ArrayList<MapPoint>();
		int kabe = 1;
		// 上チェック
		if (pos.y>=2) {
			if ( (map.getValue(pos.x  ,pos.y-1) == kabe ) &&
			     (map.getValue(pos.x-1,pos.y-1) == kabe ) &&
				 (map.getValue(pos.x-1,pos.y-2) == kabe ) &&
				 (map.getValue(pos.x  ,pos.y-2) == kabe ) &&
				 (map.getValue(pos.x+1,pos.y-2) == kabe ) &&
				 (map.getValue(pos.x+1,pos.y-1) == kabe ) ) {
				candidates.add( new MapPoint(pos.x,pos.y-1) ); 
			}
		}
		// 右チェック
		if (pos.x<=map.map_w-1-2) {
			if ( (map.getValue(pos.x+1,pos.y  ) == kabe ) &&
				 (map.getValue(pos.x+1,pos.y-1) == kabe ) &&
				 (map.getValue(pos.x+2,pos.y-1) == kabe ) &&
				 (map.getValue(pos.x+2,pos.y  ) == kabe ) &&
				 (map.getValue(pos.x+2,pos.y+1) == kabe ) &&
				 (map.getValue(pos.x+1,pos.y+1) == kabe ) ) {
				candidates.add( new MapPoint(pos.x+1,pos.y) ); 
			}
		}
		// 下チェック
		if (pos.y<=map.map_h-1-2) {
			if ( (map.getValue(pos.x  ,pos.y+1) == kabe ) &&
				 (map.getValue(pos.x+1,pos.y+1) == kabe ) &&
				 (map.getValue(pos.x+1,pos.y+2) == kabe ) &&
				 (map.getValue(pos.x  ,pos.y+2) == kabe ) &&
				 (map.getValue(pos.x-1,pos.y+2) == kabe ) &&
				 (map.getValue(pos.x-1,pos.y+1) == kabe ) ) {
				candidates.add( new MapPoint(pos.x,pos.y+1) ); 
			}
		}
		// 左チェック
		if (pos.x>=2) {
			if ( (map.getValue(pos.x-1,pos.y  ) == kabe ) &&
				 (map.getValue(pos.x-1,pos.y+1) == kabe ) &&
				 (map.getValue(pos.x-2,pos.y+1) == kabe ) &&
				 (map.getValue(pos.x-2,pos.y  ) == kabe ) &&
				 (map.getValue(pos.x-2,pos.y-1) == kabe ) &&
				 (map.getValue(pos.x-1,pos.y-1) == kabe ) ) {
				candidates.add( new MapPoint(pos.x-1,pos.y) );
			}
		}
		return candidates;
		
	}


	public void showMap() {
		for (int j=0;j<map.map_h;j++) {
			String str = "";
			for (int i=0;i<map.map_w;i++) {
				int value = map.getValue(i,j);
				if (value == 1) {
					str += "■";
				} else {
					str += "・";
				}
			}
			System.out.println( str );
		}
	}

}
