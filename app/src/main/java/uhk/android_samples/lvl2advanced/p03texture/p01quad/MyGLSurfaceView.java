package uhk.android_samples.lvl2advanced.p03texture.p01quad;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {

    private uhk.android_samples.lvl2advanced.p03texture.p01quad.Renderer renderer;

    public MyGLSurfaceView(Context context){
        super(context);

        // Render the view only when there is a change in the drawing data
        //  setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Hide superclass method to access renderer.
    public void setRenderer(uhk.android_samples.lvl2advanced.p03texture.p01quad.Renderer renderer) {
        this.renderer = renderer;
        super.setRenderer(renderer);
    }


}
