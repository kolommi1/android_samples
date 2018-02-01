package uhk.android_samples.lvl1basic.p02geometry.p01cube;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private uhk.android_samples.lvl1basic.p02geometry.p01cube.Renderer renderer;

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
    public void setRenderer(uhk.android_samples.lvl1basic.p02geometry.p01cube.Renderer renderer) {
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

}
