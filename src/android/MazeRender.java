package info.nfuture.plugin.mazeplugin;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// import com.example.sample5.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;

public class MazeRender implements Renderer {
	public MazeMap map;
	public MapPoint pos;
	public int direction; // 1:north 2:east 3:south 4:west

	int animation_mode; // 0:no 1:go forwarding 2:turn left 3:turn right
	int animation_step; 
	MazeRenderInterface animation_callback;
	
    float[] lightAmbient=new float[]{0.6f,0.6f,0.6f,1.0f };//光源アンビエント
    float[] lightDiffuse=new float[]{0.6f,0.6f,0.6f,1.0f };//光源ディフューズ
    float[] lightPos    =new float[]{0,0,3,1};             //光源位置
    float[] matAmbient  =new float[]{0.2f,0.6f,0.2f,1.0f };//マテリアルアンビエント 
    float[] matDiffuse  =new float[]{0.2f,0.6f,0.2f,1.0f };//マテリアルディフューズ
    float[] matAmbient2  =new float[]{0.2f,0.2f,0.6f,1.0f };//マテリアルアンビエント 
    float[] matDiffuse2  =new float[]{0.2f,0.2f,0.6f,1.0f };//マテリアルディフューズ
    float[] matAmbient3  =new float[]{0.6f,0.2f,0.0f,1.0f };//マテリアルアンビエント 
    float[] matDiffuse3  =new float[]{0.6f,0.2f,0.0f,1.0f };//マテリアルディフューズ

    float box[]=new float[] {//頂点座標
    		//前面
    		-0.5f, -0.5f,  0.5f,
    		0.5f, -0.5f,  0.5f,
    		-0.5f,  0.5f,  0.5f,
    		0.5f,  0.5f,  0.5f,
    		//背面
    		-0.5f, -0.5f, -0.5f,
    		-0.5f,  0.5f, -0.5f,
    		0.5f, -0.5f, -0.5f,
    		0.5f,  0.5f, -0.5f,
    		//左面
    		-0.5f, -0.5f,  0.5f,
    		-0.5f,  0.5f,  0.5f,
    		-0.5f, -0.5f, -0.5f,
    		-0.5f,  0.5f, -0.5f,
    		//右面 
    		0.5f, -0.5f, -0.5f,
    		0.5f,  0.5f, -0.5f,
    		0.5f, -0.5f,  0.5f,
    		0.5f,  0.5f,  0.5f,
    		//上面
    		-0.5f,  0.5f,  0.5f,
    		0.5f,  0.5f,  0.5f,
    		-0.5f,  0.5f, -0.5f,
    		0.5f,  0.5f, -0.5f,
    		//した面
    		-0.5f, -0.5f,  0.5f,
    		-0.5f, -0.5f, -0.5f,
    		0.5f, -0.5f,  0.5f,
    		0.5f, -0.5f, -0.5f,
    		};
    float norms[]=new float[] {//頂点法線
    		//上面
    		0f,  0f,  1f,
    		0f,  0f,  1f,
    		0f,  0f,  1f,
    		0f,  0f,  1f,
    		//下面
    		0f,  0f,  -1f,
    		0f,  0f,  -1f,
    		0f,  0f,  -1f,
    		0f,  0f,  -1f,
    		//左面
    		-1f,  0f,  0f,
    		-1f,  0f,  0f,
    		-1f,  0f,  0f,
    		-1f,  0f,  0f,
    		//右面
    		1f, 0f, 0f,
    		1f, 0f, 0f,
    		1f, 0f, 0f,
    		1f, 0f, 0f,
    		//上面
    		0f,  1f, 0f,
    		0f,  1f, 0f,
    		0f,  1f, 0f,
    		0f,  1f, 0f,
    		//下面
    		0f,  -1f, 0f,
    		0f,  -1f, 0f,
    		0f,  -1f, 0f,
    		0f,  -1f, 0f
    		};

