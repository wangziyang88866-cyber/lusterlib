package com.lusterlib.client.text.effect.animation2;

/**
 * 有规律的整体文字扭曲。
 *
 * <p>所有字符采样同一条随时间推进的连续曲线。位移、缩放和旋转均从该曲线
 * 及其斜率得出，不含每字符随机噪声，因此整段文字会像一条柔软的带子一样起伏。</p>
 */
public class LusterTextDistortion implements LusterTextTransformSource {

    private static final float TAU = (float) (Math.PI * 2.0D);
    private static final float MIN_SCALE = 0.05F;

    private float amplitude = 1.0F;
    private float speed = 1.0F;

    /** 一整段文字内出现的完整起伏数量；1 为一条完整波形。 */
    private float frequency = 1.0F;

    private float moveX = 0.75F;
    private float moveY = 2.25F;
    private float scaleX = 0.08F;
    private float scaleY = 0.14F;
    private float rotation = 5.0F;
    private boolean enabled = true;

    @Override
    public LusterTextTransform getTransform(int index, int length, float time) {
        if (!enabled || length <= 0 || index < 0 || index >= length) {
            return LusterTextTransform.IDENTITY;
        }

        float position = length == 1 ? 0.5F : (float) index / (float) (length - 1);
        float centeredPosition = position - 0.5F;
        float phase = time * speed;

        /* 所有字符共享 phase；position 只是在同一条空间曲线上取样。 */
        float spatialPhase = centeredPosition * frequency * TAU;
        float wavePhase = phase + spatialPhase;
        float wave = (float) Math.sin(wavePhase);
        float slope = (float) Math.cos(wavePhase);

        /* 整段文字共享的呼吸节奏，而非逐字独立缩放。 */
        float breath = (float) Math.sin(phase * 0.72F);
        float intensity = Math.max(0.0F, amplitude);

        float offsetX = (wave * 0.18F + breath * 0.22F) * moveX * intensity;
        float offsetY = wave * moveY * intensity;

        float finalScaleX = 1.0F
                + breath * scaleX * intensity
                - wave * scaleX * 0.25F * intensity;
        float finalScaleY = 1.0F
                - breath * scaleY * intensity
                + wave * scaleY * 0.45F * intensity;

        /* 曲线斜率决定倾角，因此相邻字符的旋转保持连续。 */
        float finalRotation = slope * frequency * rotation * intensity;

        return new LusterTextTransform(
                offsetX,
                offsetY,
                Math.max(MIN_SCALE, finalScaleX),
                Math.max(MIN_SCALE, finalScaleY),
                finalRotation
        );
    }

    public float getAmplitude() { return amplitude; }
    public LusterTextDistortion setAmplitude(float amplitude) { this.amplitude = amplitude; return this; }

    /** 0 为无扭曲，1 为默认强度。 */
    public float getIntensity() { return amplitude; }
    public LusterTextDistortion setIntensity(float intensity) {
        this.amplitude = Math.max(0.0F, intensity);
        return this;
    }

    public float getSpeed() { return speed; }
    public LusterTextDistortion setSpeed(float speed) { this.speed = speed; return this; }
    public float getFrequency() { return frequency; }
    public LusterTextDistortion setFrequency(float frequency) { this.frequency = frequency; return this; }
    public float getMoveX() { return moveX; }
    public LusterTextDistortion setMoveX(float moveX) { this.moveX = moveX; return this; }
    public float getMoveY() { return moveY; }
    public LusterTextDistortion setMoveY(float moveY) { this.moveY = moveY; return this; }
    public float getScaleX() { return scaleX; }
    public LusterTextDistortion setScaleX(float scaleX) { this.scaleX = scaleX; return this; }
    public float getScaleY() { return scaleY; }
    public LusterTextDistortion setScaleY(float scaleY) { this.scaleY = scaleY; return this; }
    public float getRotation() { return rotation; }
    public LusterTextDistortion setRotation(float rotation) { this.rotation = rotation; return this; }
    public boolean isEnabled() { return enabled; }
    public LusterTextDistortion setEnabled(boolean enabled) { this.enabled = enabled; return this; }
}
