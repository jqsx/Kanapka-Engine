#version 410 core

in vec2 vTexCoord;

out vec4 fragColor;

uniform sampler2D uMainTex;

void main() {
    vec4 color = texture(uMainTex, vTexCoord);

    vec3 invert = 1.0 - color.xyz;

    fragColor = vec4(invert, color.a);
}