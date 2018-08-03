attribute vec3 inPosition;
attribute vec3 inNormal;
varying vec3 vertColor;
varying vec3 vertPosition;
uniform mat4 mat;
void main() {
	vertPosition = 2.0*inPosition.xyz-1.0;
	gl_Position = mat * vec4(inPosition, 1.0);
	vertColor = inNormal * 0.5 + 0.5;
} 
