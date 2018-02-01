package uhk.android_samples.oglutils;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES31;
import android.util.Log;

import static uhk.android_samples.oglutils.ShaderUtils.COMPUTE_SHADER_SUPPORT_VERSION;
import static uhk.android_samples.oglutils.ShaderUtils.FRAGMENT_SHADER_SUPPORT_VERSION;
import static uhk.android_samples.oglutils.ShaderUtils.VERTEX_SHADER_SUPPORT_VERSION;

public class OGLUtils {
	private static final String TAG = "OGLESutils";
	/**
	 * Print version, vendor and extensions of current OpenGL ES
	 * @param maxGlEsVersion maximum supported OpenGL ES version
	 */
	public static void printOGLparameters(int maxGlEsVersion) {
		if(maxGlEsVersion >= 0x31000){
			Log.i(TAG,"GLES vendor: " + GLES31.glGetString(GLES31.GL_VENDOR));
			Log.i(TAG, "GLES renderer: " + GLES31.glGetString(GLES31.GL_RENDERER));
			Log.i(TAG,"GLES version: " + GLES31.glGetString(GLES31.GL_VERSION));
			Log.i(TAG,"GLES shading language version: " + GLES31.glGetString(GLES31.GL_SHADING_LANGUAGE_VERSION)
					+ " (#version " + getVersionGLSL(maxGlEsVersion) + ")");
			Log.i(TAG,"GLES extensions: " + GLES31.glGetString(GLES31.GL_EXTENSIONS));
		}
		else if(maxGlEsVersion >= 0x30000){
			Log.i(TAG,"GLES vendor: " + GLES30.glGetString(GLES30.GL_VENDOR));
			Log.i(TAG, "GLES renderer: " + GLES30.glGetString(GLES30.GL_RENDERER));
			Log.i(TAG,"GLES version: " + GLES30.glGetString(GLES30.GL_VERSION));
			Log.i(TAG,"GLES shading language version: " + GLES30.glGetString(GLES30.GL_SHADING_LANGUAGE_VERSION)
					+ " (#version " + getVersionGLSL(maxGlEsVersion) + ")");
			Log.i(TAG,"GLES extensions: " + GLES30.glGetString(GLES31.GL_EXTENSIONS));
		}
		else{
			Log.i(TAG,"GLES vendor: " + GLES20.glGetString(GLES20.GL_VENDOR));
			Log.i(TAG, "GLES renderer: " + GLES20.glGetString(GLES20.GL_RENDERER));
			Log.i(TAG,"GLES version: " + GLES20.glGetString(GLES20.GL_VERSION));
			Log.i(TAG,"GLES shading language version: " + GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION)
					+ " (#version " + getVersionGLSL(maxGlEsVersion) + ")");
			Log.i(TAG,"GLES extensions: " + GLES31.glGetString(GLES20.GL_EXTENSIONS));
		}
	}

	/**
	 * Get supported GLSL version
	 *
	 * @param maxGlEsVersion maximum supported OpenGL ES version
	 * @return version as integer number multiplied by 100, for GLSL 1.0 return
	 *         100...
	 */
	public static int getVersionGLSL(int maxGlEsVersion) {

		String version;
		if(maxGlEsVersion >= 0x30000){
			version = GLES30.glGetString(GLES30.GL_SHADING_LANGUAGE_VERSION);
		}
		else{
			version = GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION);
		}
		String[] parts = version.split(" ");
		parts = parts[4].split("\\.");
		return Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]);
	}

	/**
	 * Get supported OpenGL ES version
	 *
	 * @param maxGlEsVersion maximum supported OpenGL ES version
	 * @return version as integer number multiplied by 100, for OpenGL ES 3.1
	 *         return 310, ...
	 */
	public static int getVersionOpenGL(int maxGlEsVersion) {
		String version;
		if (maxGlEsVersion >= 0x31000){
			version = GLES31.glGetString(GLES31.GL_VERSION);
		}
		else if(maxGlEsVersion >= 0x30000){
			version = GLES30.glGetString(GLES30.GL_VERSION);
		}
		else{
			version = GLES20.glGetString(GLES20.GL_VERSION);
		}
		String[] parts = version.split(" ");
		parts = parts[2].split("\\.");
		return Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]) * 10;
	}

	/**
	 * Print parameters of current JAVA
	 * 
	 */
	public static void printJAVAparameters() {
		Log.i(TAG,"Java version: " + System.getProperty("java.version"));
		Log.i(TAG,"Java vendor: " + System.getProperty("java.vendor"));
	}

	/**
	 * Check OpenGL ES shaders support
	 *
	 * @param maxGlEsVersion maximum supported OpenGL ES version
	 */
	public static void shaderCheck(int maxGlEsVersion) {
		String extensions;
		if(maxGlEsVersion >= 0x31000){
			extensions = GLES30.glGetString(GLES31.GL_EXTENSIONS);
		}
		else if(maxGlEsVersion >= 0x30000){
			extensions = GLES30.glGetString(GLES30.GL_EXTENSIONS);
		}
		else{
			extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
		}

		if ( (OGLUtils.getVersionGLSL(maxGlEsVersion) < VERTEX_SHADER_SUPPORT_VERSION
				|| OGLUtils.getVersionGLSL(maxGlEsVersion) < FRAGMENT_SHADER_SUPPORT_VERSION)
				&& (!extensions.contains("GL_ARB_vertex_shader") || !extensions.contains("GL_ARB_fragment_shader")) ) {
			throw new RuntimeException("Shaders are not available.");
		}

		Log.i(TAG,"This OpenGL ES (shader #version: " + getVersionGLSL(maxGlEsVersion) + ") supports:\n vertex and fragment shader");

		if ((OGLUtils.getVersionGLSL(maxGlEsVersion) >= COMPUTE_SHADER_SUPPORT_VERSION)
				|| (extensions.contains("compute_shader"))){
			Log.i(TAG," compute shader");
		}
	}