    float floors[] = new float[] {
    		-0.5f,	-0.5f,	0.5f,
    		0.5f,	-0.5f,	0.5f,
    		-0.5f,	-0.5f,	-0.5f,
    		0.5f,	-0.5f,	-0.5f
    };
    float floor_norms[] = new float[] {
    		0f, 1f, 0f,
    		0f, 1f, 0f,
    		0f, 1f, 0f,
    		0f, 1f, 0f
    };
    
	float box2[];
	float norms2[];
	float floors2[];
	float floor_norms2[];
	
	int colors2[];
	int box_count = 0;
	
//	int viewWidth;
//	int viewHeight;
	
	float yrot = 0.0f;

	int texture = 0;
//	Bitmap bitmap;
	Context context;
	
    @Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Nothing to do.
    	gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // 2012/11/20 これがないと歪む
	}
    
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glEnable(GL10.GL_DEPTH_TEST); // 奥行きをつけるために必要２ デバイスバッファを有効
    	gl.glDepthFunc(GL10.GL_LEQUAL);
    	gl.glClearDepthf(1.0f);
//		gl.glViewport(0, 0, width, height);
//    	gl.glViewport(-width*2, -height*2, width*5, height*5);

    	int size = width < height ? width:height;
//    	gl.glViewport(  (int) ( (width-size)/2 -  size*2.5 ) ,  (int) ( (height-size)/2 - size*2.5 ) , size*6, size*6 );
    	gl.glViewport(  (int) ( (width-size)/2 ) ,  (int) ( (height-size)/2 ) , size, size );
    	
//		viewWidth = width;
//		viewHeight = height;
		init(gl);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		drawFrame(gl);
	}
	
	protected void init(GL10 gl) {
    	//ライティングの指定
    	gl.glEnable(GL10.GL_LIGHTING);
    	gl.glEnable(GL10.GL_LIGHT0);
    	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_AMBIENT,matAmbient,0);
    	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_DIFFUSE,matDiffuse,0);
    	gl.glLightfv(GL10.GL_LIGHT0,GL10.GL_AMBIENT,lightAmbient,0);
    	gl.glLightfv(GL10.GL_LIGHT0,GL10.GL_DIFFUSE,lightDiffuse,0);
    	gl.glLightfv(GL10.GL_LIGHT0,GL10.GL_POSITION,lightPos,0);
        
    	gl.glLightf(GL10.GL_LIGHT0, GL10.GL_LINEAR_ATTENUATION, 0.01f);
    	
    	//デプスバッファの指定
//    	gl.glEnable(GL10.GL_DEPTH_TEST);
//    	gl.glDepthFunc(GL10.GL_LEQUAL);
    	//片面スムーズシェーディングの指定
    	gl.glEnable(GL10.GL_CULL_FACE);
    	gl.glShadeModel(GL10.GL_SMOOTH);
    	//背景のクリア
