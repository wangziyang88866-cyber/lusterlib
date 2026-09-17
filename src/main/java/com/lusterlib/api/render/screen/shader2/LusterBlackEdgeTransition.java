package com.lusterlib.api.render.screen.shader2;

/**
 * 由调用方持有的黑白轮廓强度控制器。
 *
 * <p>支持指定确切时长的淡入/淡出、包含停留时间的一次性播放，以及由任意事件增加或减少目标值。
 * 本类不订阅事件，也不保存玩家或世界状态。</p>
 */
public final class LusterBlackEdgeTransition {

    private enum Phase {
        IDLE,
        TRANSITION,
        FADE_IN,
        HOLD,
        FADE_OUT
    }

    private float value;
    private float startValue;
    private float target;
    private float elapsedSeconds;
    private float durationSeconds;
    private float sequenceHoldSeconds;
    private float sequenceFadeOutSeconds;
    private Phase phase = Phase.IDLE;

    /** 创建一个从无效果开始的控制器。 */
    public LusterBlackEdgeTransition() {
        this(0.0F);
    }

    /**
     * 创建指定初始强度的控制器。
     *
     * @param initialValue 初始强度，限制到 0～1
     */
    public LusterBlackEdgeTransition(float initialValue) {
        value = clamp(initialValue);
        startValue = value;
        target = value;
    }

    /**
     * 在指定秒数内平滑过渡到目标强度；0 秒表示立即到达。
     *
     * @param target 目标强度，限制到 0～1
     * @param durationSeconds 过渡秒数
     * @return 当前控制器
     */
    public LusterBlackEdgeTransition transitionTo(float target, float durationSeconds) {
        if (!Float.isFinite(target) || !Float.isFinite(durationSeconds)) {
            return this;
        }
        beginPhase(Phase.TRANSITION, clamp(target), Math.max(0.0F, durationSeconds));
        settleZeroLengthPhases();
        return this;
    }

    /**
     * 在指定秒数内从当前值淡入到完整效果。
     *
     * @param durationSeconds 淡入秒数
     * @return 当前控制器
     */
    public LusterBlackEdgeTransition fadeIn(float durationSeconds) {
        return transitionTo(1.0F, durationSeconds);
    }

    /**
     * 在指定秒数内从当前值淡出到无效果。
     *
     * @param durationSeconds 淡出秒数
     * @return 当前控制器
     */
    public LusterBlackEdgeTransition fadeOut(float durationSeconds) {
        return transitionTo(0.0F, durationSeconds);
    }

    /**
     * 播放一次“淡入、保持、淡出”序列。三个参数均为秒；负数按 0 处理。
     * 每次调用都会从当前可见强度开始一个新序列，因此触发事件通常只应调用一次。
     *
     * @param fadeInSeconds 淡入秒数
     * @param holdSeconds 完整效果保持秒数
     * @param fadeOutSeconds 淡出秒数
     * @return 当前控制器
     */
    public LusterBlackEdgeTransition play(float fadeInSeconds, float holdSeconds, float fadeOutSeconds) {
        if (!Float.isFinite(fadeInSeconds)
                || !Float.isFinite(holdSeconds)
                || !Float.isFinite(fadeOutSeconds)) {
            return this;
        }
        sequenceHoldSeconds = Math.max(0.0F, holdSeconds);
        sequenceFadeOutSeconds = Math.max(0.0F, fadeOutSeconds);
        beginPhase(Phase.FADE_IN, 1.0F, Math.max(0.0F, fadeInSeconds));
        settleZeroLengthPhases();
        return this;
    }

    /**
     * 由事件在当前目标上增加有符号强度，并在指定秒数内到达；结果限制到 0～1。
     *
     * @param delta 有符号强度增量
     * @param durationSeconds 过渡秒数
     * @return 当前控制器
     */
    public LusterBlackEdgeTransition add(float delta, float durationSeconds) {
        if (!Float.isFinite(delta) || !Float.isFinite(durationSeconds)) {
            return this;
        }
        float base = isActive() ? target : value;
        return transitionTo(base + delta, durationSeconds);
    }

