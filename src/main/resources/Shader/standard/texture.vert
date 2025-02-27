#version 410 core

layout (location = 0) in vec3 position;

uniform mat4 uModelProj;

out vec2 vTexCoord;

void main() {
    vTexCoord = position.xy + vec2(0.5);
    gl_Position = uModelProj * vec4(position, 1.0);
}