attribute vec2 inPosition; // input from the vertex buffer
attribute vec3 inColor; // input from the vertex buffer
varying vec3 vertColor; // output from this shader to the next pipeline stage
uniform float time; // variable constant for all vertices in a single draw
void main() {
	vec3 position = vec3(inPosition,0.0);
	position.x += cos(position.y + time);
	position.z = sin(position.y + time);
	gl_Position = vec4(position, 1.0); 
	vertColor = inColor;
} 
