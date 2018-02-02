package uhk.android_samples.lvl1basic.p02geometry.p02strip;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import uhk.android_samples.R;

public class MyGLSurfaceView extends GLSurfaceView {

    private uhk.android_samples.lvl1basic.p02geometry.p02strip.Renderer renderer;

    private float ox, oy;

    public MyGLSurfaceView(Context context){
        super(context);

        // Render the view only when there is a change in the drawing data
        //  setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Hide superclass method to access renderer.
    public void setRenderer(uhk.android_samples.lvl1basic.p02geometry.p02strip.Renderer renderer) {
        this.renderer = renderer;
        super.setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if (e != null) {
            // screen is touched
            if(e.getAction() == MotionEvent.ACTION_DOWN){
                ox = e.getX();
                oy = e.getY();
            }
            // touch moved on the screen
            if(e.getAction() == MotionEvent.ACTION_MOVE){
                if (renderer != null) {
                    renderer.rotateCamera(e.getX(),e.getY(), ox, oy);
                    ox = e.getX();
                    oy = e.getY();
                }
            }
            return true;
        }
        else{
            return super.onTouchEvent(e);
        }
    }

    /**
     * Listener for all buttons
     */
    public OnClickListener buttonListener = new OnClickListener() {
        public void onClick(View v) {
            final int id = v.getId();

            if(id == R.id.btn_left){
                renderer.moveCameraLeft();
            }
            if(id == R.id.btn_right){
                renderer.moveCameraRight();
            }
            if(id == R.id.btn_up){
                renderer.moveCameraForward();
            }
            if(id == R.id.btn_down){
                renderer.moveCameraBack();
            }
            if(id == R.id.btn_minus){
                renderer.cameraChangeRadius(1.1f);
            }
            if(id == R.id.btn_plus){
                renderer.cameraChangeRadius(0.9f);
            }
            if(id == R.id.btn_firstPerson){
                renderer.cameraToggleFirstPerson();
            }
            if(id == R.id.btn_polygons){
                renderer.togglePolygons();
            }
            if(id == R.id.btn_mode){
                renderer.changeMode();
            }

        }
    };

}
