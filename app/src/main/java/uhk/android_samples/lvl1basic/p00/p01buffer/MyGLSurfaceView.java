package uhk.android_samples.lvl1basic.p00.p01buffer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {

    private uhk.android_samples.lvl1basic.p00.p01buffer.Renderer renderer;

    public MyGLSurfaceView(Context context){
        super(context);

        // Render the view only when there is a change in the drawing data
        //  setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
