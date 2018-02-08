package uhk.android_samples.oglutils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

import uhk.android_samples.transforms.Mat4Scale;
import uhk.android_samples.transforms.Mat4Transl;

public class OGLTexture2D implements OGLTexture{
	private final int[] textureID = new int[1];
    private final int width, height;
	private static final String TAG = "OGLTexture2D";

    public static class Viewer implements OGLTexture.Viewer {
        protected final int shaderProgram;
        protected final OGLBuffers buffers;
        protected final int locMat;
        protected final int locLevel;

        private static final String SHADER_VERT_SRC =
                "#version 300 es\n"+
                "in vec2 inPosition;"+
                "in vec2 inTexCoord;"+
                "uniform mat4 matTrans;"+
                "out vec2 texCoords;"+
                "void main() {"+
                "	gl_Position = matTrans * vec4(inPosition , 0.0, 1.0);"+
                "   texCoords = inTexCoord;"+
                "}"
        ;

        private static final String SHADER_FRAG_SRC =
                "#version 300 es\n"+
                "precision mediump float;"+
                "in vec2 texCoords;"+
                "out vec4 fragColor;"+
                "uniform sampler2D drawTexture;"+
                "uniform int level;"+
                "void main() {"+
                " 	fragColor = texture(drawTexture, texCoords);"+
                " 	if (level >= 0)"+
                " 		fragColor = textureLod(drawTexture, texCoords, level);"+
                "}"
        ;

        private static final String oldSHADER_VERT_SRC =
                "attribute vec2 inPosition;"+
                "attribute vec2 inTexCoord;"+
                "uniform mat4 matTrans;"+
                "varying vec2 texCoords;"+
                "void main() {"+
                "	gl_Position = matTrans * vec4(inPosition , 0.0, 1.0);"+
                "   texCoords = inTexCoord;"+
                "}"
        ;

        private static final String oldSHADER_FRAG_SRC =
                "precision mediump float;"+
                "varying vec2 texCoords;"+
                "uniform sampler2D drawTexture;"+
                "uniform int level;"+
                "void main() {"+
                " 	gl_FragColor = texture2D(drawTexture, texCoords);"+
               /* " 	if (level >= 0)"+   // in OpenGL ES 2.0 texture2DLod only in VertexShader
                " 		gl_FragColor = texture2DLod(drawTexture, texCoords, level);"+*/
                "}"
        ;

        private OGLBuffers createBuffers() {
            float[] vertexBufferData = {
                    0, 0, 0, 0,
                    1, 0, 1, 0,
                    0, 1, 0, 1,
                    1, 1, 1, 1 };
            short[] indexBufferData = { 0, 1, 2, 3 };

            OGLBuffers.Attrib[] attributes = { new OGLBuffers.Attrib("inPosition", 2),
                    new OGLBuffers.Attrib("inTexCoord", 2) };

            return new OGLBuffers(vertexBufferData, attributes, indexBufferData);
        }

        public Viewer(int maxGlEsVersion) {
            if (OGLUtils.getVersionGLSL(maxGlEsVersion)<300) {
                shaderProgram = ShaderUtils.loadProgramFromSource(maxGlEsVersion, oldSHADER_VERT_SRC, oldSHADER_FRAG_SRC, null);
            }else {
                shaderProgram = ShaderUtils.loadProgramFromSource(maxGlEsVersion, SHADER_VERT_SRC, SHADER_FRAG_SRC, null);
            }

            buffers = createBuffers();
            locMat = GLES20.glGetUniformLocation(shaderProgram, "matTrans");
            locLevel = GLES20.glGetUniformLocation(shaderProgram, "level");
        }

        @Override
        public void view(int textureID) {
            view(textureID, -1, -1);
        }

        @Override
        public void view(int textureID, double x, double y) {
            view(textureID, x, y, 1.0, 1.0);
        }

        @Override
        public void view(int textureID, double x, double y, double scale) {
            view(textureID, x, y, scale, 1.0);
        }
        @Override
        public void view(int textureID, double x, double y, double scale, double aspectXY) {
            view(textureID, x, y, scale, aspectXY, -1);
        }

        @Override
        public void view(int textureID, double x, double y, double scale, double aspectXY, int level) {
            if (shaderProgram > 0) {
                GLES20.glUseProgram(shaderProgram);
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glEnable(GLES20.GL_TEXTURE_2D);
                GLES20.glUniformMatrix4fv(locMat, 1, false, ToFloatArray
                        .convert(new Mat4Scale(scale * aspectXY, scale, 1).mul(new Mat4Transl(x, y, 0))), 0);
                GLES20.glUniform1i(locLevel, level);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
                GLES20.glUniform1i(GLES20.glGetUniformLocation(shaderProgram, "drawTexture"), 0);
                buffers.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram);
                GLES20.glDisable(GLES20.GL_TEXTURE_2D);
                GLES20.glUseProgram(0);
            }
        }

    }

    static Bitmap readTextureDataFromFile(Context context, String fileName) {
        Log.i(TAG,"Reading texture file " + fileName);
        try {
            InputStream is = context.getAssets().open(fileName);
            //there are some problems on Mac OS with mipmap, in this case set false
            Bitmap data = BitmapFactory.decodeStream(is);
            is.close();
            Log.i(TAG," ... OK");
            return data;
        } catch (IOException e) {
            Log.e(TAG," failed");
            throw new RuntimeException(e);
        }
    }

    public OGLTexture2D(int width, int height, int textureId) {
        this.width = width;
        this.height = height;
        this.textureID[0] = textureId;
    }

    public OGLTexture2D(Bitmap textureData) {
        this.width = textureData.getWidth();
        this.height = textureData.getHeight();
        GLES20.glGenTextures(1, textureID, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID[0]);

        // A version of texImage2D that determines the internalFormat and type automatically.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureData, 0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        textureData.recycle();
    }

    public OGLTexture2D( int width, int height, int internalFormat, int pixelFormat, int pixelType, Buffer buffer) {
        this.width = width;
        this.height = height;
        GLES20.glGenTextures(1, textureID, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, internalFormat,
                width, height, 0,
                pixelFormat, pixelType, buffer);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    }

    public OGLTexture2D(Context context, String fileName) {
        this(readTextureDataFromFile(context, fileName));
    }

    public void bind() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID[0]);
    }

    @Override
    public void bind(int shaderProgram, String name, int slot) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + slot);
        bind();
        GLES20.glUniform1i(GLES20.glGetUniformLocation(shaderProgram, name), slot);
    }

    @Override
    public void bind(int shaderProgram, String name) {
        bind(shaderProgram, name, 0);
    }

    @Override
    public int getTextureId(){
        return textureID[0];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
