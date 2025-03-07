#version 410 core

out vec4 fragColor;

in vec2 vTexCoord;
uniform sampler2D uMainTex;
uniform highp float uTime;

uniform vec2 uAtlasRes;
uniform int uAtlasIndex;

void main() {
    vec2 a = round(abs(uAtlasRes));

    vec2 texCoord = vTexCoord;

    if (a.x > 0 && a.y > 0) {
        int max = int(a.x * a.y);

        int boundIndex = abs(uAtlasIndex) % max;

        float xStep = 1.f / a.x;
        float yStep = 1.f / a.y;

        vec2 texIndex = vec2(boundIndex % int(a.x), floor(float(boundIndex / a.y)));

        texCoord = vec2(texCoord.x * xStep + texIndex.x * xStep, texCoord.y * yStep + texIndex.y * yStep);
    }

    fragColor = texture(uMainTex, texCoord);
}