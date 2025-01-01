#version 330 core

uniform sampler2D uMainTex;
in vec2 uTexCoord;

flat in float location;

uniform float uTexWidth;

out vec4 fragColor;

void main() {
    float charWidth = 7.39;

    fragColor = texture(uMainTex, uTexCoord);
}