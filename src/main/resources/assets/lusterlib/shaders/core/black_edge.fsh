#version 150

uniform sampler2D Sampler0;
uniform float Intensity;
uniform vec2 InSize;
uniform vec3 EdgeColor;
uniform vec3 InteriorColor;

in vec2 texCoord0;
out vec4 fragColor;

float sampleLuma(vec2 uv) {
    vec3 color = texture(Sampler0, clamp(uv, vec2(0.0), vec2(1.0))).rgb;
    return dot(color, vec3(0.2126, 0.7152, 0.0722));
}

// A four-tap centred gradient is deliberately used instead of an eight-tap Sobel kernel.
// It preserves the colour-only post-process path required by shader packs while cutting
// edge-detection texture reads from 24 to 12 per output pixel.
float gradient(vec2 uv, vec2 texel, float radius) {
    vec2 stepSize = texel * radius;
    float gx = sampleLuma(uv + vec2(stepSize.x, 0.0))
            - sampleLuma(uv - vec2(stepSize.x, 0.0));
    float gy = sampleLuma(uv + vec2(0.0, stepSize.y))
            - sampleLuma(uv - vec2(0.0, stepSize.y));
    return length(vec2(gx, gy));
}

void main() {
    vec4 source = texture(Sampler0, texCoord0);
    vec2 texel = 1.0 / max(InSize, vec2(1.0));

    // The narrow response forms the bright line. Wider responses are progressively dimmer,
    // producing a black -> gray -> edge-color ramp as a surface approaches a contour.
    float narrow = smoothstep(0.06, 0.30, gradient(texCoord0, texel, 1.0));
    float medium = smoothstep(0.05, 0.27, gradient(texCoord0, texel, 2.4));
    float wide = smoothstep(0.04, 0.24, gradient(texCoord0, texel, 4.8));
    float line = max(narrow, medium * 0.72);
    float inwardGlow = max(wide * 0.24, medium * 0.46);
    float contour = clamp(max(line, inwardGlow), 0.0, 1.0);
    contour = pow(contour, 0.82);

    vec3 treated = mix(InteriorColor, EdgeColor, contour);
    float blendAmount = Intensity * Intensity * (3.0 - 2.0 * Intensity);
    fragColor = vec4(mix(source.rgb, treated, blendAmount), source.a);
}
