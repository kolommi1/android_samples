package uhk.android_samples.lvl1basic.p01start.p04utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.widget.TextView;

import uhk.android_samples.R;

public class MainActivity extends Activity {

    private MyGLSurfaceView sample_GL_View;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        sample_GL_View = findViewById(R.id.mySurfaceView);
        textView = findViewById(R.id.textView);

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
            sample_GL_View.setRenderer(new Renderer(this, maxGlEsVersion));
        }
        else if (supportEs2)
        {
            // Create OpenGL ES 2.0 context.
            sample_GL_View.setEGLContextClientVersion(2);

            // Set the renderer
            sample_GL_View.setRenderer(new Renderer(this, maxGlEsVersion));
        }
        else
        {
            throw new RuntimeException("Device does not support OpenGL ES 2.0");
        }

    }

    public void setViewText(String text){
        this.runOnUiThread(new Runnable() {
            public void run() {
                textView.setText(text);
            }
        });
    }

    public void appendViewText(String text){
        this.runOnUiThread(new Runnable() {
            public void run() {
                textView.append(text);
            }
        });
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
