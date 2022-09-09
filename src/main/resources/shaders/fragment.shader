#version 330

in float particlePos;

out vec4 fragColor;

void main()
{
//    float dist = distance(particlePos, gl_FragCoord.xy);
//    vec3 color = (dist < 100) ? vec3(1.0) : vec3(0.0);
//    vec3 color = ()
    vec3 color = vec3(particlePos, 0.0, 0.0);
    fragColor = vec4(color, 1.0);
}