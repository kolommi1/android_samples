package uhk.android_samples.lvl1basic.p02geometry.p02strip;

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
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers, buffers2, buffers3, buffers4, buffers5;
    private int mode = 0;
    private boolean polygons = true;

    private int shaderProgram, locMat;

    private Camera cam = new Camera();
    private Mat4 proj; // created in onSurfaceChanged()

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
        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl1basic/p02geometry/p02strip/simple");
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
                // 3rd triangle
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

        short[] indexBufferData3 = new short[18];
        for (int i = 0; i<9; i+=3){
            indexBufferData3[i*2] = (short)(2*i);      indexBufferData3[i*2+1] = (short)(2*i+1);
            indexBufferData3[i*2+2] = (short)(2*i+1);  indexBufferData3[i*2+3] = (short)(2*i+2);
            indexBufferData3[i*2+4] = (short)(2*i+2);  indexBufferData3[i*2+5] = (short)(2*i);
        }
        //create geometry with index buffer as the line list
        buffers4 = new OGLBuffers(strip, attributes, indexBufferData3);

        short[] indexBufferData4 = {1,0,2,1,5,2,8,5,11,8,14,11,17,14};
        //create geometry with index buffer as the line strip
        buffers5 = new OGLBuffers(strip, attributes, indexBufferData4);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);
        activity.setViewText( "lvl1basic\np02geometry\np02strip\n");
        if (polygons){
            activity.appendViewText("polygon: fill\n");
        }
        else{
            activity.appendViewText("polygon: line\n");
        }
        switch(mode % 9){
            case 0:
                activity.appendViewText("mode: all triangles of triangle list,\n without index buffer");
                if (polygons){
                    buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);
                }
                else{
                    buffers.draw(GLES20.GL_LINES, shaderProgram);
                }
                break;
            case 1:
                activity.appendViewText("mode: first 3 triangles of triangle list,\n without index buffer");
                if (polygons){
                    //number of vertices
                    buffers.draw(GLES20.GL_TRIANGLES, shaderProgram, 9);
                }
                else{
                    buffers.draw(GLES20.GL_LINES, shaderProgram, 9);
                }
                break;
            case 2:
                activity.appendViewText( "mode: 3rd, 4th and 5th triangles of triangle list,\n without index buffer");
                if (polygons){
                    //number of vertices, index of the first vertex
                    buffers.draw(GLES20.GL_TRIANGLES, shaderProgram, 9, 6);
                }
                else{
                    buffers.draw(GLES20.GL_LINES, shaderProgram, 9, 6);
                }
                break;
            case 3:
                activity.appendViewText("mode: odd triangles of triangle list,\n with defined index buffer");
                if (polygons){
                    buffers2.draw(GLES20.GL_TRIANGLES, shaderProgram);
                }
                else{
                    buffers4.draw(GLES20.GL_LINES, shaderProgram);
                }
                break;
            case 4:
                activity.appendViewText("mode: 1st and 2nd odd triangles of triangle list,\n with defined index buffer");
                if (polygons){
                    //number of vertices
                    buffers2.draw(GLES20.GL_TRIANGLES, shaderProgram, 6);
                }
                else{
                    buffers4.draw(GLES20.GL_LINES, shaderProgram, 12);
                }
                break;
            case 5:
                activity.appendViewText("mode: 2nd and 3rd odd triangles of triangle list,\n with defined index buffer");
                if (polygons){
                    //number of vertices, index of the first vertex
                    buffers2.draw(GLES20.GL_TRIANGLES, shaderProgram, 6, 3);
                }
                else{
                    buffers4.draw(GLES20.GL_LINES, shaderProgram, 12, 6);
                }
                break;
            case 6:
                activity.appendViewText("mode: all triangles of triangle strip,\n with defined index buffer");
                if (polygons){
                    buffers3.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram);
                }
                else{
                    buffers5.draw(GLES20.GL_LINE_STRIP, shaderProgram);
                }
                break;
            case 7:
                activity.appendViewText("mode: first 3 triangles of triangle strip,\n with defined index buffer");
                if (polygons){
                    //number of vertices
                    buffers3.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram, 5);
                }
                else{
                    buffers5.draw(GLES20.GL_LINE_STRIP, shaderProgram, 8);
                }
                break;
            case 8:
                activity.appendViewText("mode: 3rd and 4th triangles of triangle strip,\n with defined index buffer");
                if (polygons){
                    //number of vertices, index of the first vertex
                    buffers3.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram, 4, 2);
                }
                else{
                    buffers5.draw(GLES20.GL_LINE_STRIP, shaderProgram, 6, 4);
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
