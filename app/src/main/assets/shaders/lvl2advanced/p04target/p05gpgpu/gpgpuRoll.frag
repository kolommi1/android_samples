precision mediump float;
varying vec2 texCoord;
uniform sampler2D textureID;
void main() {
	gl_FragColor = texture2D(textureID, texCoord);
	gl_FragColor = texture2D(textureID, fract(texCoord+2.0/512.0));
	//gl_FragColor = vec4(texCoord, 1.0,1.0);
	//gl_FragColor.r = 1.0;
} 
