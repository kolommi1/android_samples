package uhk.android_samples.oglutils;

import android.opengl.GLES20;

import java.nio.Buffer;
import java.nio.ByteBuffer;

// requires EXT_texture_rg extension
public class OGLTexImageByte implements OGLTexImage<OGLTexImageByte> {
    private final byte[] data;
    private final int width, height, depth;
    private final OGLTexImage.Format<OGLTexImageByte> format;

    public static class Format implements OGLTexImage.Format<OGLTexImageByte> {
        private final int componentCount;

        public Format(int componentCount) {
            this.componentCount = componentCount;
        }

        public static final int
                GL_RED_EXT = 0x1903,
                GL_RG_EXT  = 0x8227;

        @Override
        public int getInternalFormat() {
            switch (componentCount) {
                case 1:
                    return GL_RED_EXT;
                case 2:
                    return GL_RG_EXT;
                case 3:
                    return GLES20.GL_RGB;
                case 4:
                    return GLES20.GL_RGBA;
                default:
                    return -1;
            }
        }

        @Override
        public int getPixelFormat() {
            switch (componentCount) {
                case 1:
                    return GL_RED_EXT;
                case 2:
                    return GL_RG_EXT;
                case 3:
                    return GLES20.GL_RGB;
                case 4:
                    return GLES20.GL_RGBA;
                default:
                    return -1;
            }
        }

        @Override
        public int getPixelType() {
            return GLES20.GL_UNSIGNED_BYTE;
        }

        @Override
        public int getComponentCount() {
            return componentCount;
        }

        @Override
        public Buffer newBuffer(int width, int height) {
            return newBuffer(width, height, 1);
        }

        @Override
        public Buffer newBuffer(int width, int height, int depth) {
            return  ByteBuffer.allocateDirect(width * height * depth
                    * componentCount);
        }

        @Override
        public OGLTexImageByte newTexImage(int width, int height) {
            return new OGLTexImageByte(width, height, 1, this);
        }

        @Override
        public OGLTexImageByte newTexImage(int width, int height, int depth) {
            return new OGLTexImageByte(width, height, depth, this);
        }
    }

    public static class FormatIntensity extends Format {
        public FormatIntensity() {
            super(1);
        }

        @Override
        public int getInternalFormat() {
            return 1;
        }

        @Override
        public int getPixelFormat() {
            return GLES20.GL_LUMINANCE;
        }

        @Override
        public int getPixelType() {
            return GLES20.GL_UNSIGNED_BYTE;
        }
    }

    public OGLTexImageByte(int width, int height, int depth, int componentCount) {
        this(width, height, depth, new OGLTexImageByte.Format(componentCount));
    }

    public OGLTexImageByte(int width, int height, int depth, int componentCount, byte[] data) {
        this(width, height, depth, new OGLTexImageByte.Format(componentCount), data);
    }

    public OGLTexImageByte(int width, int height, int componentCount) {
        this(width, height, 1, new OGLTexImageByte.Format(componentCount));
    }

    public OGLTexImageByte(int width, int height, int componentCount, byte[] data) {
        this(width, height, 1, new OGLTexImageByte.Format(componentCount), data);
    }

    public OGLTexImageByte(int width, int height, int depth, OGLTexImage.Format<OGLTexImageByte> format) {
        this( width, height, depth, format, new byte[width * height * depth * format.getComponentCount()]);
    }

    public OGLTexImageByte(int width, int height, OGLTexImage.Format<OGLTexImageByte> format) {
        this( width, height, 1, format, new byte[width * height * format.getComponentCount()]);
    }
    public OGLTexImageByte(int width, int height, OGLTexImage.Format<OGLTexImageByte> format, byte[] data) {
        this( width, height, 1, format, data);
    }

    public OGLTexImageByte(int width, int height, int depth, OGLTexImage.Format<OGLTexImageByte> format, byte[] data) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.format = format;
        this.data = data;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public void setDataBuffer(Buffer buffer) {
        if (buffer instanceof ByteBuffer && buffer.capacity() == width * height * depth * format.getComponentCount()) {
            buffer.rewind();
            ((ByteBuffer) buffer).get(data);
        }
    }

    @Override
    public Buffer getDataBuffer() {
        Buffer buffer = ByteBuffer.wrap(data);
        buffer.rewind();
        return buffer;
    }

    @Override
    public OGLTexImage.Format<OGLTexImageByte> getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }

    public OGLTexImageFloat toOGLTexImageFloat() {
        return toOGLTexImageFloat(format.getComponentCount()) ;
    }

    public OGLTexImageFloat toOGLTexImageFloat(int componentCount) {
        float[] array = new float[width * height * componentCount];
        for (int z = 0; z < depth; z++)
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    for (int i = 0; i < componentCount; i++)
                        array[z * width * height * componentCount
                                + y * width * componentCount + x * componentCount + i]
                                = (0xff & data[z * width * height * format.getComponentCount()
                                + y * width * format.getComponentCount()
                                + x * format.getComponentCount()
                                + i % format.getComponentCount()] )/ 255.0f;
        //0xff z duvodu pouziti bytu jako neznaminkoveho

        return new OGLTexImageFloat(width, height, depth, new OGLTexImageFloat.Format(componentCount), array);
    }

    public void setPixel(int x, int y, byte value) {
        setVoxel(x, y, 0, 0, value);
    }

    public void setPixel(int x, int y, int component, byte value) {
        setVoxel(x, y, 0, component, value);
    }

    public void setVoxel(int x, int y, int z, byte value) {
        setVoxel(x, y, z, 0, value);
    }

    public void setVoxel(int x, int y, int z, int component, byte value) {
        if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth
                && component >= 0 && component < format.getComponentCount()) {
            data[(z * width * height + y * width + x) * format.getComponentCount() + component] = value;
        }
    }

    public byte getPixel(int x, int y) {
        return getVoxel(x, y, 0, 0);
    }

    public byte getPixel(int x, int y, int component) {
        return getVoxel(x, y, 0, component);
    }

    public byte getVoxel(int x, int y, int z) {
        return getVoxel(x, y, z, 0);
    }

    public byte getVoxel(int x, int y, int z, int component) {
        byte value = 0;
        if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth && component >= 0 && component < format.getComponentCount())
            value = data[(z * width * height  + y * width  + x) * format.getComponentCount() + component];
        return value;
    }

  /*  public void save(GLProfile glp, String fileName) {
        TextureData textureData = new TextureData(glp,
                format.getInternalFormat(), width, height, 0,
                format.getPixelFormat(), format.getPixelType(),
                false, false, false, getDataBuffer(), null);
        try {
            Log.i(TAG,"Saving texture " + fileName );
            TextureIO.write(textureData, new File(fileName));
            Log.i(TAG," ... OK");
        } catch (GLException | IOException e) {
            Log.e(TAG,"failed");
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }*/

}