//    	gl.glClearColor(0.0f,0.0f,0.0f,0.0f);
//    	gl.glClearDepthf(1.0f);
		
    	int max_array_count = 5*5*2; // 壁ブロック最大で50個
    	box2 = new float[box.length*max_array_count];     
    	norms2 = new float[norms.length*max_array_count]; 
    	colors2 = new int[max_array_count];

    	int max_floor_array_count = 5*5*2; // 床最大で50個
    	floors2 = new float[floors.length*max_floor_array_count];     
    	floor_norms2 = new float[floor_norms.length*max_floor_array_count]; 
    
    	int[] textures = { 0 } ;
    	gl.glGenTextures(1,textures,0);
    	texture = textures[0];
	}

	protected void drawFrame(GL10 gl) {
		int anime_max = 20;
		
		int w = 5;
		int h = 5;
		
		Map<MapPoint,Integer> recPoint = new HashMap<MapPoint,Integer>();
		Map<MapPoint,Integer> recPoint2 = new HashMap<MapPoint,Integer>();
		
		//頂点配列と法線配列の指定
		box_count = 0;
		int floor_count = 0;
		
		for (int i=-(w-1)/2;i<=(w-1)/2;i++) {
    		for (int j=0;j>-h;j--) {
				MapPoint p = map.relativePoint(pos, direction, i, j);
				if ( map.getValue(p.x, p.y) == 1) { // 壁の表示
    				for (int k=0;k<box.length/3;k++) {
    					box2[box_count*box.length + k*3 ] = box[k*3] + i;
    					box2[box_count*box.length + k*3 + 1 ] = box[k*3+1];
    					box2[box_count*box.length + k*3 + 2 ] = box[k*3+2] + j;
    				}
    				for (int k=0;k<norms.length/3;k++) {
    					norms2[box_count*norms.length + k*3] = norms[k*3];
    					norms2[box_count*norms.length + k*3+1] = norms[k*3+1];
    					norms2[box_count*norms.length + k*3+2] = norms[k*3+2];
    				}
    				colors2[box_count] = ((p.y)%2 == 0)? 0:1;
    				box_count += 1;

    				recPoint.put(p, 1);
				} else if ( map.getValue(p.x,p.y) == 0) {  // 床の表示
    				for (int k=0;k<floors.length/3;k++) {
    					floors2[floor_count*floors.length + k*3 ] = floors[k*3] + i;
    					floors2[floor_count*floors.length + k*3 + 1 ] = floors[k*3+1];
    					floors2[floor_count*floors.length + k*3 + 2 ] = floors[k*3+2] + j;
    				}
    				for (int k=0;k<floor_norms.length/3;k++) {
    					floor_norms2[floor_count*floor_norms.length + k*3] = floor_norms[k*3];
    					floor_norms2[floor_count*floor_norms.length + k*3+1] = floor_norms[k*3+1];
    					floor_norms2[floor_count*floor_norms.length + k*3+2] = floor_norms[k*3+2];
    				}
    				floor_count += 1;
    				recPoint2.put(p, 1);
				}
//    			lmap_count += 1;
    		}
    	}
    	if (animation_mode==1) {
    		
    		for (int i=-(w-1)/2;i<=(w-1)/2;i++) {
        		for (int j=-h;j>-h-1;j--) {
    				MapPoint p = map.relativePoint(pos, direction, i, j);
    				if ( map.getValue(p.x, p.y) == 1) {
    					Integer nn = recPoint.get(p);
    					if (nn == null || nn.intValue() != 1) {
	        				for (int k=0;k<box.length/3;k++) {
	        					box2[box_count*box.length + k*3 ] = box[k*3] + i;
	        					box2[box_count*box.length + k*3 + 1 ] = box[k*3+1];
	        					box2[box_count*box.length + k*3 + 2 ] = box[k*3+2] + j;
	        				}
	        				for (int k=0;k<norms.length/3;k++) {
	        					norms2[box_count*norms.length + k*3] = norms[k*3];
	        					norms2[box_count*norms.length + k*3+1] = norms[k*3+1];
	        					norms2[box_count*norms.length + k*3+2] = norms[k*3+2];
	        				}
	        				colors2[box_count] = ((p.y)%2 == 0)? 0:1;
	        				box_count += 1;

	        				recPoint.put(p, 1);
        				}
        			} else if ( map.getValue(p.x,p.y) == 0) {
    					Integer nn = recPoint2.get(p);
    					if (nn == null || nn.intValue() != 1) {
    	    				for (int k=0;k<floors.length/3;k++) {
    	    					floors2[floor_count*floors.length + k*3 ] = floors[k*3] + i;
    	    					floors2[floor_count*floors.length + k*3 + 1 ] = floors[k*3+1];
    	    					floors2[floor_count*floors.length + k*3 + 2 ] = floors[k*3+2] + j;
    	    				}
    	    				for (int k=0;k<floor_norms.length/3;k++) {
    	    					floor_norms2[floor_count*floor_norms.length + k*3] = floor_norms[k*3];
    	    					floor_norms2[floor_count*floor_norms.length + k*3+1] = floor_norms[k*3+1];
    	    					floor_norms2[floor_count*floor_norms.length + k*3+2] = floor_norms[k*3+2];
    	    				}
    	    				floor_count += 1;
    	    				recPoint2.put(p, 1);
    					}
    				}
        		}
        	}
    	} 
    	if (animation_mode==2) {
    		int new_direction = map.turn(direction, -1);
    		
    		for (int i=-(w-1)/2;i<=(w-1)/2;i++) {
        		for (int j=0;j>-h;j--) {
    				MapPoint p = map.relativePoint(pos, new_direction, i, j);
    				if ( map.getValue(p.x, p.y) == 1) {
    					Integer nn = recPoint.get(p);
    					if (nn == null || nn.intValue() != 1) {
	        				for (int k=0;k<box.length/3;k++) {
	        					box2[box_count*box.length + k*3 ] = box[k*3] + j;
	        					box2[box_count*box.length + k*3 + 1 ] = box[k*3+1];
	        					box2[box_count*box.length + k*3 + 2 ] = box[k*3+2] - i;
	        				}
	        				for (int k=0;k<norms.length/3;k++) {
	        					norms2[box_count*norms.length + k*3] = norms[k*3];
	        					norms2[box_count*norms.length + k*3+1] = norms[k*3+1];
	        					norms2[box_count*norms.length + k*3+2] = norms[k*3+2];
	        				}
	        				colors2[box_count] = ((p.y)%2 == 0)? 0:1;
	        				box_count += 1;

	        				recPoint.put(p, 1);
        				}
        			} else if ( map.getValue(p.x,p.y) == 0) {
    					Integer nn = recPoint2.get(p);
    					if (nn == null || nn.intValue() != 1) {
    	    				for (int k=0;k<floors.length/3;k++) {
    	    					floors2[floor_count*floors.length + k*3 ] = floors[k*3] + j;
    	    					floors2[floor_count*floors.length + k*3 + 1 ] = floors[k*3+1];
    	    					floors2[floor_count*floors.length + k*3 + 2 ] = floors[k*3+2] - i;
    	    				}
    	    				for (int k=0;k<floor_norms.length/3;k++) {
    	    					floor_norms2[floor_count*floor_norms.length + k*3] = floor_norms[k*3];
    	    					floor_norms2[floor_count*floor_norms.length + k*3+1] = floor_norms[k*3+1];
    	    					floor_norms2[floor_count*floor_norms.length + k*3+2] = floor_norms[k*3+2];
    	    				}
    	    				floor_count += 1;
    	    				recPoint2.put(p, 1);
    					}
    				}
        		}
        	}
    	} 
    	if (animation_mode==3) {
    		int new_direction = map.turn(direction, 1);
    		
    		for (int i=-(w-1)/2;i<=(w-1)/2;i++) {
        		for (int j=0;j>-h;j--) {
    				MapPoint p = map.relativePoint(pos, new_direction, i, j);
    				if ( map.getValue(p.x, p.y) == 1) {
    					Integer nn = recPoint.get(p);
    					if (nn == null || nn.intValue() != 1) {
	        				for (int k=0;k<box.length/3;k++) {
	        					box2[box_count*box.length + k*3 ] = box[k*3] - j;
	        					box2[box_count*box.length + k*3 + 1 ] = box[k*3+1];
	        					box2[box_count*box.length + k*3 + 2 ] = box[k*3+2] + i;
	        				}
	        				for (int k=0;k<norms.length/3;k++) {
	        					norms2[box_count*norms.length + k*3] = norms[k*3];
	        					norms2[box_count*norms.length + k*3+1] = norms[k*3+1];
	        					norms2[box_count*norms.length + k*3+2] = norms[k*3+2];
	        				}
	        				colors2[box_count] = ((p.y)%2 == 0)? 0:1;
	        				box_count += 1;

	        				recPoint.put(p, 1);
        				}
    				} else if ( map.getValue(p.x,p.y) == 0) {
    					Integer nn = recPoint2.get(p);
    					if (nn == null || nn.intValue() != 1) {
    	    				for (int k=0;k<floors.length/3;k++) {
    	    					floors2[floor_count*floors.length + k*3 ] = floors[k*3] - j;
    	    					floors2[floor_count*floors.length + k*3 + 1 ] = floors[k*3+1];
    	    					floors2[floor_count*floors.length + k*3 + 2 ] = floors[k*3+2] + i;
    	    				}
    	    				for (int k=0;k<floor_norms.length/3;k++) {
    	    					floor_norms2[floor_count*floor_norms.length + k*3] = floor_norms[k*3];
    	    					floor_norms2[floor_count*floor_norms.length + k*3+1] = floor_norms[k*3+1];
    	    					floor_norms2[floor_count*floor_norms.length + k*3+2] = floor_norms[k*3+2];
    	    				}
    	    				floor_count += 1;
    	    				recPoint2.put(p, 1);
    					}
    				}
        		}
        	}
    	}
    	
    	FloatBuffer cubeBuff = makeFloatBuffer(box2);
    	FloatBuffer normBuff = makeFloatBuffer(norms2); 

    	FloatBuffer cubeBuff2 = makeFloatBuffer(floors2);
    	FloatBuffer normBuff2 = makeFloatBuffer(floor_norms2);
    	
    	//背景塗り潰し
    	gl.glClearColor(0,1,1,1);
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);


    	//http://itpro.nikkeibp.co.jp/article/COLUMN/20060807/245274/
    	gl.glMatrixMode(GL10.GL_PROJECTION);
    	gl.glLoadIdentity();
