package uhk.android_samples.lvl1basic.p01start.p04utils;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;

/**
 * Read and compile shader from files "/shader/glsl01/start.*" using ShaderUtils
 * class in oglutils package
 * Manage (create, bind, draw) vertex and index buffers using OGLBuffers class
 * in oglutils package
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private Context context;

    private OGLBuffers buffers;

    private int shaderProgram, locTime;

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

        shaderProgram = ShaderUtils.loadProgram(context, maxGlEsVersion,"shaders/lvl1basic/p01start/p04utils/start.vert",
                        "shaders/lvl1basic/p01start/p04utils/start.frag",null);

        //shorter version of loading shader program
        //shaderProgram = shaderUtils.loadProgram("shaders/lvl1basic/p01start/p04utils/start");

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

        // bind and draw
        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

    }

    private void createBuffers(){
        float[] vertexBufferData = {
            -1, -1, 	0.7f, 0, 0,
            1,  0,		0, 0.7f, 0,
            0,  1,		0, 0, 0.7f
        };
        short[] indexBufferData = { 0, 1, 2 };

        // vertex binding description, concise version
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2), // 2 floats
                new OGLBuffers.Attrib("inColor", 3) // 3 floats
        };
        buffers = new OGLBuffers(vertexBufferData, attributes,
                indexBufferData);
        // the concise version requires attributes to be in this order within
        // vertex and to be exactly all floats within vertex

/*		full version for the case that some floats of the vertex are to be ignored
 * 		(in this case it is equivalent to the concise version):
 		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 2, 0), // 2 floats, at 0 floats from vertex start
				new OGLBuffers.Attrib("inColor", 3, 2) }; // 3 floats, at 2 floats from vertex start
		buffers = new OGLBuffers( vertexBufferData, 5, // 5 floats altogether in a vertex
				attributes, indexBufferData);
*/

    }

}
