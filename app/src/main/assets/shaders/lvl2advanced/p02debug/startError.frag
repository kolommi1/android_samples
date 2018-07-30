precision mediump float;
varying vec3 vertColor; // input from the previous pipeline stage
void main() {
	//wrong name of constructor
	//gl_FragColor = vec3(vertColor, 1.0);
	//correct name of constructor
	gl_FragColor = vec4(vertColor, 1.0);
} 
