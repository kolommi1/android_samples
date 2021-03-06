package uhk.android_samples.lvl1basic.p03texture.p03multiple;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLTexture2D;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;
import uhk.android_samples.oglutils.ToFloatArray;
import uhk.android_samples.transforms.Camera;
import uhk.android_samples.transforms.Mat4;
import uhk.android_samples.transforms.Mat4PerspRH;
import uhk.android_samples.transforms.Vec3D;

/**
 *  Load and apply two different texture on one object using OGLTexture from oglutils package
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers;
    private OGLTexture2D texture, texture2;
    private OGLTexture2D.Viewer textureViewer;

    private int shaderProgram, locMat, locHeight;

    private Camera cam = new Camera();
    private Mat4 proj;

    Renderer(MainActivity activity, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);

        // shader files are in /assets/ directory - must be created
        // in android studio: right click module(app)->New->Folder->Assets Folder
        // in this project: android_samples\app\src\main\assets
        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl1basic/p03texture/p03multiple/texture");

        createBuffers();

        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");
        locHeight = GLES20.glGetUniformLocation(shaderProgram, "height");

        // load texture
        // texture files are in /assets/textures/

        Log.i("Textures","Loading texture...");
        texture = new OGLTexture2D(activity, "textures/mosaic.jpg");
        texture2 = new OGLTexture2D(activity, "textures/testTexture.jpg");

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                        .withAzimuth(Math.PI * 1.25)
                        .withZenith(Math.PI * -0.125);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        textureViewer = new OGLTexture2D.Viewer(maxGlEsVersion);
    }


    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);
        GLES20.glUniform1f(locHeight, height);

        texture.bind(shaderProgram, "textureID1", 0);
        texture2.bind(shaderProgram, "textureID2", 1);

        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);
        textureViewer.view(texture, -1, -0.5, 0.5);
        textureViewer.view(texture2, -1, -1, 0.5);

        activity.setViewText( "lvl1basic\np03texture\np03multiple");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;

        GLES20.glViewport(0, 0, width, height);
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
    }


    private void createBuffers() {
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

}
