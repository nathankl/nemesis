#version 430 core

//in vec4 vs_color;
in vec2 TexCoord;

out vec4 color;

//uniform vec4 uColor;
uniform sampler2D uTexture;

void main(void) {
    //color = vs_color;
    color = texture(uTexture, TexCoord);
}