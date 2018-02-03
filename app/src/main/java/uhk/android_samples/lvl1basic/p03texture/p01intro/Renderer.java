package uhk.android_samples.lvl1basic.p03texture.p01intro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLModelOBJ;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;
import uhk.android_samples.oglutils.ToFloatArray;
import uhk.android_samples.transforms.Camera;
import uhk.android_samples.transforms.Mat4;
import uhk.android_samples.transforms.Mat4PerspRH;
import uhk.android_samples.transforms.Vec3D;

/**
 *  Load texture and apply it to cube faces
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private Context context;
    private int width, height;

    private OGLBuffers buffers;

    private int shaderProgram, texture, locMat;

    private Camera cam = new Camera();
    private Mat4 proj;

    Renderer(Context context, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);

        //TODO: print text with OGLutils - render text to texture, render texture

        // shader files are in /assets/ directory - must be created
        // in android studio: right click module(app)->New->Folder->Assets Folder
        // in this project: android_samples\app\src\main\assets
        ShaderUtils shaderUtils = new ShaderUtils(context, maxGlEsVersion);
        if (OGLUtils.getVersionGLSL(maxGlEsVersion)<300)
            shaderProgram = shaderUtils.loadProgram( "shaders/lvl1basic/p03texture/p01intro/textureOld");
        else
            shaderProgram = shaderUtils.loadProgram( "shaders/lvl1basic/p03texture/p01intro/texture");

        createBuffers();

        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");

        // load texture
        // texture files are in /assets/textures/

        Log.i("Textures","Loading texture...");
        texture = loadTexture("textures/mosaic.jpg");

        if(texture != 0){
            Log.i("Textures","ok");
        }
        else {
            Log.i("Textures","failed");
        }

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                        .withAzimuth(Math.PI * 1.25)
                        .withZenith(Math.PI * -0.125);

        //enable depth test
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }


    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);

        bindTexture();
        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;
        // nastavení viewportu
        GLES20.glViewport(0, 0, width, height);
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
    }


    void createBuffers() {
        // vertices are not shared among triangles (and thus faces) so each face
        // can have a correct normal in all vertices
        // also because of this, the vertices can be directly drawn as GL_TRIANGLES
        // (three and three vertices form one face)
        // triangles defined in index buffer
        float[] cube = {
                // bottom (z-) face
                1, 0, 0,	0, 0, -1, 	1, 0,
                0, 0, 0,	0, 0, -1,	0, 0,
                1, 1, 0,	0, 0, -1,	1, 1,
                0, 1, 0,	0, 0, -1,	0, 1,
                // top (z+) face
                1, 0, 1,	0, 0, 1,	1, 0,
                0, 0, 1,	0, 0, 1,	0, 0,
                1, 1, 1,	0, 0, 1,	1, 1,
                0, 1, 1,	0, 0, 1,	0, 1,
                // x+ face
                1, 1, 0,	1, 0, 0,	1, 0,
                1, 0, 0,	1, 0, 0,	0, 0,
                1, 1, 1,	1, 0, 0,	1, 1,
                1, 0, 1,	1, 0, 0,	0, 1,
                // x- face
                0, 1, 0,	-1, 0, 0,	1, 0,
                0, 0, 0,	-1, 0, 0,	0, 0,
                0, 1, 1,	-1, 0, 0,	1, 1,
                0, 0, 1,	-1, 0, 0,	0, 1,
                // y+ face
                1, 1, 0,	0, 1, 0,	1, 0,
                0, 1, 0,	0, 1, 0,	0, 0,
                1, 1, 1,	0, 1, 0,	1, 1,
                0, 1, 1,	0, 1, 0,	0, 1,
                // y- face
                1, 0, 0,	0, -1, 0,	1, 0,
                0, 0, 0,	0, -1, 0,	0, 0,
                1, 0, 1,	0, -1, 0,	1, 1,
                0, 0, 1,	0, -1, 0,	0, 1
        };

        short[] indexBufferData = new short[36];
        for (int i = 0; i<6; i++){
            indexBufferData[i*6] = (short)(i*4);
            indexBufferData[i*6 + 1] = (short)(i*4 + 1);
            indexBufferData[i*6 + 2] = (short)(i*4 + 2);
            indexBufferData[i*6 + 3] = (short)(i*4 + 1);
            indexBufferData[i*6 + 4] = (short)(i*4 + 2);
            indexBufferData[i*6 + 5] = (short)(i*4 + 3);
        }


        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3),
                new OGLBuffers.Attrib("inTextureCoordinates", 2)
        };

        buffers = new OGLBuffers(cube, attributes, indexBufferData);
    }

    void bindTexture() {
        // bind texture to a shader uniform variable via a texture slot 0
        // first bind texture to slot 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); // slot 0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        // then bind shader variable to slot 0
        int locTexture = GLES20.glGetUniformLocation(shaderProgram, "textureID");
        GLES20.glUniform1i(locTexture, 0); // slot 0
        // these steps can be performed independently (e.g. bind another texture
        // to slot 0 without rebinding shader variable)
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

    public int loadTexture(final String fileName)
    {

        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
        } catch (IOException e) {
            Log.e("Textures","File not found ");
            e.printStackTrace();
            return 0;
        }
        if (is == null) {
            Log.e("Textures","File not found ");
            return 0;
        }
        // texture handler
        final int[] textureHandler = new int[1];
        GLES20.glGenTextures(1, textureHandler, 0);

        if (textureHandler[0] != 0) {

            // load picture data from a file and decode them to Bitmap
            final Bitmap bitmap = BitmapFactory.decodeStream(is);

            //bind handler to OpenGl texture - following OpenGL calls will affect this texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandler[0]);

            // set texture filtering parameters
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // load bitmap to OpenGl texture
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // generate mipmaps
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

            // data from bitmap has been loaded to OpenGL, bitmap memory can be released
            bitmap.recycle();
        }

        if (textureHandler[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandler[0];
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

}