    /**
     * 由事件立即增加有符号强度；正数增强，负数减弱。
     *
     * @param delta 有符号强度增量
     * @return 当前控制器
     */
    public LusterBlackEdgeTransition add(float delta) {
        if (Float.isFinite(delta)) {
            snapTo(value + delta);
        }
        return this;
    }

    /**
     * 每个渲染帧调用一次，并把返回值直接传给 {@link LusterBlackEdgeShader#draw}。
     *
     * @param deltaSeconds 距上次显示帧的秒数
     * @return 当前平滑强度
     */
    public float update(float deltaSeconds) {
        if (!Float.isFinite(deltaSeconds) || deltaSeconds <= 0.0F) {
            return value;
        }

        float remaining = deltaSeconds;
        while (phase != Phase.IDLE && remaining > 0.0F) {
            float phaseRemaining = durationSeconds - elapsedSeconds;
            if (phaseRemaining <= 0.0F) {
                finishPhase();
                continue;
            }

            float consumed = Math.min(remaining, phaseRemaining);
            elapsedSeconds += consumed;
            remaining -= consumed;

            if (phase != Phase.HOLD) {
                float progress = Math.min(elapsedSeconds / durationSeconds, 1.0F);
                float eased = progress * progress * (3.0F - 2.0F * progress);
                value = startValue + (target - startValue) * eased;
            }

            if (elapsedSeconds >= durationSeconds) {
                finishPhase();
            }
        }
        return value;
    }

    /**
     * 立即设置当前值并取消尚未完成的过渡或播放序列。
     *
     * @param value 新强度，限制到 0～1
     * @return 当前控制器
     */
    public LusterBlackEdgeTransition snapTo(float value) {
        if (Float.isFinite(value)) {
            this.value = clamp(value);
            startValue = this.value;
            target = this.value;
            elapsedSeconds = 0.0F;
            durationSeconds = 0.0F;
            sequenceHoldSeconds = 0.0F;
            sequenceFadeOutSeconds = 0.0F;
            phase = Phase.IDLE;
        }
        return this;
    }

    /**
     * 获取当前值。
     *
     * @return 当前可见强度
     */
    public float getValue() {
        return value;
    }

    /**
     * 获取当前目标。
     *
     * @return 当前阶段的目标强度
     */
    public float getTarget() {
        return target;
    }

    /**
     * 检查控制器是否仍在运行。
     *
     * @return 是否仍有普通过渡或 play 序列正在进行
     */
    public boolean isActive() {
        return phase != Phase.IDLE;
    }

    private void beginPhase(Phase newPhase, float newTarget, float seconds) {
        phase = newPhase;
        startValue = value;
        target = newTarget;
        elapsedSeconds = 0.0F;
        durationSeconds = seconds;
    }

    private void finishPhase() {
        if (phase != Phase.HOLD) {
            value = target;
        }

        switch (phase) {
            case FADE_IN -> beginPhase(Phase.HOLD, 1.0F, sequenceHoldSeconds);
            case HOLD -> beginPhase(Phase.FADE_OUT, 0.0F, sequenceFadeOutSeconds);
            case FADE_OUT, TRANSITION -> {
                phase = Phase.IDLE;
                elapsedSeconds = 0.0F;
                durationSeconds = 0.0F;
            }
            case IDLE -> {
            }
        }
        settleZeroLengthPhases();
    }

    /** 立即跨过所有 0 秒阶段，并保证 play(0, 0, 0) 同步结束。 */
    private void settleZeroLengthPhases() {
        for (int skipped = 0; skipped < 3 && phase != Phase.IDLE && durationSeconds == 0.0F; skipped++) {
            Phase completed = phase;
            if (completed != Phase.HOLD) {
                value = target;
            }
            if (completed == Phase.FADE_IN) {
                beginPhase(Phase.HOLD, 1.0F, sequenceHoldSeconds);
            } else if (completed == Phase.HOLD) {
                beginPhase(Phase.FADE_OUT, 0.0F, sequenceFadeOutSeconds);
            } else {
                phase = Phase.IDLE;
            }
        }
    }

    private static float clamp(float value) {
        return Math.min(Math.max(value, 0.0F), 1.0F);
    }
}
