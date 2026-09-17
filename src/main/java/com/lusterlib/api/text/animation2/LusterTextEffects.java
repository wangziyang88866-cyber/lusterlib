package com.lusterlib.api.text.animation2;

/** animation2 内置文字效果的工厂入口。 */
public final class LusterTextEffects {

    private LusterTextEffects() {
    }

    /** 创建可配置的整体连续波形效果。 */
    public static LusterTextWave wave() {
        return new LusterTextWave();
    }
}
