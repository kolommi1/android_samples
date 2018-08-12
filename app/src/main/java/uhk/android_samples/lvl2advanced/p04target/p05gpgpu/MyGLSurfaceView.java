package uhk.android_samples.lvl2advanced.p04target.p05gpgpu;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import uhk.android_samples.R;

public class MyGLSurfaceView extends GLSurfaceView {

    private uhk.android_samples.lvl2advanced.p04target.p05gpgpu.Renderer renderer;

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
    public void setRenderer(uhk.android_samples.lvl2advanced.p04target.p05gpgpu.Renderer renderer) {
        this.renderer = renderer;
        super.setRenderer(renderer);
    }

    /**
     * Listener for all buttons
     */
    public OnClickListener buttonListener = new OnClickListener() {
        public void onClick(View v) {
            final int id = v.getId();

            if(id == R.id.btn_save){
                renderer.init();
            }

        }
    };

}
