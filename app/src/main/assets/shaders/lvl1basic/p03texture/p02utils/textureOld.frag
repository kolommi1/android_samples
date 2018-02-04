precision mediump float;
varying vec3 vertColor;
varying vec2 texCoord;
uniform sampler2D textureID;
void main() {
	gl_FragColor = texture2D(textureID, texCoord);
} 
