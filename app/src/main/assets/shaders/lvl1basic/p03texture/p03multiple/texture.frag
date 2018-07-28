precision mediump float;
varying vec3 vertColor;
varying vec2 texCoord;
uniform float height;
uniform sampler2D textureID1;
uniform sampler2D textureID2;
void main() {
	if (gl_FragCoord.y<height/float(2))
		gl_FragColor = texture2D(textureID1, texCoord);
	else
		gl_FragColor = texture2D(textureID2, texCoord);
} 
