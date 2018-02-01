attribute vec3 inPosition; // input from the vertex buffer
attribute vec3 inNormal; // input from the vertex buffer
varying vec3 vertColor; // output from this shader to the next pipeline stage
uniform mat4 mat; // variable constant for all vertices in a single draw
void main() {
	gl_Position = mat * vec4(inPosition, 1.0);
	vertColor = inNormal * 0.5 + 0.5;
} 
