package uhk.android_samples.lvl2advanced.p02debug;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLRenderTarget;
import uhk.android_samples.oglutils.OGLTexture2D;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;
import uhk.android_samples.oglutils.ToFloatArray;
import uhk.android_samples.transforms.Camera;
import uhk.android_samples.transforms.Mat4;
import uhk.android_samples.transforms.Mat4PerspRH;
import uhk.android_samples.transforms.Mat4Scale;
import uhk.android_samples.transforms.Vec3D;

/**
 * Debugging tools sample
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers;

    private int shaderProgram, locTime;

    private float time = 0;

    private long oldmils;
    private double fps = 0;

    enum DEBUGMODE {
        NONE,  //no special debug mode
        INDIVIDUAL // manually checking glError after calling GL method
    };
    private DEBUGMODE debugMode = DEBUGMODE.NONE;

    Renderer(MainActivity activity, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);
        //java parameters
        OGLUtils.printJAVAparameters();

        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl2advanced/p02debug/start");
        //shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl2advanced/p02debug/startError");

        createBuffers();

        locTime = GLES20.glGetUniformLocation(shaderProgram, "time");

        //ERROR - wrong constant of setting depth test, correct should be set by GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_DEPTH_ATTACHMENT);

        //checking GLErrors at the end of initialization, useful to leave it here
        if (debugMode == DEBUGMODE.INDIVIDUAL)
            OGLUtils.checkGLError("at the end of init: " + this.getClass().getName() + "." +
                    Thread.currentThread().getStackTrace()[1].getMethodName(), true);

    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        //frame per second calculation
        long mils = System.currentTimeMillis();
        if ((mils - oldmils)>0){
            fps = 1000 / (double)(mils - oldmils + 1);
            oldmils=mils;
        }
        Log.i("OGL", "Display method call, FPS = " + String.format("%3.1f", fps));

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        time += 0.1;

        //ERROR - wrong setting uniform variable, correct shader must be set before this
        GLES20.glUniform1f(locTime, time);
        //checking GLErrors
        if (debugMode == DEBUGMODE.INDIVIDUAL)
            OGLUtils.checkGLError("after setting uniform variable: " + this.getClass().getName() + "." +
                    Thread.currentThread().getStackTrace()[1].getMethodName(), true);


        //ERROR - wrong id of shader program
        //GLES20.glUseProgram(2);

        //checking GLErrors
        if (debugMode == DEBUGMODE.INDIVIDUAL)
            OGLUtils.checkGLError("after setting shader program: " + this.getClass().getName() + "." +
                    Thread.currentThread().getStackTrace()[1].getMethodName(), true);
        GLES20.glUseProgram(shaderProgram);

        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        time += 0.1;
        GLES20.glUniform1f(locTime, time);


        // bind and draw
        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);


        GLES20.glUseProgram(0);

        //checking GLErrors at the end of display method,
        //useful to check at least one per frame,
        //leave it here, end of display method
        if (debugMode == DEBUGMODE.INDIVIDUAL)
            OGLUtils.checkGLError("at the end of display: " + this.getClass().getName() + "." +
                    Thread.currentThread().getStackTrace()[1].getMethodName(), true);

        activity.setViewText( "lvl2advanced\np02debug");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;

        GLES20.glViewport(0, 0, width, height);
    }


    private void createBuffers() {
        float[] vertexBufferData = {
                -1, -1, 	0.7f, 0, 0,
                1,  0,		0, 0.7f, 0,
                0,  1,		0, 0, 0.7f
        };
        short[] indexBufferData = { 0, 1, 2 };

        // vertex binding description, concise version
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2), // 2 floats
                new OGLBuffers.Attrib("inColor", 3) // 3 floats
        };
        buffers = new OGLBuffers(vertexBufferData, attributes,
                indexBufferData);
        // the concise version requires attributes to be in this order within
        // vertex and to be exactly all floats within vertex

/*		full version for the case that some floats of the vertex are to be ignored
 * 		(in this case it is equivalent to the concise version):
 		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 2, 0), // 2 floats, at 0 floats from vertex start
				new OGLBuffers.Attrib("inColor", 3, 2) }; // 3 floats, at 2 floats from vertex start
		buffers = new OGLBuffers(vertexBufferData, 5, // 5 floats altogether in a vertex
				attributes, indexBufferData);
*/
    }

}