//    	gl.glFrustumf(-2.0f, 2.0f, -2.0f, 2.0f, 0.5f, 5.5f); // 最後の二つは、near,fatの順番
    	gl.glFrustumf(-0.4f, 0.4f, -0.4f, 0.4f, 0.5f, 5.5f); // 最後の二つは、near,fatの順番
    	
    	//モデルビュー行列の指定
    	gl.glMatrixMode(GL10.GL_MODELVIEW);
    	gl.glLoadIdentity();
    	if (animation_mode == 0) {
    		GLU.gluLookAt(gl,0,0.0f,0.5f,0,0.0f,0,0,1,0);
    	} else if (animation_mode == 1) {
    		float s = ((float)animation_step) / ((float) anime_max);
    		GLU.gluLookAt(gl, 0, 0.0f, 0.5f - s, 0, 0.0f, 0.0f - s, 0, 1, 0);
    	} else if (animation_mode == 2) {
    		float lvl = ((float)animation_step) / ((float) anime_max);
    		float pi = 3.141592f;
    		float angle = pi/2*lvl;
    		float nx = (float) (Math.sin(angle) * (0.5f));
    		float nz = (float) (Math.cos(angle) * (0.5f));
    		GLU.gluLookAt(gl, nx, 0.0f, nz , 0, 0, 0.0f, 0, 1, 0);
    	} else if (animation_mode == 3) {
    		float lvl = ((float)animation_step) / ((float) anime_max);
    		float pi = 3.141592f;
    		float angle = pi/2*lvl;
    		float nx = (float) (Math.sin(-angle) * (0.5f));
    		float nz = (float) (Math.cos(-angle) * (0.5f));
    		GLU.gluLookAt(gl, nx, 0.0f, nz, 0, 0, 0.0f, 0, 1, 0);
    	}
    	//回転の指定
    	gl.glRotatef(0,1,0,0);
    	gl.glRotatef(yrot,0,1,0);
