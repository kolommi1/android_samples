package uhk.android_samples.lvl1basic.p01start.p05multiple;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;

/**
 * Draw two different geometries with two different shader programs
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private Context context;

    private OGLBuffers buffers, buffers2;

    private int shaderProgram, shaderProgram2, locTime, locTime2;

    private float time = 0;

    Renderer(Context context, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // barva pozadí
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);



        //TODO: print text with OGLutils - render text to texture, render texture

        // shader files are in /assets/ directory - must be created
        // in android studio: right click module(app)->New->Folder->Assets Folder
        // in this project: android_samples\app\src\main\assets
        shaderProgram = ShaderUtils.loadProgram(context, maxGlEsVersion, "shaders/lvl1basic/p01start/p05multiple/start");
        shaderProgram2 = ShaderUtils.loadProgram(context, maxGlEsVersion, "shaders/lvl1basic/p01start/p05multiple/start2");
        createBuffers();


        // internal OpenGL ID of a shader uniform (constant during one draw call
        // - constant value for all processed vertices or pixels) variable
        locTime = GLES20.glGetUniformLocation(shaderProgram, "time");
        locTime2 = GLES20.glGetUniformLocation(shaderProgram2, "time");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // nastavení viewportu
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        time += 0.1;

        // set the current shader to be used
        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniform1f(locTime, time); // correct shader must be set before this

        // bind and draw
        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        // set the current shader to be used
        GLES20.glUseProgram(shaderProgram2);
        GLES20.glUniform1f(locTime2, time); // correct shader must be set before this

        // bind and draw
        buffers2.draw(GLES20.GL_TRIANGLES, shaderProgram2);

    }

    private void createBuffers(){
        float[] vertexBufferData = {
            -1, -1, 	0.7f, 0, 0,
            1,  0,		0, 0.7f, 0,
            0,  1,		0, 0, 0.7f
        };
        short[] indexBufferData = { 0, 1, 2 };

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2), // 2 floats
                new OGLBuffers.Attrib("inColor", 3) // 3 floats
        };
        buffers = new OGLBuffers(vertexBufferData, attributes,
                indexBufferData);


        float[] vertexBufferDataPos = {
                -1, 1,
                0.5f, 0,
                -0.5f, -1
        };
        float[] vertexBufferDataCol = {
                0, 1, 1,
                1, 0, 1,
                1, 1, 1
        };
        OGLBuffers.Attrib[] attributesPos = {
                new OGLBuffers.Attrib("inPosition", 2),
        };
        OGLBuffers.Attrib[] attributesCol = {
                new OGLBuffers.Attrib("inColor", 3)
        };

        buffers2 = new OGLBuffers( vertexBufferDataPos, attributesPos,
                indexBufferData);
        buffers2.addVertexBuffer(vertexBufferDataCol, attributesCol);

    }

}
