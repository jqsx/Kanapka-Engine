#version 330 core

out vec4 fragColor;

in vec2 vTexCoord;
uniform sampler2D uMainTex;

void main() {
    fragColor = texture(uMainTex, vTexCoord);
}