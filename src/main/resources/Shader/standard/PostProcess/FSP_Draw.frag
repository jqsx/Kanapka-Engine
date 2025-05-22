#version 410 core

in vec2 vTexCoord;

out vec4 fragColor;

uniform sampler2D uMainTex;

const float offset = 1.0 / 300.0;

vec3 select(vec3 a, vec3 b, float v) {
    return v > 0.5 ? a : b;
}

vec3 isVibrant(vec3 target, vec3 backup) {
    float avg = (target.r * target.g * target.b) / 3.0;

    float _interpolation = round(avg - 0.3);

    return select(round(target), backup, _interpolation);
}

void main() {
//    vec2 offsets[9] = vec2[](
//    vec2(-offset,  offset), // top-left
//    vec2( 0.0f,    offset), // top-center
//    vec2( offset,  offset), // top-right
//    vec2(-offset,  0.0f),   // center-left
//    vec2( 0.0f,    0.0f),   // center-center
//    vec2( offset,  0.0f),   // center-right
//    vec2(-offset, -offset), // bottom-left
//    vec2( 0.0f,   -offset), // bottom-center
//    vec2( offset, -offset)  // bottom-right
//    );
//
//    float kernel[9] = float[](
//    1, 1, 1,
//    1, 1, 1,
//    1, 1, 1
//    );
//
//    vec3 sampleTex[9];
//    for(int i = 0; i < 9; i++)
//    {
//        sampleTex[i] = vec3(texture(uMainTex, vTexCoord.st + offsets[i]));
//    }
//    vec3 col = vec3(0.0);
//    for(int i = 0; i < 9; i++)
//        col += sampleTex[i] * kernel[i];

    fragColor = vec4(texture(uMainTex, vTexCoord.st).xyz, 1.0);
}