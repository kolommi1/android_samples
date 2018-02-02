precision mediump float;
varying vec3 vertColor; // input from the previous pipeline stage
void main() {
	//gl_FragColor = vec4(vec3(position.x,position.y,position.z),0.0);
 	gl_FragColor = vec4(vertColor, 1.0);
	//gl_FragColor = vec4(1.0);
} 


