#version 330

in vec2 foobar;

out vec4 fragColor;

uniform float width;

uniform sampler2D textureSampler;

/**
 * Convert r, g, b to normalized vec3
 */
vec3 rgb(float r, float g, float b) {
    return vec3(r / 255.0, g / 255.0, b / 255.0);
}

/**
 * Draw a circle at vec2 `pos` with radius `rad` and
 * color `color`.
 */
vec4 circle(vec2 uv, vec2 pos, float rad, vec3 color) {

    vec2 temp = uv - pos;

    temp.x = (abs(temp.x) > 0.5f * width) ? temp.x - width * sign(temp.x) : temp.x;
    temp.y = (abs(temp.y) > 0.5f * width) ? temp.y - width * sign(temp.y) : temp.y;

    float d = length(temp) - rad;
    float t = clamp(d, 0.0, 1.0);
    return vec4(color, 1.0 - t);
}

void main()
{
//    vec2 iResolution = vec2(width);
//    vec2 uv = gl_FragCoord.xy;
//    vec2 center = 0.5 * iResolution;
//    float radius = 0.1 * iResolution.y;
//
//    // Background layer
//    vec4 layer1 = vec4(rgb(210.0, 222.0, 228.0), 1.0);
//
//    // Circle
//    vec3 red = rgb(225.0, 95.0, 60.0);
//    vec4 layer2 = circle(uv, center, radius, red);
//
//    // Blend the two
//    fragColor = mix(layer1, layer2, layer2.a);
////
////    vec2 uv = foobar / width;
//
//    fragColor = mix(gl_FragColor.rgb, vec4(uv, 0.0f, 0.0f), 0.5);

    fragColor = texture(textureSampler, foobar);

}