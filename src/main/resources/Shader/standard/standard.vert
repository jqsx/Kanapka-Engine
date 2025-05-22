#version 410 core

layout (location = 0) in vec3 aPosition;

uniform mat4 uModelProj;

void main() {
    gl_Position = uModelProj * vec4(aPosition, 1.0);
}