#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform float Intensity;
uniform float Time;
uniform vec2 InSize;
uniform float Pulse;
uniform vec2 MotionDirection;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    float amount = clamp(Intensity, 0.0, 2.0);
    float strength = clamp(amount, 0.0, 1.0);
    float blendAmount = strength * strength * (3.0 - 2.0 * strength);
    float power = 1.0 + max(amount - 1.0, 0.0) * 0.8;
    float pulse = clamp(Pulse + (texCoord0.y - 0.5) * 0.08, 0.0, 1.0);
    vec2 texel = 1.0 / max(InSize, vec2(1.0));
    float splitPixels = clamp((0.55 + 0.45 * pulse) * power, 0.0, 1.0);

    vec4 center = texture(Sampler0, texCoord0);
    vec3 left = texture(Sampler0, clamp(texCoord0 - vec2(texel.x, 0.0), vec2(0.0), vec2(1.0))).rgb;
    vec3 right = texture(Sampler0, clamp(texCoord0 + vec2(texel.x, 0.0), vec2(0.0), vec2(1.0))).rgb;
    vec3 down = texture(Sampler0, clamp(texCoord0 - vec2(0.0, texel.y), vec2(0.0), vec2(1.0))).rgb;
    vec3 up = texture(Sampler0, clamp(texCoord0 + vec2(0.0, texel.y), vec2(0.0), vec2(1.0))).rgb;
    vec3 sharpened = center.rgb + (center.rgb * 4.0 - left - right - down - up) * (0.72 * power);
    // Reuse the horizontal sharpening samples for sub-pixel RGB separation.
    vec3 chromatic = sharpened;
    chromatic.r = mix(sharpened.r, right.r, splitPixels * 0.38);
    chromatic.b = mix(sharpened.b, left.b, splitPixels * 0.38);

    // One history lookup gives a restrained temporal blur without a second displaced sample.
    vec2 motion = MotionDirection * texel * (2.5 + 2.5 * pulse) * power;
    vec3 previousA = texture(Sampler1, clamp(texCoord0 - motion, vec2(0.0), vec2(1.0))).rgb;
    vec3 color = mix(chromatic, previousA, min(0.30, 0.16 * power));

    float luminance = dot(color, vec3(0.2126, 0.7152, 0.0722));
    float saturation = max(0.10, 0.32 / power);
    color = mix(vec3(luminance), color, saturation);
    color *= 1.58 + 0.36 * (power - 1.0);
    color = (color - 0.5) * (1.72 + 0.18 * pulse + 0.34 * (power - 1.0)) + 0.5;

    // Cross-process and deepen shadows without adding any local red glow.
    float processedLuma = dot(color, vec3(0.2126, 0.7152, 0.0722));
    float shadowMask = 1.0 - smoothstep(0.10, 0.58, processedLuma);
    color *= 1.0 - shadowMask * min(0.48, 0.31 * power);
    color += shadowMask * vec3(-0.055, 0.035, 0.075) * power;
    color += smoothstep(0.48, 0.96, processedLuma) * vec3(0.035, 0.018, -0.025) * power;

    // Deliberately push bright regions past a conventional shoulder for an excessive HDR response.
    vec3 hot = max(color - vec3(0.62), vec3(0.0));
    color += hot * hot * (1.35 * power);

    // Strong global sickly cyan-green cast; unlike the old treatment this is not position-dependent.
    color *= mix(vec3(1.0), vec3(0.82, 1.07, 1.10), min(1.0, 0.82 * power));

    // Interleaved gradient noise avoids an expensive per-pixel trigonometric hash.
    float noiseFrame = floor(Time * 30.0) * 0.75487766;
    float noise = fract(52.9829189 * fract(
        dot(gl_FragCoord.xy + vec2(noiseFrame), vec2(0.06711056, 0.00583715))
    )) - 0.5;
    color += noise * (0.045 * power);

    fragColor = vec4(clamp(mix(center.rgb, color, blendAmount), 0.0, 1.0), center.a);
}
