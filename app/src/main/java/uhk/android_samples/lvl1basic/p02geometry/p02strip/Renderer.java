package uhk.android_samples.lvl1basic.p02geometry.p02strip;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;
import uhk.android_samples.oglutils.ToFloatArray;
import uhk.android_samples.transforms.Camera;
import uhk.android_samples.transforms.Mat4;
import uhk.android_samples.transforms.Mat4PerspRH;
import uhk.android_samples.transforms.Vec3D;

/**
 * Draw 3D geometry, use camera and projection transformations
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private Context context;
    private int width, height;

    private OGLBuffers buffers, buffers2, buffers3;
    private int mode = 0;
    private boolean polygons = true;

    private int shaderProgram, locMat;

    private Camera cam = new Camera();
    private Mat4 proj; // created in onSurfaceChanged()

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
        shaderProgram = ShaderUtils.loadProgram(context, maxGlEsVersion, "shaders/lvl1basic/p02geometry/p02strip/simple");
        createBuffers();

        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");

        // on android we use custom GLSurfaceView to handle touch events (MyGLSurfaceView)
        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                        .withAzimuth(Math.PI * 1.25)
                        .withZenith(Math.PI * -0.125);

        //enable depth test
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    private void createBuffers(){
        // triangles defined in vertex buffer
        float[] strip = {
                // first triangle
                1, 0, 0,	0, 0, -1,
                0, 0, 0,	0, 0, -1,
                1, 1, 0,	0, 0, -1,
                // second triangle
                0, 0, 0,	0, -1, 0,
                1, 1, 0,	0, -1, 0,
                0, 1, 0,	0, -1, 0,
                // 3st triangle
                1, 1, 0,	-1, 0, 0,
                0, 1, 0,	-1, 0, 0,
                1, 2, 0,	-1, 0, 0,
                // 4th triangle
                0, 1, 0,	0, 1, 0,
                1, 2, 0,	0, 1, 0,
                0, 2, 0,	0, 1, 0,
                // 5th triangle
                1, 2, 0,	0, 0, 1,
                0, 2, 0,	0, 0, 1,
                1, 3, 0,	0, 0, 1,
                // 6th triangle
                0, 2, 0,	1, 0, 0,
                1, 3, 0,	1, 0, 0,
                0, 3, 0,	1, 1, 1,
        };

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        //create geometry without index buffer as the triangle list
        buffers = new OGLBuffers(strip, attributes, null);

        short[] indexBufferData = new short[9];
        for (int i = 0; i<9; i+=3){
            indexBufferData[i] = (short)(2*i);
            indexBufferData[i+1] = (short)(2*i+1);
            indexBufferData[i+2] = (short)(2*i+2);
        }
        //create geometry with index buffer as the triangle list
        buffers2 = new OGLBuffers(strip, attributes, indexBufferData);

        short[] indexBufferData2 = {0,1,2,5,8,11,14,17};
        //create geometry with index buffer as the triangle strip
        buffers3 = new OGLBuffers(strip, attributes, indexBufferData2);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);

        switch(mode % 9){
            case 0:
                if (polygons){
                    buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);
                }
                else{
                    buffers.draw(GLES20.GL_LINES, shaderProgram);
                }
                break;
            case 1:
                if (polygons){
                    //number of vertices
                    buffers.draw(GLES20.GL_TRIANGLES, shaderProgram, 9);
                }
                else{
                    buffers.draw(GLES20.GL_LINES, shaderProgram, 9);
                }
                break;
            case 2:
                if (polygons){
                    //number of vertices, index of the first vertex
                    buffers.draw(GLES20.GL_TRIANGLES, shaderProgram, 9, 6);
                }
                else{
                    buffers.draw(GLES20.GL_LINES, shaderProgram, 9, 6);
                }
                break;
            case 3:
                if (polygons){
                    buffers2.draw(GLES20.GL_TRIANGLES, shaderProgram);
                }
                else{
                    buffers.draw(GLES20.GL_LINES, shaderProgram);
                }
                break;
            case 4:
                if (polygons){
                    //number of vertices
                    buffers2.draw(GLES20.GL_TRIANGLES, shaderProgram, 6);
                }
                else{
                    buffers.draw(GLES20.GL_LINES, shaderProgram, 6);
                }
                break;
            case 5:
                if (polygons){
                    //number of vertices, index of the first vertex
                    buffers2.draw(GLES20.GL_TRIANGLES, shaderProgram, 6, 3);
                }
                else{
                    buffers.draw(GLES20.GL_LINES, shaderProgram, 6, 3);
                }
                break;
            case 6:
                if (polygons){
                    buffers3.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram);
                }
                else{
                    buffers.draw(GLES20.GL_LINE_STRIP, shaderProgram);
                }
                break;
            case 7:
                if (polygons){
                    //number of vertices
                    buffers3.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram, 5);
                }
                else{
                    buffers.draw(GLES20.GL_LINE_STRIP, shaderProgram, 5);
                }
                break;
            case 8:
                if (polygons){
                    //number of vertices, index of the first vertex
                    buffers3.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram, 4, 2);
                }
                else{
                    buffers.draw(GLES20.GL_LINE_STRIP, shaderProgram, 4, 2);
                }
                break;
        }

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

    public void changeMode(){
        mode++;
    }

    public void togglePolygons(){
        polygons = !polygons;
    }

}
