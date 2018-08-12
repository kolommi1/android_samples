package uhk.android_samples.lvl2advanced.p04target.p02draw;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLRenderTarget;
import uhk.android_samples.oglutils.OGLTexImageByte;
import uhk.android_samples.oglutils.OGLTexImageFloat;
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
 *   use renderTargetu for color and depth storage, edit texture
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers;
    private OGLTexture2D texture, textureColor, textureDepth;
    private OGLTexture2D.Viewer textureViewer;
    private OGLRenderTarget renderTarget;

    private int shaderProgram, locMat;

    private Camera cam = new Camera();
    private Mat4 proj;
    private boolean modificate = true;

    Renderer(MainActivity activity, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);

        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl2advanced/p04target/p02draw/texture");
        createBuffers();
        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");

        texture = new OGLTexture2D(activity, "textures/bricks.jpg");
        renderTarget = new OGLRenderTarget(500, 500);

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                        .withAzimuth(Math.PI * 1.25)
                        .withZenith(Math.PI * -0.125);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        textureViewer = new OGLTexture2D.Viewer(maxGlEsVersion);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glUseProgram(shaderProgram);

        // set render target
        renderTarget.bind();

        GLES20.glClearColor(0.1f, 0.2f, 0.3f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        texture.bind(shaderProgram, "textureID", 0);

        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)
                        .mul(new Mat4Scale((double) width / height, 1, 1))), 0);
        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        if (modificate) {
            // get texture context
            textureColor = renderTarget.getColorTexture();
            textureDepth = renderTarget.getDepthTexture();

            // get texture data as byte
            OGLTexImageByte imgByte = textureColor.getTexImage(new OGLTexImageByte.Format(4));
            // edit RGBA data
            imgByte.setPixel(100, 125, 0, (byte)0xff); // red
            imgByte.setPixel(100, 125, 1, (byte)0x00); // green
            imgByte.setPixel(100, 125, 2, (byte)0xff); // blue
            imgByte.setPixel(100, 125, 3, (byte)0xff); // alpha
            for (int i = 0; i < 200; i++) {
                imgByte.setPixel(i, i, 0, imgByte.getPixel(100, 125, 0));
                imgByte.setPixel(i, i, 1, imgByte.getPixel(100, 125, 1));
                imgByte.setPixel(i, i, 2, imgByte.getPixel(100, 125, 2));
                imgByte.setPixel(i, i, 3, imgByte.getPixel(100, 125, 3));
            }
            for (int i = 0; i < 200; i++) {
                imgByte.setPixel(i + 1, i, 0, (byte)0x00);
                imgByte.setPixel(i + 1, i, 1, (byte)0xff);
                imgByte.setPixel(i + 1, i, 2, (byte)0xff);
                imgByte.setPixel(i + 1, i, 3, (byte)0xff);
            }
            // save changes to texture
            textureColor.setTexImage(imgByte);

            // get depth data
            imgByte = textureDepth.getTexImage(new OGLTexImageByte.FormatDepth());
            for (int i = 0; i < 200; i++)
                imgByte.setPixel(i-1, i, (byte)0xff);
            textureDepth.setTexImage(imgByte);
        }

        // default render target
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, width, height);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // previous drawing used as texture for next drawing
        //renderTarget.bindColorTexture(shaderProgram, "textureID", 0);
        renderTarget.getColorTexture().bind(shaderProgram, "textureID", 0);
        //renderTarget.bindDepthTexture(shaderProgram, "textureID", 0);

        GLES20.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);

        buffers.draw(GLES20.GL_TRIANGLES, shaderProgram);

        GLES20.glUseProgram(0);
        textureViewer.view(textureColor, -1, 0, 0.5, height / (double) width);
        textureViewer.view(textureDepth, -1, -1, 0.5, height / (double) width);

        activity.setViewText( "lvl1basic\np04target\np02draw");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;

        GLES20.glViewport(0, 0, width, height);
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 1.0, 100.0);
    }


    private void createBuffers() {

        float[] cube = {
                // bottom (z-) face
                1, 0, 0,	0, 0, -1,
                0, 0, 0,	0, 0, -1,
                1, 1, 0,	0, 0, -1,
                0, 1, 0,	0, 0, -1,
                // top (z+) face
                1, 0, 1,	0, 0, 1,
                0, 0, 1,	0, 0, 1,
                1, 1, 1,	0, 0, 1,
                0, 1, 1,	0, 0, 1,
                // x+ face
                1, 1, 0,	1, 0, 0,
                1, 0, 0,	1, 0, 0,
                1, 1, 1,	1, 0, 0,
                1, 0, 1,	1, 0, 0,
                // x- face
                0, 1, 0,	-1, 0, 0,
                0, 0, 0,	-1, 0, 0,
                0, 1, 1,	-1, 0, 0,
                0, 0, 1,	-1, 0, 0,
                // y+ face
                1, 1, 0,	0, 1, 0,
                0, 1, 0,	0, 1, 0,
                1, 1, 1,	0, 1, 0,
                0, 1, 1,	0, 1, 0,
                // y- face
                1, 0, 0,	0, -1, 0,
                0, 0, 0,	0, -1, 0,
                1, 0, 1,	0, -1, 0,
                0, 0, 1,	0, -1, 0
        };

        short[] indexBufferData = new short[36];
        for (int i = 0; i<6; i++){
            indexBufferData[i*6] = (short)(i*4);
            indexBufferData[i*6 + 1] =(short)(i*4 + 1);
            indexBufferData[i*6 + 2] =(short)(i*4 + 2);
            indexBufferData[i*6 + 3] =(short)(i*4 + 1);
            indexBufferData[i*6 + 4] =(short)(i*4 + 2);
            indexBufferData[i*6 + 5] =(short)(i*4 + 3);
        }
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        buffers = new OGLBuffers(cube, attributes, indexBufferData);
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

    public void modificate(){
        modificate = !modificate;
    }

}
