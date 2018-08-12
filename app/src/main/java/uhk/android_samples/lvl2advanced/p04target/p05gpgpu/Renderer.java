package uhk.android_samples.lvl2advanced.p04target.p05gpgpu;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLRenderTarget;
import uhk.android_samples.oglutils.OGLTexImageByte;
import uhk.android_samples.oglutils.OGLTexImageFloat;
import uhk.android_samples.oglutils.OGLTexture2D;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;
import uhk.android_samples.oglutils.ToFloatArray;
import uhk.android_samples.transforms.Camera;
import uhk.android_samples.transforms.Mat4;
import uhk.android_samples.transforms.Mat4Identity;
import uhk.android_samples.transforms.Vec3D;

/**
 * Load and apply texture using OGLTexture from oglutils package
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers;

    private int shaderProgram;

    private OGLTexture2D dataTexture;

    private OGLRenderTarget renderTarget;
    private OGLRenderTarget renderTarget2;
    private OGLRenderTarget renderTargetHlp;
    private OGLTexture2D.Viewer textureViewer;

    private boolean init = true;
    private OGLTexImageByte dataTexImage = null;
    private  int dataWidth = 512, dataHeight = 512;

    private Random random = new Random();

    Renderer(MainActivity activity, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);

        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion,
                "shaders/lvl2advanced/p04target/p05gpgpu/gpgpuRoll");
    //    shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion,  "shaders/lvl2advanced/p04target/p05gpgpu/gpgpuMax");

        createBuffers();
        initData();

        //two renderTargets, one for reading, one for rendering
        renderTarget = new OGLRenderTarget( dataWidth, dataHeight);
        renderTarget2 = new OGLRenderTarget( dataWidth, dataHeight);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        textureViewer = new OGLTexture2D.Viewer(maxGlEsVersion);
    }

    void initData() {
        dataTexImage = new OGLTexImageByte(dataWidth, dataHeight, 4);
        byte[] bytes = new byte[dataHeight*dataWidth];
        random.nextBytes(bytes);
        for (int i = 0; i < dataHeight; i++){
            for (int j = 0; j < dataWidth; j++) {
                dataTexImage.setPixel(j, i, 0, bytes[i*j]);
            }
            dataTexImage.setPixel(i, i, 0, (byte)0xff);
        }
        dataTexture = new OGLTexture2D(dataTexImage);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        // render to renderTarget, not on screen
        renderTarget.bind();

        GLES20.glUseProgram(shaderProgram);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT );

        if (init) {
            init = false;
            initData();
            dataTexture.bind(shaderProgram, "textureID", 0);
        }
        else{
            renderTarget2.getColorTexture().bind(shaderProgram, "textureID", 0);
        }

        buffers.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram);

        // result of previous drawing is used as texture for the next drawing
        renderTarget.getColorTexture().bind(shaderProgram, "textureID", 0);

        // default render target (screen)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, width, height);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        buffers.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram);

        // switch targets
        renderTargetHlp = renderTarget2;
        renderTarget2 = renderTarget;
        renderTarget = renderTargetHlp;

        //original
        textureViewer.view(dataTexture, -1, -1, 0.5, height / (double) width);
        //new
        textureViewer.view(renderTarget.getColorTexture(), -1, -0.5, 0.5, height / (double) width);

        activity.setViewText( "lvl2advanced\np04target\np05gpgpu");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;

        GLES20.glViewport(0, 0, width, height);
    }


    private void createBuffers() {
        // full-screen quad, just NDC positions are needed, texturing
        // coordinates can be calculated from them
        float[] triangleStrip = { 1, -1,
                1, 1,
                -1, -1,
                -1, 1 };

        OGLBuffers.Attrib[] attributesStrip = {
                new OGLBuffers.Attrib("inPosition", 2)};

        buffers = new OGLBuffers(triangleStrip, attributesStrip, null);
    }
    public void init(){
        init = true;;
    }
}
