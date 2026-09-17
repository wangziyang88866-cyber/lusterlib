package com.lusterlib.api.render.screen.shader1;

/**
 * 由调用方持有的理智着色强度平滑器。
 *
 * <p>它不依赖玩家、世界或事件类型；事件只需调用 {@link #setTarget(float)}，
 * 渲染代码每帧调用 {@link #update(float)} 即可平滑升降。</p>
 */
public final class LusterSanityTransition {

    private float value;
    private float target;
    private float riseSeconds;
    private float fallSeconds;

    /** 创建一个从 0 开始、升降均使用 1 秒平滑时间的控制器。 */
    public LusterSanityTransition() {
        this(0.0F, 1.0F, 1.0F);
    }

    /**
     * @param initialValue 初始强度，限制到 0～2
     * @param riseSeconds 增强响应时间（秒）
     * @param fallSeconds 减弱响应时间（秒）
     */
    public LusterSanityTransition(float initialValue, float riseSeconds, float fallSeconds) {
        value = clampIntensity(initialValue);
        target = value;
        setResponseTimes(riseSeconds, fallSeconds);
    }

    /** 设置新目标；当前值不会立即跳变。 */
    public LusterSanityTransition setTarget(float target) {
        if (Float.isFinite(target)) {
            this.target = clampIntensity(target);
        }
        return this;
    }

    /**
     * 在当前目标强度上叠加有符号增量；正数增强，负数减弱。
     * 结果自动限制到 0～2，当前值仍会平滑追踪新目标。
     */
    public LusterSanityTransition add(float delta) {
        if (Float.isFinite(delta)) {
            target = clampIntensity(target + delta);
        }
        return this;
    }

    /** 将目标强度增加指定的非负量。 */
    public LusterSanityTransition increase(float amount) {
        if (Float.isFinite(amount) && amount > 0.0F) {
            add(amount);
        }
        return this;
    }

    /** 将目标强度减少指定的非负量。 */
    public LusterSanityTransition decrease(float amount) {
        if (Float.isFinite(amount) && amount > 0.0F) {
            add(-amount);
        }
        return this;
    }

    /** 分别设置增强和减弱响应时间。 */
    public LusterSanityTransition setResponseTimes(float riseSeconds, float fallSeconds) {
        this.riseSeconds = sanitizeSeconds(riseSeconds);
        this.fallSeconds = sanitizeSeconds(fallSeconds);
        return this;
    }

    /**
     * 向目标值更新一帧。返回值可直接传给 {@link LusterSanityShader#draw}。
     *
     * @param deltaSeconds 距上次更新的真实时间（秒）
     */
    public float update(float deltaSeconds) {
        if (!Float.isFinite(deltaSeconds) || deltaSeconds <= 0.0F || value == target) {
            return value;
        }
        float response = target > value ? riseSeconds : fallSeconds;
        if (response <= 0.0F) {
            value = target;
            return value;
        }

        // 帧率无关的指数阻尼：response 秒后完成约 95% 的变化。
        float blend = 1.0F - (float) Math.exp(-3.0F * deltaSeconds / response);
        value += (target - value) * blend;
        if (Math.abs(target - value) < 0.0001F) {
            value = target;
        }
        return value;
    }

    /** 立即设置当前值和目标值。 */
    public LusterSanityTransition snapTo(float value) {
        if (Float.isFinite(value)) {
            this.value = clampIntensity(value);
            this.target = this.value;
        }
        return this;
    }

    public float getValue() {
        return value;
    }

    public float getTarget() {
        return target;
    }

    private static float sanitizeSeconds(float seconds) {
        return Float.isFinite(seconds) ? Math.max(0.0F, seconds) : 1.0F;
    }

    private static float clampIntensity(float intensity) {
        return Math.min(Math.max(intensity, 0.0F), 2.0F);
    }
}