//    	yrot+=1.0f;
    	

    	//頂点配列の指定 
    	gl.glVertexPointer(3,GL10.GL_FLOAT,0,cubeBuff);
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	//法線配列の指定
    	gl.glNormalPointer(GL10.GL_FLOAT,0,normBuff);
    	gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);


    	// 作画(壁)
    	for (int k=0;k<box_count;k++) {
//        	gl.glColor4f(1.0f,0,0,1.0f);
    		if (colors2[k] == 0) {
    			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_AMBIENT,matAmbient,0);
    			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_DIFFUSE,matDiffuse,0);
    		} else {
    			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_AMBIENT,matAmbient2,0);
    			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_DIFFUSE,matDiffuse2,0);
    		}
        	//前面と背面のプリミティブの描画
//        	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_AMBIENT,matAmbient,0);
//        	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_DIFFUSE,matDiffuse,0);
        	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,k*24+0,4);
        	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,k*24+4,4);
        	//左面と右面のプリミティブの描画
//        	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_AMBIENT,matAmbient,0);
//        	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_DIFFUSE,matDiffuse,0);
        	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,k*24+8,4);
        	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,k*24+12,4);
        	//上面と下面のプリミティブの描画
//        	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_AMBIENT,matAmbient,0);
//        	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_DIFFUSE,matDiffuse,0);
        	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,k*24+16,4);
        	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,k*24+20,4);
    	}

    	//頂点配列の指定 
    	gl.glVertexPointer(3,GL10.GL_FLOAT,0,cubeBuff2);
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	//法線配列の指定
    	gl.glNormalPointer(GL10.GL_FLOAT,0,normBuff2);
    	gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_AMBIENT,matAmbient3,0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_DIFFUSE,matDiffuse3,0);

    	gl.glBindTexture(GL10.GL_TEXTURE_2D,texture);
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
    	
