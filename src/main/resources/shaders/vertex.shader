#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec3 deltas;

out vec2 pos;

void main()
{
    gl_Position = vec4(position.xy, 0.0, 1.0);
    pos = deltas.xy;
}