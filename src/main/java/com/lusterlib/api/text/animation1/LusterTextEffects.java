package com.lusterlib.api.text.animation1;

/** animation1 内置文字效果的工厂入口。 */
public final class LusterTextEffects {

    private LusterTextEffects() {
    }

    /** 创建可配置的“反胃/蠕动”文字效果。 */
    public static LusterTextRetching retching() {
        return new LusterTextRetching();
    }
}
