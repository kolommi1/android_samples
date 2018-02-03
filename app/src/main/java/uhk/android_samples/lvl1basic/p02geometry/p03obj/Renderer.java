package uhk.android_samples.lvl1basic.p02geometry.p03obj;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

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
 * Load and draw a geometry stored in a Wavefront OBJ file
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private Context context;
    private int width, height;

    private OGLBuffers buffers;
    private OGLModelOBJ model;

    private int shaderProgram, locMat;

    private Camera cam = new Camera();
    private Mat4 proj, swapYZ = new Mat4(new double[] {
                            1, 0, 0, 0,
                            0, 0, 1, 0,
                            0, 1, 0, 0,
                            0, 0, 0, 1,
    });

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
        shaderProgram = ShaderUtils.loadProgram(context, maxGlEsVersion, "shaders/lvl1basic/p02geometry/p03obj/ducky");
        //shaderProgram = ShaderUtils.loadProgram(context, maxGlEsVersion, "shaders/lvl1basic/p02geometry/p03obj/teapot");

        // obj files are in  /assets/objects/...
        model = new OGLModelOBJ(context, "objects/ducky.obj");
        //model = new OGLModelOBJ(context, "objects/Teapot.obj");
        //model= new OGLModelOBJ(context,"objects/ElephantBody.obj");
        //model= new OGLModelOBJ(context,"objects/TexturedCube.obj");

        buffers = model.getBuffers();

        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");

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
                ToFloatArray.convert(swapYZ.mul(cam.getViewMatrix()).mul(proj)), 0);

        buffers.draw(model.getTopology(), shaderProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;
        // nastavení viewportu
        GLES20.glViewport(0, 0, width, height);
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
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
