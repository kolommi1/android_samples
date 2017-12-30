package uhk.android_samples.lvl1basic.p00.p01buffer;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uhk.android_samples.R;

public class MainActivity extends Activity {

    private MyGLSurfaceView sample_GL_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sample_GL_View = new MyGLSurfaceView(this);
        setContentView(sample_GL_View);

    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        sample_GL_View.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        sample_GL_View.onPause();
    }
}
