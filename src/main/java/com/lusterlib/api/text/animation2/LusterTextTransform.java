package com.lusterlib.api.text.animation2;

/**
 * 一个字符相对于原始位置的局部几何变换。
 * 此类只描述数据，不包含渲染逻辑。
 */
public final class LusterTextTransform {

    public static final LusterTextTransform IDENTITY =
            new LusterTextTransform(0.0F, 0.0F, 1.0F, 1.0F, 0.0F);

    private final float offsetX;
    private final float offsetY;
    private final float scaleX;
    private final float scaleY;
    private final float rotation;

    public LusterTextTransform(float offsetX, float offsetY, float scaleX, float scaleY, float rotation) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.rotation = rotation;
    }

    public float getOffsetX() { return offsetX; }
    public float getOffsetY() { return offsetY; }
    public float getScaleX() { return scaleX; }
    public float getScaleY() { return scaleY; }
    public float getRotation() { return rotation; }
}
