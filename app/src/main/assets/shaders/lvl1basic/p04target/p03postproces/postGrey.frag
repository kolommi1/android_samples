precision mediump float;
varying vec2 texCoord;
uniform sampler2D textureID;
void main() {
	float delta = 1.0/512.0;
	vec3 color = texture2D(textureID, texCoord).rgb;
	
	//convert to grey level
	gl_FragColor = vec4(vec3(0.299*color.r+0.587*color.b+0.114*color.g),1.0);
} 
