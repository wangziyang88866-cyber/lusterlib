package com.lusterlib.api.text.animation1;

import com.lusterlib.client.text.effect.animation1.LusterTextDistortion;

/**
 * 可配置的“反胃/蠕动”文字效果。
 */
public final class LusterTextRetching implements LusterTextEffect {

    private final LusterTextDistortion distortion = new LusterTextDistortion();

    /** 0 为无扭曲，1 为默认强度，建议使用 0 至 2。 */
    public LusterTextRetching setIntensity(float intensity) {
        distortion.setIntensity(intensity);
        return this;
    }

    public float getIntensity() { return distortion.getIntensity(); }

    public LusterTextRetching setSpeed(float speed) {
        distortion.setSpeed(speed);
        return this;
    }

    public LusterTextRetching setFrequency(float frequency) {
        distortion.setFrequency(frequency);
        return this;
    }

    public LusterTextRetching setSeed(int seed) {
        distortion.setSeed(seed);
        return this;
    }

    @Override
    public LusterTextTransform transform(int characterIndex, int characterCount, float timeSeconds) {
        com.lusterlib.client.text.effect.animation1.LusterTextTransform transform =
                distortion.getTransform(characterIndex, characterCount, timeSeconds);
        return new LusterTextTransform(
                transform.getOffsetX(), transform.getOffsetY(), transform.getScaleX(),
                transform.getScaleY(), transform.getRotation()
        );
    }
}
