package info.nfuture.plugin.mazeplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MazePlugin extends CordovaPlugin {

	public static Context mContext = null;
	private static ViewGroup viewGroup = null;
	private static LinearLayout mazeLayout = null;
	
	private static GLSurfaceView glSurfaceView = null;
	private static MazeMap map = null;
	private static MazeRender render = null;
	
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		mContext = super.cordova.getActivity().getApplicationContext();
		mazeLayout = new LinearLayout(mContext);
		mazeLayout.setGravity( Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL );
		viewGroup = (ViewGroup) ( (ViewGroup) webView.getParent() ).getParent();
	}

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if ( action.equals("init")) {
			init(args,callbackContext);
		} else if ( action.equals("show")) {
			show(args,callbackContext);
		} else if ( action.equals("remove")) {
			remove(args,callbackContext);
		} else if ( action.equals("forward")) {
			forward(args,callbackContext);
		} else if ( action.equals("turnleft")) {
			turnleft(args,callbackContext);
		} else if ( action.equals("turnright")) {
			turnright(args,callbackContext);
		}
		return true;
	}
	
	public void init(JSONArray args,CallbackContext callbackContext) throws JSONException {
		/* make map */
		map = new MazeMap(20,20);
		MakeMap makeMap = new MakeMap(map);
		makeMap.make(1,1,1000);

		render = new MazeRender();
		render.map = map;
		render.pos = new MapPoint(1,1);
		render.direction = 3;
		
		render.animation_mode = 0;
		render.animation_step = 0;
		render.animation_callback = null;
		
		render.context = mContext;
		callbackContext.success("ok");
		
	}

	public void show(JSONArray args,final CallbackContext callbackContext) throws JSONException {
		glSurfaceView = new CustomSurfaceView( mContext );
		glSurfaceView.setEGLConfigChooser(true);
		glSurfaceView.setRenderer(render);
		
		cordova.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mazeLayout.addView( glSurfaceView );
				
				viewGroup.addView(mazeLayout,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.TOP));
				
				callbackContext.success("ok");
				
			} 
		});
		
	}
	
	public void remove(JSONArray args,final CallbackContext callbackContext) throws JSONException {
		cordova.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				viewGroup.removeView( mazeLayout );
				mazeLayout.removeAllViews();
				callbackContext.success("ok");
			}
			
		});
	}

	public void forward(JSONArray args,final CallbackContext callbackContext) throws JSONException {
		if (render.animation_mode != 0) {
			return;
		}
		MapPoint newPos = render.map.forwardPoint(render.pos, render.direction);
		if (render.map.getValue( newPos.x , newPos.y) == 0) {
			render.animation_mode = 1;
			render.animation_step = 1;
			render.animation_callback = new MazeRenderInterface() {

				@Override
				public void do_callback(MazeRender render) {
					MapPoint newPos = render.map.forwardPoint(render.pos, render.direction);
					if (render.map.getValue( newPos.x, newPos.y) == 0) {
						render.pos = newPos;
					}
				}
				
			};
		}
		callbackContext.success("ok");

	}
	
	public void turnleft(JSONArray args,final CallbackContext callbackContext) throws JSONException {
		if (render.animation_mode != 0) {
			return;
		}
		render.animation_mode = 2;
		render.animation_step = 1;
		render.animation_callback = new MazeRenderInterface() {

			@Override
			public void do_callback(MazeRender render) {
				render.direction = render.map.turn(render.direction, -1);
			}
			
		};
		callbackContext.success("ok");
	}

	public void turnright(JSONArray args,final CallbackContext callbackContext) throws JSONException {
		if (render.animation_mode != 0) {
			return;
		}
		render.animation_mode = 3;
		render.animation_step = 1;
		render.animation_callback = new MazeRenderInterface() {

			@Override
			public void do_callback(MazeRender render) {
				render.direction = render.map.turn(render.direction, 1);
			}
			
		};
		callbackContext.success("ok");
	}

}
