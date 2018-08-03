package uhk.android_samples.lvl2advanced.p03texture.p02cubetexture;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;

import uhk.android_samples.oglutils.OGLTextureCube;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;
import uhk.android_samples.oglutils.ToFloatArray;
import uhk.android_samples.transforms.Camera;
import uhk.android_samples.transforms.Mat4;
import uhk.android_samples.transforms.Mat4PerspRH;
import uhk.android_samples.transforms.Vec3D;

/**
 * Load and apply cube texture using OGLTextureCube from oglutils package
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers;

    private int shaderProgram, locMat;

    private OGLTextureCube texture;

    private Camera cam = new Camera();
    private Mat4 proj;
    private Mat4 swapYZflipZ = new Mat4(new double[] {
            1, 0, 0, 0,
            0, 0, -1, 0,
            0, 1, 0, 0,
            0, 0, 1, 1,
    });
    private OGLTextureCube.Viewer textureViewer;

    Renderer(MainActivity activity, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);

        // shader files are in /shaders/ directory
        // shaders directory must be set as a source directory of the project
        // e.g. in Eclipse via main menu Project/Properties/Java Build
        // Path/Source
        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl2advanced/p03texture/p02cubetexture/textureCube");

        createBuffers();

        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");
        // texture files are in /res/textures/
        // texture = new OGLTextureCube(activity, "textures/skyBox_.jpg",OGLTextureCube.SUFFICES_RIGHT_LEFT);
        // texture = new OGLTextureCube(activity, "textures/snow_.jpg",OGLTextureCube.SUFFICES_POSITIVE_NEGATIVE);
        String[] names = {"textures/snow_positive_x.jpg",
                "textures/snow_negative_x.jpg",
                "textures/snow_negative_y.jpg",
                "textures/snow_positive_y.jpg",
                "textures/snow_positive_z.jpg",
                "textures/snow_negative_z.jpg"};

        texture = new OGLTextureCube(activity, names);

        cam = cam.withPosition(new Vec3D(0.5, 0.5, 0.5));

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        textureViewer = new OGLTextureCube.Viewer(maxGlEsVersion);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(swapYZflipZ.mul(cam.getViewMatrix()).mul(proj)), 0);

        texture.bind(shaderProgram, "textureID", 0);

        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        textureViewer.view(texture);

        activity.setViewText( "lvl2advanced\np03texture\np02cubetexture");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;
        proj = new Mat4PerspRH(Math.PI / 3, height / (double) width, 0.01, 1000.0);
        GLES20.glViewport(0, 0, width, height);
    }


    private void createBuffers() {
        // vertices are not shared among triangles (and thus faces) so each face
        // can have a correct normal in all vertices
        // also because of this, the vertices can be directly drawn as GL_TRIANGLES
        // (three and three vertices form one face)
        // triangles defined in index buffer
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
            indexBufferData[i*6] =(short)( i*4);
            indexBufferData[i*6 + 1] = (short)(i*4 + 1);
            indexBufferData[i*6 + 2] = (short)(i*4 + 2);
            indexBufferData[i*6 + 3] = (short)(i*4 + 1);
            indexBufferData[i*6 + 4] = (short)(i*4 + 2);
            indexBufferData[i*6 + 5] = (short)(i*4 + 3);
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

}
