#version 330 core

layout (location = 0) in vec3 position;

uniform mat4 uModelProj;

void main() {
    gl_Position = uModelProj * vec4(position, 1.0);
}