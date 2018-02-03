attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTextureCoordinates;
varying vec3 vertColor;
varying vec2 texCoord;
uniform mat4 mat;
void main() {
	gl_Position = mat * vec4(inPosition, 1.0);
	vertColor = inNormal * 0.5 + 0.5;
	texCoord = inTextureCoordinates;
} 
