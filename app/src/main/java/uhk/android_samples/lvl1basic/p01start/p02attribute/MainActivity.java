package uhk.android_samples.lvl1basic.p01start.p02attribute;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;

import uhk.android_samples.R;
import uhk.android_samples.lvl1basic.p00.p01buffer.*;

public class MainActivity extends Activity {

    private MyGLSurfaceView sample_GL_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sample_GL_View = new MyGLSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final int maxGlEsVersion = configurationInfo.reqGlEsVersion;
        final boolean supportEs3 = maxGlEsVersion >= 0x30000;
        final boolean supportEs2 = maxGlEsVersion >= 0x20000;

        if (supportEs3)
        {
            // Create OpenGL ES 3.0 context.
            sample_GL_View.setEGLContextClientVersion(3);

            // Set the renderer
            sample_GL_View.setRenderer(new Renderer(maxGlEsVersion));
        }
        else if (supportEs2)
        {
            // Create OpenGL ES 2.0 context.
            sample_GL_View.setEGLContextClientVersion(2);

            // Set the renderer
            sample_GL_View.setRenderer(new Renderer(maxGlEsVersion));
        }
        else
        {
            throw new RuntimeException("Device does not support OpenGL ES 2.0");
        }

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
