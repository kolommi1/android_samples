package uhk.android_samples.lvl2advanced.p04target.p01save;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.R;
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
import uhk.android_samples.transforms.Mat4PerspRH;
import uhk.android_samples.transforms.Mat4Scale;
import uhk.android_samples.transforms.Vec3D;

/**
 *   use renderTargetu for color and depth storage, save texture to file
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers;
    private OGLTexture2D texture, textureColor, textureDepth;
    private OGLTexture2D.Viewer textureViewer;
    private OGLRenderTarget renderTarget;

    private int shaderProgram, locMat;

    private Camera cam = new Camera();
    private Mat4 proj;
    private boolean saved = false;

    Renderer(MainActivity activity, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);

        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl2advanced/p04target/p01save/texture");
        createBuffers();
        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");

        texture = new OGLTexture2D(activity, "textures/bricks.jpg");
        renderTarget = new OGLRenderTarget(500, 500);

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                        .withAzimuth(Math.PI * 1.25)
                        .withZenith(Math.PI * -0.125);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        textureViewer = new OGLTexture2D.Viewer(maxGlEsVersion);
    }
    private void loadImageFromStorage(String dir)
    {
        try {
            Log.i("OGL","opening texture " + dir+ "/screen.png");
            File file = new File(dir, "screen.png");
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            textureColor = new OGLTexture2D(bitmap);
            Log.i("OGL","OK");
        }
        catch (FileNotFoundException e)
        {
            Log.i("OGL","FAILED");
            Log.e("OGL", e.getMessage());
            e.printStackTrace();
        }

        try {

            Log.i("OGL","opening texture " + dir+ "/screenZ.png");
            File file = new File(dir, "screenZ.png");

            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
            textureDepth = new OGLTexture2D(b);
            Log.i("OGL","OK");
        }
        catch (FileNotFoundException e)
        {
            Log.i("OGL","FAILED");
            Log.e("OGL", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glUseProgram(shaderProgram);

        // bind render target
        renderTarget.bind();

        GLES20.glClearColor(0.1f, 0.2f, 0.3f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        texture.bind(shaderProgram, "textureID", 0);

        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)
                        .mul(new Mat4Scale((double) width / height, 1, 1))), 0);
        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        // retrieve texture context
        textureColor = renderTarget.getColorTexture();
        textureDepth = renderTarget.getDepthTexture();

        if (!saved) {

            saved = true;
            // save texture to file

            // load texture data
            OGLTexImageByte imgByte = textureColor.getTexImage(new OGLTexImageByte.Format(4));
            // save RGBA texture to a PNG file
            imgByte.save(activity,  "screen.png");

            imgByte = textureDepth.getTexImage(new OGLTexImageByte.Format(4));
            imgByte.save(activity, "screenZ.png");

            //load saved files
         /*   File directory = activity.getDir("OGLimages", Context.MODE_PRIVATE);
            loadImageFromStorage(directory.getAbsolutePath());*/

        }

        // change to default frame buffer (screen)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, width, height);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //use the result of the previous draw as a texture for the next
        //renderTarget.bindColorTexture(shaderProgram, "textureID", 0);
        renderTarget.getColorTexture().bind(shaderProgram, "textureID", 0);
        //renderTarget.bindDepthTexture(shaderProgram, "textureID", 0);

        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);

        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        GLES20.glUseProgram(0); // no shader
        //draw textures
        textureViewer.view(textureColor, -1, 0, 0.5, height / (double) width);
        textureViewer.view(textureDepth, -1, -1, 0.5, height / (double) width);

        activity.setViewText( "lvl1basic\np04target\np01save");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;

        GLES20.glViewport(0, 0, width, height);
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 1.0, 100.0);
    }


    private void createBuffers() {

        float[] cube = {
                // bottom (z-) face
                1, 0, 0,	0, 0, -1,
                0, 0, 0,	0, 0, -1,
                1, 1, 0,	0, 0, -1,
                0, 1, 0,	0, 0, -1,
                // top (z+) face
                1, 0, 1,	0, 0, 1,
                0, 0, 1,	0, 0, 1,
                1, 1, 1,	0, 0, 1,
                0, 1, 1,	0, 0, 1,
                // x+ face
                1, 1, 0,	1, 0, 0,
                1, 0, 0,	1, 0, 0,
                1, 1, 1,	1, 0, 0,
                1, 0, 1,	1, 0, 0,
                // x- face
                0, 1, 0,	-1, 0, 0,
                0, 0, 0,	-1, 0, 0,
                0, 1, 1,	-1, 0, 0,
                0, 0, 1,	-1, 0, 0,
                // y+ face
                1, 1, 0,	0, 1, 0,
                0, 1, 0,	0, 1, 0,
                1, 1, 1,	0, 1, 0,
                0, 1, 1,	0, 1, 0,
                // y- face
                1, 0, 0,	0, -1, 0,
                0, 0, 0,	0, -1, 0,
                1, 0, 1,	0, -1, 0,
                0, 0, 1,	0, -1, 0
        };

        short[] indexBufferData = new short[36];
        for (int i = 0; i<6; i++){
            indexBufferData[i*6] = (short)(i*4);
            indexBufferData[i*6 + 1] =(short)(i*4 + 1);
            indexBufferData[i*6 + 2] =(short)(i*4 + 2);
            indexBufferData[i*6 + 3] =(short)(i*4 + 1);
            indexBufferData[i*6 + 4] =(short)(i*4 + 2);
            indexBufferData[i*6 + 5] =(short)(i*4 + 3);
        }
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        buffers = new OGLBuffers(cube, attributes, indexBufferData);
    }

    /**
     * @param tx coordinate X from touchEvent
     * @param ty coordinate Y from touchEvent
     * @param ox coordinate X from previous touchEvent
     * @param oy coordinate Y from previous touchEvent
     */
    public void rotateCamera(float tx,float ty,float ox,float oy){
       cam = cam.addAzimuth((double) Math.PI * (ox - tx) / width)
               .addZenith((double) -Math.PI * (ty - oy) / height);
    }


    public void moveCameraForward(){
        cam = cam.forward(1);
    }
    public void moveCameraBack(){
        cam = cam.backward(1);
    }
    public void moveCameraLeft(){
        cam = cam.left(1);
    }
    public void moveCameraRight(){
        cam = cam.right(1);
    }

    public void cameraChangeRadius(float radiusMultiplier){
        cam = cam.mulRadius(radiusMultiplier);
    }

    public void cameraToggleFirstPerson(){
        cam = cam.withFirstPerson(!cam.getFirstPerson());
    }

    public void saveTexture(){
        saved = false;
    }
}
