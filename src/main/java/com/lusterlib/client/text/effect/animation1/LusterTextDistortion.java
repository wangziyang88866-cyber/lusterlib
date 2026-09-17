package com.lusterlib.client.text.effect.animation1;

public class LusterTextDistortion implements LusterTextTransformSource {

    private static final float MIN_SCALE = 0.08F;
    private static final float MAX_SCALE = 3.25F;

    /*
     * 最终位移硬上限，防止高强度或错误参数让文字离开预期区域。
     * 单位为 GUI 像素。
     */
    private static final float MAX_OFFSET_X = 14.0F;
    private static final float MAX_OFFSET_Y = 18.0F;

    private float amplitude = 1.0F;
    private float speed = 1.0F;
    private float frequency = 0.45F;

    private float moveX = 1.5F;
    private float moveY = 1.5F;

    private float scaleX = 0.12F;
    private float scaleY = 0.18F;

    private float rotation = 4.0F;

    private int seed;
    private boolean enabled = true;

    @Override
    public LusterTextTransform getTransform(int index, int length, float time) {
        if (!enabled || length <= 0 || index < 0 || index >= length) {
            return LusterTextTransform.IDENTITY;
        }

        float intensity = Math.max(0.0F, amplitude);
        float position = length == 1
                ? 0.5F
                : (float) index / (float) (length - 1);

        float phase = time * speed;
        float seedOffset = seed * 0.0137F;

        /*
         * 多个非整数速度的连续形变场。
         * 它们不会像单一 sin 一样整齐地一起回到原位。
         */
        float localPhase = phase * 1.6180339F
                + position * frequency * 12.0F
                + seedOffset;

        float distortion = (
                sin(localPhase)
                        + sin(localPhase * 0.731F + 2.4F) * 0.65F
                        + cos(localPhase * 1.913F - 0.8F) * 0.40F
        ) / 2.05F;

        float flow = (
                sin(phase * 0.317F + position * 17.0F + seedOffset)
                        + cos(phase * 0.173F - position * 9.0F) * 0.55F
        ) / 1.55F;

        float pressure = (
                sin(phase * 0.113F + position * 23.0F)
                        + cos(phase * 0.071F - position * 13.0F + seedOffset) * 0.60F
        ) / 1.60F;

        /*
         * 持续累积的扭转相位。
         * 即使位移暂时接近中心，缩放与旋转仍继续变化。
         */
        float twist = phase * (0.90F + position * 0.70F)
                + distortion * 2.0F
                + flow * 1.25F;

        float rawOffsetX = (
                distortion * 2.00F
                        + flow * 0.95F
                        + pressure * 0.55F
        ) * moveX * intensity;

        float rawOffsetY = (
                distortion * 2.40F
                        + pressure * 1.25F
                        - 0.55F
        ) * moveY * intensity;

        /*
         * 最终位移强制限制在局部范围内。
         */
        float offsetX = clamp(rawOffsetX, -MAX_OFFSET_X, MAX_OFFSET_X);
        float offsetY = clamp(rawOffsetY, -MAX_OFFSET_Y, MAX_OFFSET_Y);

        /*
         * 指数缩放比原先的 1 ± scale 更激烈，
         * 但最终仍限制在安全范围，避免矩阵异常或无限放大。
         */
        float scaleFieldX = sin(twist) * 1.20F
                + distortion * 0.75F
                + pressure * 0.35F;

        float scaleFieldY = cos(twist * 1.37F + distortion) * 1.45F
                - distortion * 0.95F
                + flow * 0.50F;

        float finalScaleX = (float) Math.exp(
                clamp(scaleFieldX * scaleX * intensity * 3.0F, -2.0F, 1.2F)
        );

        float finalScaleY = (float) Math.exp(
                clamp(scaleFieldY * scaleY * intensity * 3.6F, -2.0F, 1.2F)
        );

        /*
         * rotation 随时间持续累积；不再使用会回归 0 的单一波形。
         */
        float finalRotation = (
                twist * 0.85F
                        + distortion * 2.5F
                        + flow * 1.2F
        ) * rotation * intensity;

        return new LusterTextTransform(
                offsetX,
                offsetY,
                clamp(finalScaleX, MIN_SCALE, MAX_SCALE),
                clamp(finalScaleY, MIN_SCALE, MAX_SCALE),
                finalRotation
        );
    }

    private static float sin(float value) {
        return (float) Math.sin(value);
    }

    private static float cos(float value) {
        return (float) Math.cos(value);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public float getAmplitude() {
        return amplitude;
    }

    public LusterTextDistortion setAmplitude(float amplitude) {
        this.amplitude = amplitude;
        return this;
    }

    /**
     * 0 为无扭曲，1 为默认强度。
     */
    public float getIntensity() {
        return amplitude;
    }

    public LusterTextDistortion setIntensity(float intensity) {
        this.amplitude = Math.max(0.0F, intensity);
        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public LusterTextDistortion setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public float getFrequency() {
        return frequency;
    }

    public LusterTextDistortion setFrequency(float frequency) {
        this.frequency = frequency;
        return this;
    }

    public float getMoveX() {
        return moveX;
    }

    public LusterTextDistortion setMoveX(float moveX) {
        this.moveX = moveX;
        return this;
    }

    public float getMoveY() {
        return moveY;
    }

    public LusterTextDistortion setMoveY(float moveY) {
        this.moveY = moveY;
        return this;
    }

    public float getScaleX() {
        return scaleX;
    }

    public LusterTextDistortion setScaleX(float scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    public float getScaleY() {
        return scaleY;
    }

    public LusterTextDistortion setScaleY(float scaleY) {
        this.scaleY = scaleY;
        return this;
    }

    public float getRotation() {
        return rotation;
    }

    public LusterTextDistortion setRotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public int getSeed() {
        return seed;
    }

    public LusterTextDistortion setSeed(int seed) {
        this.seed = seed;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LusterTextDistortion setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}