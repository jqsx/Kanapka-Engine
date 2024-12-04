#version 330 core

out vec4 fragColor;

in vec2 vTexCoord;
uniform sampler2D uMainTex;
uniform highp float uTime;
uniform vec4 uColors[64];

void main() {
    vec4 color = texture(uMainTex, vTexCoord);

    vec4 closest = vec4(vec3(1.0), 1.0);
    float l = 9999.0;

    for (int i = 0; i < 64; i++) {
        vec4 c = uColors[i];

        vec3 diff = abs(color.xyz - c.xyz);

        float _l = diff.x + diff.y + diff.z;

        if (_l < l) {
            closest = c;
            l = _l;
        }
    }

    fragColor = vec4(vec3(l), color.a * closest.a);
}