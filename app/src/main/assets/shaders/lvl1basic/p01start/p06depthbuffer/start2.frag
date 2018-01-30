precision mediump float;
varying vec3 vertColor; // input from the previous pipeline stage
void main() {
//	gl_FragColor = vec4(vertColor, 1.0); //coloring by set color
	gl_FragColor = vec4(vec3(gl_FragCoord.z), 1.0); //coloring by the depth coordinate
//	gl_FragColor = vec4(gl_FragCoord.xy / 1000.0, gl_FragCoord.z, 1.0); // coloring by the pixel position
} 
