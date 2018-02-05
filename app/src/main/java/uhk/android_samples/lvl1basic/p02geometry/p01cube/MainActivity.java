package uhk.android_samples.lvl1basic.p02geometry.p01cube;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
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

        addButtonsToRelativeLayout(findViewById(R.id.mainLayout));

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

    private void addButtonsToRelativeLayout(RelativeLayout layout ){
        //Button down
        Button downButton = new Button(this);
        downButton.setText("\\/");
        // file: /src/main/res/values/ids.xml - help file - can be filled with needed ids
        downButton.setId(R.id.btn_down);
        // set parameters for xml file
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        downButton.setLayoutParams(params);
        // attach listener
        downButton.setOnClickListener(sample_GL_View.buttonListener);
        layout.addView(downButton);


        //Button left
        Button leftButton = new Button(this);
        leftButton.setText("<");
        leftButton.setId(R.id.btn_left);

        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.LEFT_OF, R.id.btn_down);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        leftButton.setLayoutParams(params);

        leftButton.setOnClickListener(sample_GL_View.buttonListener);
        layout.addView(leftButton);


        //Button right
        Button rightButton = new Button(this);
        rightButton.setText(">");
        rightButton.setId(R.id.btn_right);

        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.btn_down);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rightButton.setLayoutParams(params);

        rightButton.setOnClickListener(sample_GL_View.buttonListener);
        layout.addView(rightButton);


        //Button up
        Button upButton = new Button(this);
        upButton.setText("/\\");
        upButton.setId(R.id.btn_up);

        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.btn_down);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        upButton.setLayoutParams(params);

        upButton.setOnClickListener(sample_GL_View.buttonListener);
        layout.addView(upButton);


        //Button plus
        Button plusButton = new Button(this);
        plusButton.setText("+");
        plusButton.setId(R.id.btn_plus);

        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        plusButton.setLayoutParams(params);

        plusButton.setOnClickListener(sample_GL_View.buttonListener);
        layout.addView(plusButton);

        //Button plus
        Button minusButton = new Button(this);
        minusButton.setText("-");
        minusButton.setId(R.id.btn_minus);

        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.BELOW, R.id.btn_plus);
        minusButton.setLayoutParams(params);

        minusButton.setOnClickListener(sample_GL_View.buttonListener);
        layout.addView(minusButton);

        //Button First person
        Button perspectiveButton = new Button(this);
        perspectiveButton.setText(R.string.btn_first);
        perspectiveButton.setId(R.id.btn_firstPerson);

        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        perspectiveButton.setLayoutParams(params);

        perspectiveButton.setOnClickListener(sample_GL_View.buttonListener);
        layout.addView(perspectiveButton);
    }
}
