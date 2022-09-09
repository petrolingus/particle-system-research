#version 330

layout (location=0) in vec3 position;

layout(std140, binding = 0) uniform Example {
    float x;
};

out float particlePos;

void main()
{
    gl_Position = vec4(position, 1.0);
    particlePos = x;
}