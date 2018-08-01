package uhk.android_samples.lvl2advanced.p03texture.p01quad;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLRenderTarget;
import uhk.android_samples.oglutils.OGLTexImageFloat;
import uhk.android_samples.oglutils.OGLTexture2D;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;
import uhk.android_samples.oglutils.ToFloatArray;
import uhk.android_samples.transforms.Camera;
import uhk.android_samples.transforms.Mat4;
import uhk.android_samples.transforms.Mat4Identity;
import uhk.android_samples.transforms.Vec3D;

/**
 * Load and apply texture using OGLTexture from oglutils package
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers;

    private int shaderProgram, locMat;

    private OGLTexture2D texture;
    private OGLTexture2D texture2;
    private OGLTexture2D texture3;
    private OGLTexture2D texture4;

    private Camera cam = new Camera();
    private Mat4 proj;
    private OGLTexture2D.Viewer textureViewer;
    private OGLRenderTarget renderTarget1, renderTarget2;

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
        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl2advanced/p03texture/p01quad/textureQuad");

        createBuffers();

        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");

        texture = new OGLTexture2D(activity, "textures/testTexture.png");
        texture2 = new OGLTexture2D(activity, "textures/testTexture.jpg");
        texture3 = new OGLTexture2D(activity, "textures/testTexture.gif");
        texture4 = new OGLTexture2D(activity, "textures/testTexture.bmp");

        texture.setTexImage(addAxes(texture.getTexImage(new OGLTexImageFloat.Format(4))));
        texture2.setTexImage(addAxes(texture2.getTexImage(new OGLTexImageFloat.Format(4))));
        texture3.setTexImage(addAxes(texture3.getTexImage(new OGLTexImageFloat.Format(4))));
        texture4.setTexImage(addAxes(texture4.getTexImage(new OGLTexImageFloat.Format(4))));

        cam = cam.withPosition(new Vec3D(0.5, 0.5, 2))
                .withAzimuth(Math.PI /2)
                .withZenith(-Math.PI /2);


        renderTarget1 = new OGLRenderTarget(500, 500);
        renderTarget2 = new OGLRenderTarget(500, 500);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        textureViewer = new OGLTexture2D.Viewer(maxGlEsVersion);


    }

    private OGLTexImageFloat addAxes(OGLTexImageFloat image){
        int bold = 10;
        //draw axes to texture
        for (int i = 0; i<image.getWidth(); i++)
            for(int j=0; j<bold; j++){
                image.setPixel(i, j, 0, 1.0f); //red
                image.setPixel(i, j, 1, 0.0f); //green
                image.setPixel(i, j, 2, 0.0f); //blue
            }
        for (int i = 0; i<image.getHeight(); i++)
            for(int j=0; j<bold; j++){
                image.setPixel(j, i, 0, 0.0f); //red
                image.setPixel(j, i, 1, 1.0f); //green
                image.setPixel(j, i, 2, 0.0f); //blue
            }
        for (int i = 0; i<bold; i++)
            for(int j=0; j<bold; j++){
                image.setPixel(j, i, 0, 0.0f); //red
                image.setPixel(j, i, 1, 0.0f); //green
                image.setPixel(j, i, 2, 1.0f); //blue
            }
        //update image
        return image;
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        //render to texture
        renderTarget1.bind();

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniformMatrix4fv(locMat, 1, false,
                //ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);
                ToFloatArray.convert((new Mat4Identity())), 0);

        texture2.bind(shaderProgram, "textureID", 0);

        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        //end of render to texture

        //add axis to rendered texture and update
        renderTarget1.getColorTexture().setTexImage(
                addAxes(renderTarget1.getColorTexture().getTexImage(new OGLTexImageFloat.Format(4))));

        //render to texture
        renderTarget2.bind();
        GLES20.glClearColor(0.9f, 0.6f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniformMatrix4fv(locMat, 1, false,
                //ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);
                ToFloatArray.convert((new Mat4Identity())), 0);

        texture2.bind(shaderProgram, "textureID", 0);

        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);
        //end of render to texture

    /*    BufferedImage img = renderTarget2.getColorTexture().toBufferedImage();
        Graphics gr = img.getGraphics();
        gr.setColor(new Color(0xff0000ff)); //red
        gr.drawLine(0, 0, img.getWidth(), 150);
        gr.setColor(new Color(0xff00ff00));  //green
        gr.drawLine(0, 0, 150, img.getHeight());
        gr.setColor(new Color(0xffff0000)); //blue
        gr.drawLine(0, 0, img.getWidth()/2, img.getHeight()/2);
        img.setRGB(0, 0, 0xffffffff); //white
        gr.setColor(new Color(0xffffffff)); //white
        String data = "Buffered Image";
        //char[] data = new char[] {'Buffered Image'};
        gr.drawChars(data.toCharArray(), 0, data.length(), 100, 100);
        renderTarget2.getColorTexture().fromBufferedImage(img);*/

        // set the default render target (screen)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, width, height);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniformMatrix4fv(locMat, 1, false,
                //ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);
                ToFloatArray.convert((new Mat4Identity())), 0);

        renderTarget2.getColorTexture().bind(shaderProgram, "textureID", 0);

        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        textureViewer.view(texture, -1, -1, 0.5);
        textureViewer.view(texture2, -1, -0.5, 0.5);
        textureViewer.view(texture3, -1, 0, 0.5);
        textureViewer.view(texture4, -1, 0.5, 0.5);

        textureViewer.view(renderTarget1.getColorTexture(), 0, -1, 0.5);

        textureViewer.view(renderTarget2.getColorTexture(), -0.5, -1, 0.5);

        activity.setViewText( "lvl2advanced\np03texture\np01quad");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;

        GLES20.glViewport(0, 0, width, height);
    }


    private void createBuffers() {
        float[] cube = {
                // bottom (z-) face
                1, 0, 0,	0, 0, -1,	1, 0,
                0, 0, 0,	0, 0, -1,	0, 0,
                1, 1, 0,	0, 0, -1,	1, 1,
                0, 1, 0,	0, 0, -1,	0, 1

        };
        short[] indexBufferData = {0, 1, 2, 1, 2, 3};
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3),
                new OGLBuffers.Attrib("inTextureCoordinates", 2)
        };

        buffers = new OGLBuffers(cube, attributes, indexBufferData);
    }

}
