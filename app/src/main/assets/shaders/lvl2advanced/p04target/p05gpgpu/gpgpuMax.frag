precision mediump float;
varying vec2 texCoord;
uniform sampler2D textureID;
void main() {
	float delta = 1.0/512.0;
	float value1 = texture2D(textureID, texCoord).r;
	float value2 = texture2D(textureID, texCoord+vec2( delta, 0.0)).r;
	//gl_FragColor = vec4(texCoord, 1.0,1.0);
	
	float value = max(value1,value2);
	gl_FragColor = vec4(value, 0.0, 0.0, 1.0);
} 
