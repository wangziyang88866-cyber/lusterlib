package com.lusterlib.client.text.effect.animation2;

/**
 * 为文本中指定位置的字符计算局部变换。
 */
@FunctionalInterface
public interface LusterTextTransformSource {

    LusterTextTransform getTransform(int index, int length, float time);
}