//      int res_id = R.drawable.image_a1;    	
    	int res_id = context.getResources().getIdentifier("image_a1", "drawable", context.getPackageName());
    	
    	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), res_id);
    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bitmap,0);
    	bitmap.recycle();
    	
    	float coords[] = null;
    	if (direction==1) {
    	  coords = new float[]{
    		  	0, 0,
    			1, 0,
    			0, 1,
    			1, 1,
    	  };
    	} else if (direction==2) {
    		coords = new float[]{
        			0, 1,
        			0, 0,
        			1, 1,
        			1, 0,
        		};
    	} else if (direction==3) {
    		coords = new float[]{
        			1, 1,
        			0, 1,
        			1, 0,
        			0, 0,
        		};
    	} else if (direction==4) {
    		coords = new float[]{
        			1, 0,
        			1, 1,
        			0, 0,
        			0, 1,
        		};
    	}
    	float coords2[] = new float[coords.length*floor_count];
    	for (int k=0;k<floor_count;k++) {
    		for (int l=0;l<8;l++) {
    			coords2[k*8+l] = coords[l];
    		}
    	}
    	
    	ByteBuffer textureCoord = ByteBuffer.allocateDirect(coords2.length*4);
    	textureCoord.order(ByteOrder.nativeOrder());
    	textureCoord.asFloatBuffer().put(coords2);
    	
    	gl.glEnable(GL10.GL_TEXTURE_2D);
    	gl.glTexCoordPointer(2,GL10.GL_FLOAT,0,textureCoord);

    	gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);


    	// 作画(床)
    	for (int k=0;k<floor_count;k++) {
        	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,k*4+0,4);
    	}
    	
    	gl.glDisable(GL10.GL_TEXTURE_2D);
    	
    	if (animation_mode != 0) {
    		if (animation_mode == 1 || animation_mode == 2 || animation_mode == 3) {
    			animation_step += 1;
    			if (animation_step>anime_max) {
    				if (animation_callback != null) {
    					animation_callback.do_callback(this);
    				}
    				animation_mode = 0;
    				animation_step = 0;
    			}
    		}
    	}
    	    	
	}

	
	protected static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect( arr.length*4 );
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

}
