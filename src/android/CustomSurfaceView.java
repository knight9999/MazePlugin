package info.nfuture.plugin.mazeplugin;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class CustomSurfaceView extends GLSurfaceView {

	public CustomSurfaceView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		System.out.println( "w = " + String.valueOf(width) + ", h = " + String.valueOf(height) );
		int size = (width<height) ? width:height;
		setMeasuredDimension(size, size);
	}
	
	

}
