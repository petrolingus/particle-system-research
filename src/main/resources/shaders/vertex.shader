#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texures;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec2 foobar;

void main()
{
    vec4 mvPos = viewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * mvPos;
    foobar = texures;
}