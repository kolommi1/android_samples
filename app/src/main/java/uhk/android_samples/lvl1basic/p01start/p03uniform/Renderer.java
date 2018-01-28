package uhk.android_samples.lvl1basic.p01start.p03uniform;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLUtils;

/**
 * Sending a uniform variable to shader
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int[] vertexBuffer = new int[1], indexBuffer = new int[1];
    private int shaderProgram, locTime;
    private int maxGlEsVersion;
    private float time = 0;
    private static final String TAG = "android_samples uniform";

    Renderer(int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // barva pozadí
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);

        createBuffers();
        createShaders();

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

    private void createShaders(){
        String shaderVertSrc =
                "attribute vec2 inPosition;" + // input from the vertex buffer
                "attribute vec3 inColor;" + // input from the vertex buffer
                "varying vec3 vertColor;" + // output from this shader to the next pipeline stage
                "uniform float time;" + // variable constant for all vertices in a single draw
                "void main() {" +
                "   vec2 position = inPosition;"+
                "   position.y += cos(position.x + time);" +
                " 	gl_Position = vec4(position, 0.0, 1.0);" +
                "   vertColor = inColor;"+
                "}"
        ;
        // gl_Position - built-in vertex shader output variable containing
        // vertex position before w-clipping and dehomogenization, must be
        // filled

        String shaderFragSrc =
                "precision mediump float;" +
                "varying vec3 vertColor;"+ // input from the previous pipeline stage
                "void main() {" +
                " 	gl_FragColor = vec4(vertColor, 1.0);" + //output
                "}"
        ;
        // gl_FragColor - built-in output from fragment shader

        // vertex shader - create and compile
        int vs = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (vs != 0) {
            GLES20.glShaderSource(vs, shaderVertSrc);
            GLES20.glCompileShader(vs);

            int[] compiled = new int[1];
            GLES20.glGetShaderiv(vs, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + GLES20.GL_VERTEX_SHADER + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(vs));
                GLES20.glDeleteShader(vs);
                vs = 0;
            }
        }

        // fragment shader - create and compile
        int fs = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (fs != 0) {
            GLES20.glShaderSource(fs,  shaderFragSrc);
            GLES20.glCompileShader(fs);

            int[] compiled = new int[1];
            GLES20.glGetShaderiv(fs, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + GLES20.GL_FRAGMENT_SHADER + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(fs));
                GLES20.glDeleteShader(fs);
                fs = 0;
            }
        }

        // create and link program
        shaderProgram = GLES20.glCreateProgram();
        if (shaderProgram != 0) {
            GLES20.glAttachShader(shaderProgram, vs);
            GLES20.glAttachShader(shaderProgram, fs);
            GLES20.glLinkProgram(shaderProgram);

            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(shaderProgram));
                GLES20.glDeleteProgram(shaderProgram);
                shaderProgram = 0;
            }

            if (vs > 0) GLES20.glDetachShader(shaderProgram, vs);
            if (fs > 0) GLES20.glDetachShader(shaderProgram, fs);
            if (vs > 0) GLES20.glDeleteShader(vs);
            if (fs > 0) GLES20.glDeleteShader(fs);
        }
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
