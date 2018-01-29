package uhk.android_samples.lvl1basic.p01start.p04utils;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;

/**
 * Read and compile shader from files "/shader/glsl01/start.*" using ShaderUtils
 * class in oglutils package
 * Manage (create, bind, draw) vertex and index buffers using OGLBuffers class
 * in oglutils package
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int[] vertexBuffer = new int[1], indexBuffer = new int[1];
    private int shaderProgram, locTime;
    private int maxGlEsVersion;
    private Context context;
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

        // shader files are in /assets/ directory - must be created
        // in android studio: right click module(app)->New->Folder->Assets Folder
        // in this project: android_samples\app\src\main\assets

        //TODO: print text with OGLutils - render text to texture, render texture

        ShaderUtils shaderUtils = new ShaderUtils(context, maxGlEsVersion);
        shaderProgram = shaderUtils.loadProgram("shaders/lvl1basic/p01start/p04utils/start.vert",
                        "shaders/lvl1basic/p01start/p04utils/start.frag",null);

        //shorter version of loading shader program
        //shaderProgram = shaderUtils.loadProgram("shaders/lvl1basic/p01start/p04utils/start");

        //TODO: manage buffers with OGLutils
        createBuffers();


        // internal OpenGL ID of a shader uniform (constant during one draw call
        // - constant value for all processed vertices or pixels) variable
        locTime = GLES20.glGetUniformLocation(shaderProgram, "time");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // nastavení viewportu
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // set the current shader to be used, could have been done only once (in
        // init) in this sample (only one shader used)
        GLES20.glUseProgram(shaderProgram);
        time += 0.1;
        GLES20.glUniform1f(locTime, time); // correct shader must be set before this
        bindBuffers();

        // draw - MODE(triangles), number of vertexes, type of vertexes, location of indices
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3, GLES20.GL_UNSIGNED_SHORT, 0);

        //unbind from the buffers
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void createBuffers(){
        // create and fill vertex buffer data
        float[] vertexBufferData = {
            -1, -1, 	0.7f, 0, 0,
            1,  0,		0, 0.7f, 0,
            0,  1,		0, 0, 0.7f
        };
        // create buffer required for sending data to a native library
        FloatBuffer vertexBufferBuffer = ByteBuffer.allocateDirect(vertexBufferData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBufferBuffer.put(vertexBufferData);
        vertexBufferBuffer.position(0);

        GLES20.glGenBuffers(1, vertexBuffer, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBufferData.length * 4,
                vertexBufferBuffer, GLES20.GL_STATIC_DRAW);

        // create and fill index buffer data (element buffer in OpenGL terminology)
        short[] indexBufferData = { 0, 1, 2 };
        // create buffer required for sending data to a native library
        ShortBuffer indexBufferBuffer = ByteBuffer.allocateDirect(indexBufferData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBufferBuffer.put(indexBufferData);
        indexBufferBuffer.position(0);

        GLES20.glGenBuffers(1, indexBuffer, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,indexBufferData.length * 2,
                indexBufferBuffer, GLES20.GL_STATIC_DRAW);
    }

    private void bindBuffers() {
        // internal OpenGL ID of a vertex shader input variable
        int locPosition = GLES20.glGetAttribLocation(shaderProgram, "inPosition");
        int locColor = GLES20.glGetAttribLocation(shaderProgram, "inColor");

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0]);
        GLES20.glEnableVertexAttribArray(locPosition);
        // shader variable ID, number of components, type of components,
        // normalize?, size of a vertex in bytes, location of first component
        GLES20.glVertexAttribPointer(locPosition, 2, GLES20.GL_FLOAT, false, 20, 0);
        GLES20.glEnableVertexAttribArray(locColor);
        // color information starts at 0 + 8(position) = 8
        GLES20.glVertexAttribPointer(locColor, 3, GLES20.GL_FLOAT, false, 20, 8);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);
    }

}
