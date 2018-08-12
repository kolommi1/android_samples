precision mediump float;
varying vec3 vertColor;
varying vec2 texCoord;
uniform sampler2D textureID;
void main() {
	gl_FragColor = texture2D(textureID, texCoord);
	//gl_FragColor = texture2D(textureID, vec2(3*texCoord.x, 3*texCoord.y ));
	//gl_FragColor = texture2D(textureID, vec2(3*texCoord.x-floor(3*texCoord.x),3*texCoord.y-floor(3*texCoord.y)));
	//gl_FragColor.r = 1.0;
} 