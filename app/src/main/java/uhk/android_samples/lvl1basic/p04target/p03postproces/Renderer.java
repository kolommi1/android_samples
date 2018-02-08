package uhk.android_samples.lvl1basic.p04target.p03postproces;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

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
 * Use render to texture to perform rendering post-processing
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers, bufferStrip;
    private OGLTexture2D texture;

    private int shaderProgram, shaderProgramPost, locMat;

    private Camera cam = new Camera();
    private Mat4 proj;

    private OGLRenderTarget renderTarget;
    private boolean save = false;
    private OGLTexture2D.Viewer textureViewer;

    Renderer(MainActivity activity, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);

        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl1basic/p04target/p03postproces/texture");
        shaderProgramPost = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl1basic/p04target/p03postproces/postBlur");
        //shaderProgramPost = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl1basic/p04target/p03postproces/postGrey");

        createBuffers();

        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");

        texture = new OGLTexture2D(activity, "textures/mosaic.jpg");

        renderTarget = new OGLRenderTarget( 500, 500);

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                        .withAzimuth(Math.PI * 1.25)
                        .withZenith(Math.PI * -0.125);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        textureViewer = new OGLTexture2D.Viewer(maxGlEsVersion);
    }



    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glUseProgram(shaderProgram);

        // set our render target (texture)
        renderTarget.bind();
        GLES20.glClearColor(0.1f, 0.2f, 0.3f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        texture.bind(shaderProgram, "textureID", 0);

        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj).mul(new Mat4Scale((double)width / height, 1, 1))), 0);
        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        GLES20.glUseProgram(shaderProgramPost);

        // set the default render target (screen)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        // reset viewport but fit the aspect ratio of our render target (1:1)
        // best into the window
        if ((double)width/height > 1.0)
            GLES20.glViewport(0, 0, height, height);
        else
            GLES20.glViewport(0, 0, width, width);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // use the result of the previous draw as a texture for the next
        renderTarget.getColorTexture().bind(shaderProgramPost, "textureID", 0);

        // draw the full-screen quad
        bufferStrip.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgramPost);

        textureViewer.view(renderTarget.getColorTexture(), -1, -1, 0.5, 1);

        activity.setViewText( "lvl1basic\np04target\np03postproces");
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

        // full-screen quad, just NDC positions are needed, texturing
        // coordinates can be calculated from them
        float[] triangleStrip = { 1, -1,
                1, 1,
                -1, -1,
                -1, 1 };

        OGLBuffers.Attrib[] attributesStrip = {
                new OGLBuffers.Attrib("inPosition", 2)};

        bufferStrip = new OGLBuffers(triangleStrip, attributesStrip, null);
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
