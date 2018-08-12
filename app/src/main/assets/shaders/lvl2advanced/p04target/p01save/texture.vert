attribute vec3 inPosition;
attribute vec3 inNormal;
varying vec3 vertColor;
varying vec2 texCoord;
uniform mat4 mat;
void main() {
	gl_Position = mat * vec4(inPosition, 1.0);
	vertColor = inNormal * 0.5 + 0.5;
	int aux = int(dot(abs(inNormal) * vec3(0, 1, 2), vec3(1, 1, 1)));
	texCoord = vec2(inPosition[int(mod(float((aux + 1)) , 3.0))], inPosition[int(mod(float((aux + 2)) ,3.0))]);
} 
