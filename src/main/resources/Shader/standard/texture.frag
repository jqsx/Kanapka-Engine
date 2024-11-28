#version 330 core

out vec4 fragColor;

in vec2 vTexCoord;
uniform sampler2D uMainTex;
uniform highp float uTime;

void main() {
    fragColor = texture(uMainTex, vTexCoord);
}