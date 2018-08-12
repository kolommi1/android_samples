attribute vec2 inPosition;
varying vec2 texCoord;
uniform mat4 mat;
void main() {
	gl_Position = vec4(inPosition, 0.5, 1.0);
	texCoord = inPosition/2.0 + 0.5;
} 
