precision mediump float;
varying vec3 vertColor; // input from the previous pipeline stage
void main() {
	gl_FragColor = vec4(vertColor, 1.0); //output
} 
