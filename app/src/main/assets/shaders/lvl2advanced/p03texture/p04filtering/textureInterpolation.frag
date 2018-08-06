#extension GL_EXT_shader_texture_lod : enable
precision mediump float;
varying vec3 vertColor;
varying vec2 texCoord;

uniform sampler2D textureID;
uniform float level;
void main() {
	vec2 textureCoord;

	textureCoord = texCoord ;

	gl_FragColor = texture2D(textureID, textureCoord * 2.0);
	if (level > 0.0)
	    gl_FragColor = texture2DLodEXT(textureID, textureCoord, level);
}
