precision mediump float;
varying vec3 vertColor;
varying vec2 texCoord;
uniform sampler2D textureID;
void main() {
	gl_FragColor = texture2D(textureID, texCoord);
	if (length(texCoord.y)<0.1) //x axis
		gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
	if (length(texCoord.x)<0.1) //y axis 
		gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);
	if (length(texCoord)<0.3) 
		gl_FragColor = vec4(1.0);
	if (length(texCoord)<0.2){
		gl_FragColor = texture2D(textureID, vec2(0.0, 0.0));
	} 
} 
