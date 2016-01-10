#version 430 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;

//uniform float green_color;

//out vec4 vs_color;
out vec2 TexCoord;

void main()
{
    gl_Position = vec4(position, 1.0f);
    //vs_color = color;
    TexCoord = textureCoord;
}
