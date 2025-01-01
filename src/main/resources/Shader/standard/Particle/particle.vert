#version 330

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aOffset;

uniform mat4 uModelProj;

out vec2 vTexCoord;

void main() {
    vTexCoord = aPosition.xy + vec2(0.5);
    gl_Position = uModelProj * vec4(aPosition + vec3(aOffset, 0.0), 1.0);
}