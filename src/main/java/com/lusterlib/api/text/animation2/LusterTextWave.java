package com.lusterlib.api.text.animation2;

import com.lusterlib.client.text.effect.animation2.LusterTextDistortion;

/**
 * 可配置的整体连续波形文字效果。
 */
public final class LusterTextWave implements LusterTextEffect {

    private final LusterTextDistortion distortion = new LusterTextDistortion();

    /** 0 为无扭曲，1 为默认强度，建议使用 0 至 2。 */
    public LusterTextWave setIntensity(float intensity) {
        distortion.setIntensity(intensity);
        return this;
    }

    public float getIntensity() { return distortion.getIntensity(); }

    /** 设置整体波形的动画速度；1 为默认速度。 */
    public LusterTextWave setSpeed(float speed) {
        distortion.setSpeed(speed);
        return this;
    }

    /** 设置整段文字内的起伏数量；1 表示一条完整波形。 */
    public LusterTextWave setFrequency(float frequency) {
        distortion.setFrequency(frequency);
        return this;
    }

    /** 设置整体横向位移幅度。 */
    public LusterTextWave setMoveX(float moveX) {
        distortion.setMoveX(moveX);
        return this;
    }

    /** 设置整体纵向位移幅度。 */
    public LusterTextWave setMoveY(float moveY) {
        distortion.setMoveY(moveY);
        return this;
    }

    /** 设置横向缩放幅度。 */
    public LusterTextWave setScaleX(float scaleX) {
        distortion.setScaleX(scaleX);
        return this;
    }

    /** 设置纵向缩放幅度。 */
    public LusterTextWave setScaleY(float scaleY) {
        distortion.setScaleY(scaleY);
        return this;
    }

    /** 设置沿波形倾斜时的最大旋转角度。 */
    public LusterTextWave setRotation(float rotation) {
        distortion.setRotation(rotation);
        return this;
    }

    @Override
    public LusterTextTransform transform(int characterIndex, int characterCount, float timeSeconds) {
        com.lusterlib.client.text.effect.animation2.LusterTextTransform transform =
                distortion.getTransform(characterIndex, characterCount, timeSeconds);
        return new LusterTextTransform(
                transform.getOffsetX(), transform.getOffsetY(), transform.getScaleX(),
                transform.getScaleY(), transform.getRotation()
        );
    }
}
