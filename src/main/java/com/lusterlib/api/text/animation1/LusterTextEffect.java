package com.lusterlib.api.text.animation1;

/**
 * animation1 文字效果的统一调用接口。
 */
@FunctionalInterface
public interface LusterTextEffect {

    LusterTextTransform transform(int characterIndex, int characterCount, float timeSeconds);
}
