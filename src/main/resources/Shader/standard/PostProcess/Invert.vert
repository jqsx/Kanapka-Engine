#version 330 core

layout (location = 0) in vec3 aPosition;

out vec2 vTexCoord;

void main() {
    vTexCoord = (aPosition.xy + vec2(1.0)) / 2.0;
    gl_Position = vec4(aPosition, 1.0);
}