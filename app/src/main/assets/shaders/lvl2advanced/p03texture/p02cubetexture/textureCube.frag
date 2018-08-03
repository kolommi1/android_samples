precision mediump float;
varying vec3 vertColor;
varying vec3 vertPosition;
uniform samplerCube textureID;
void main() {
	gl_FragColor = vec4(textureCube(textureID, vertPosition).rgb,1.0);
}

	 
