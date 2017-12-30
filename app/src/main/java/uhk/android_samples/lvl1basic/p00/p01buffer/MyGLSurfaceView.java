package uhk.android_samples.lvl1basic.p00.p01buffer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {

    private uhk.android_samples.lvl1basic.p00.p01buffer.Renderer renderer;

    public MyGLSurfaceView(Context context){
        super(context);

        init(context);
        // Render the view only when there is a change in the drawing data
        //  setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new uhk.android_samples.lvl1basic.p00.p01buffer.Renderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

    }

}
