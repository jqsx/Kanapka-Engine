#version 410 core

uniform vec3 uColor;

out vec4 fragColor;

void main() {
    vec3 color = uColor;
    if (uColor.x + uColor.y + uColor.z == 0.0)
        color = vec3(1.0, 0.0, 1.0);

    fragColor = vec4(color, 1.0);
}