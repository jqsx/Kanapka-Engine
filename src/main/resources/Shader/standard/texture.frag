#version 330 core

out vec4 fragColor;

in vec2 vTexCoord;
uniform sampler2D uMainTex;
uniform highp float uTime;

uniform vec4 uColors[64];

void main() {
    vec4 color = texture(uMainTex, vTexCoord);

    vec3 close = vec3(1.0, 0.0, 0.0);
    float far = 20000.0;
    for (int i = 0; i < 64; i++) {
        vec3 diff = abs(uColors[i].xyz - color.xyz);

        float dist = diff.x * diff.x + diff.y * diff.y + diff.z * diff.z;

        if (dist < far) {
            close = uColors[i].rgb;
            far = dist;
        }
    }

    fragColor = vec4(close, color.a);
}