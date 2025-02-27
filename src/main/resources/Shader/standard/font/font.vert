#version 410 core

layout (location = 0) in vec3 aPosition;

uniform mat4 uModelProj;

out vec2 uTexCoord;

void main() {
    gl_Position = uModelProj * vec4(aPosition, 1.0);
    uTexCoord = aPosition.xy + vec2(0.5);
}