/*	/**
	 * Return correct debug object
	 *
	 * @param gl
	 * @return
	 */
/*	public static GL2GL3 getDebugGL(GL2GL3 gl){
		int version = getVersionOpenGL(gl);
		if (version < 300)
			return new DebugGL2(gl.getGL2());
		if (version < 400)
			return new DebugGL3(gl.getGL3());
		return new DebugGL4(gl.getGL4());
	}

*/	/**
	 * Check GL error
	 *
	 * @param longReport
	 *            type of report
	 */
	static public void checkGLError( String text, boolean longReport) {
		int err = GLES20.glGetError();
		String errorName, errorDesc;

		while (err != GLES20.GL_NO_ERROR) {

			switch (err) {
			case GLES20.GL_INVALID_ENUM:
				errorName = "GL_INVALID_ENUM";
				errorDesc = "An unacceptable value is specified for an enumerated argument. The offending command is ignored and has no other side effect than to set the error flag.";
				break;

			case GLES20.GL_INVALID_VALUE:
				errorName = "GL_INVALID_VALUE";
				errorDesc = "A numeric argument is out of range. The offending command is ignored and has no other side effect than to set the error flag.";
				break;

			case GLES20.GL_INVALID_OPERATION:
				errorName = "GL_INVALID_OPERATION";
				errorDesc = "The specified operation is not allowed in the current state. The offending command is ignored and has no other side effect than to set the error flag.";
				break;
			case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION:
				errorName = "GL_INVALID_FRAMEBUFFER_OPERATION";
				errorDesc = "The framebuffer object is not complete. The offending command is ignored and has no other side effect than to set the error flag.";
				break;
			case GLES20.GL_OUT_OF_MEMORY:
				errorName = "GL_OUT_OF_MEMORY";
				errorDesc = "There is not enough memory left to execute the command. The state of the GL is undefined, except for the state of the error flags, after this error is recorded.";
				break;
			default:
				return;
			}
			if (longReport)
				Log.e(TAG,text + " GL error: " + err + " " + errorName + ": " + errorDesc);
			else
				Log.e(TAG,text + " GL error: " + errorName);
			err = GLES20.glGetError();
		}

	}

	/**
	 * Empty GL error
	 *
	 */
	static public void emptyGLError() {
		int err = GLES20.glGetError();
		while (err != GLES20.GL_NO_ERROR) {
			err = GLES20.glGetError();
		}

	}

	/**
	 * Check GL error
	 *
	 */
	static public void checkGLError(String text) {
		checkGLError( text, false);
	}

	/**
	 * Check GL error
	 */
	static public void checkGLError() {
		checkGLError( "", false);
	}

}
