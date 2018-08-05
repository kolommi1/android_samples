package uhk.android_samples.lvl2advanced.p03texture.p04filtering;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uhk.android_samples.oglutils.OGLBuffers;
import uhk.android_samples.oglutils.OGLTexImageByte;
import uhk.android_samples.oglutils.OGLTexture2D;
import uhk.android_samples.oglutils.OGLTextureCube;
import uhk.android_samples.oglutils.OGLUtils;
import uhk.android_samples.oglutils.ShaderUtils;
import uhk.android_samples.oglutils.ToFloatArray;
import uhk.android_samples.transforms.Camera;
import uhk.android_samples.transforms.Mat4;
import uhk.android_samples.transforms.Mat4PerspRH;
import uhk.android_samples.transforms.Vec3D;

/**
 * Load and apply texture using OGLTexture from oglutils package<br/>
 * requires GL_EXT_shader_texture_lod
 */
public class Renderer implements GLSurfaceView.Renderer {

    private int maxGlEsVersion;
    private MainActivity activity;
    private int width, height;

    private OGLBuffers buffers;

    private int shaderProgram, locMat;

    private OGLTexture2D texture, textureGrid;

    private Camera cam = new Camera();
    private Mat4 proj;

    private OGLTexture2D.Viewer textureViewer;
    private int modeTex = 1, modeInter = 5, texSource;
    private int level, locLevel;

    private int size = 1024;
    private int maxLevel;
    Renderer(MainActivity activity, int maxGlEsVersion){
        this.maxGlEsVersion = maxGlEsVersion;
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OGLUtils.shaderCheck(maxGlEsVersion);
        OGLUtils.printOGLparameters(maxGlEsVersion);

        shaderProgram = ShaderUtils.loadProgram(activity, maxGlEsVersion, "shaders/lvl2advanced/p03texture/p04filtering/textureInterpolation");

        createBuffers();

        locMat = GLES20.glGetUniformLocation(shaderProgram, "mat");
        locLevel = GLES20.glGetUniformLocation(shaderProgram, "level");

        // texture files are in assets/textures/
        texture = new OGLTexture2D(activity, "textures/testTexture.png");
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        OGLTexImageByte imageGrid = new OGLTexImageByte(size, size, new OGLTexImageByte.Format(4));
        for (int i = 0; i < size; i += 20)
            for (int j = 0; j < size; j += 20)
                for (int m = 0; m < 10; m++)
                    for (int n = 0; n < 10; n++) {
                        imageGrid.setPixel(i + m, j + n, 1, (byte) 0xff);
                        imageGrid.setPixel(i + m, j + n, 2, (byte) 0xff);
                        imageGrid.setPixel(i + m, j + n, (byte) 0xff);
                    }

        textureGrid = new OGLTexture2D(imageGrid);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        // coloring first MIP level
        imageGrid = textureGrid.getTexImage(new OGLTexImageByte.Format(4), 1);
        for (int i = 0; i < imageGrid.getWidth(); i++)
            for (int j = 0; j < imageGrid.getHeight(); j++) {
                imageGrid.setPixel(i, j, 1, (byte) 0xff);
            }
        textureGrid.setTexImage(imageGrid, 1);

        // coloring second MIP level
        imageGrid = textureGrid.getTexImage(new OGLTexImageByte.Format(4), 2);
        for (int i = 0; i < imageGrid.getWidth(); i++)
            for (int j = 0; j < imageGrid.getHeight(); j++) {
                imageGrid.setPixel(i, j, 2, (byte) 0xff);
            }
        textureGrid.setTexImage(imageGrid, 2);

        // coloring third MIP level
        imageGrid = textureGrid.getTexImage(new OGLTexImageByte.Format(4), 3);
        for (int i = 0; i < imageGrid.getWidth(); i++)
            for (int j = 0; j < imageGrid.getHeight(); j++) {
                imageGrid.setPixel(i, j, 0, (byte) 0xff);
            }
        textureGrid.setTexImage(imageGrid, 3);

        cam = cam.withPosition(new Vec3D(1.01, 1.01, -0.02)).withAzimuth(Math.PI * 1.25).withZenith(Math.PI * 0.125);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        textureViewer = new OGLTexture2D.Viewer(maxGlEsVersion);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        activity.setViewText( "lvl2advanced\np03texture\np04filtering\n");
        OGLTexture2D testTexture;
        if (texSource == 1)
            testTexture = texture;
        else
            testTexture = textureGrid;

        testTexture.bind();
        maxLevel = 1 + (int) Math.floor((Math.log(Math.max(testTexture.getHeight(), testTexture.getWidth())) / Math.log(2)));

        switch (modeTex) {
            case 0:
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                activity.appendViewText("texMode: GL_CLAMP_TO_EDGE\n");
                break;
            case 1:
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
                activity.appendViewText("texMode: GL_REPEAT\n");
                break;
            case 2:
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);
                activity.appendViewText("texMode: GL_MIRRORED_REPEAT\n");
        }

        switch (modeInter) {
            case 0:
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                activity.appendViewText("interpolation: GL_NEAREST\n");
                break;
            case 1:
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                activity.appendViewText("interpolation: GL_LINEAR\n");
                break;
            case 2:
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST_MIPMAP_NEAREST);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                activity.appendViewText("interpolation: GL_NEAREST_MIPMAP_NEAREST\n");
                break;
            case 3:
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST_MIPMAP_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                activity.appendViewText("interpolation: GL_NEAREST_MIPMAP_LINEAR\n");
                break;
            case 4:
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                activity.appendViewText("interpolation: GL_LINEAR_MIPMAP_NEAREST\n");
                break;
            case 5:
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                activity.appendViewText("interpolation: GL_LINEAR_MIPMAP_LINEAR\n");
        }
        activity.appendViewText( "level of MIP " + level + "/" + maxLevel);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniformMatrix4fv(locMat, 1, false, ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);

        GLES20.glUniform1f(locLevel,level);

        buffers.draw(GLES20.GL_TRIANGLE_STRIP, shaderProgram);

        //show loaded texture and its mip layers
        textureViewer.view(texture, 0.25, -1, 0.5);
        for(int i= 0; i<8; i++)
            textureViewer.view(texture, 0.75, i*0.25-1, 0.25, 1.0, i);

        //show made texture and its mip layers
        textureViewer.view(textureGrid, -0.75, -1, 0.5);
        for(int i= 0; i<8; i++)
            textureViewer.view(textureGrid, -1, i*0.25-1, 0.25, 1.0, i);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;
        proj = new Mat4PerspRH(Math.PI / 0.6, height / (double) width, 0.01, 20.0);
        GLES20.glViewport(0, 0, width, height);
    }

    private void createBuffers() {
        float[] face = {
                // face
                1, 0, 0, 0, 0, 0, 0, 1,
                0, 0, 0, 0, 0, 0, 1, 1,
                1, 1, 0, 0, 0, 0, 0, 0,
                0, 1, 0, 0, 0, 0, 1, 0

        };

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3),
                new OGLBuffers.Attrib("inTextureCoordinates", 2) };

        buffers = new OGLBuffers(face, attributes, null);
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

    public void changeTexSource(){
        texSource = (texSource + 1) % 2;
    }

    public void changeLevel(){
        level = (level + 1) % maxLevel;
    }
    public void changeTexMode(){
        modeTex = (modeTex + 1) % 3;
    }

    public void changeInterMode(){
        modeInter = (modeInter + 1) % 6;
    }
}
