#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D Wx;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main(void) {
 gl_FragColor=vec4(0.1,1.,0.,1.);
//  gl_FragColor = texture2D(Wx,vertTexCoord.st);    
}
