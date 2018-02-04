#version 300 es
precision mediump float;
in vec3 vertColor;
in vec2 texCoord;
uniform sampler2D textureID;
out vec4 fragColor;
void main() {
	fragColor = texture(textureID, texCoord);
} 
