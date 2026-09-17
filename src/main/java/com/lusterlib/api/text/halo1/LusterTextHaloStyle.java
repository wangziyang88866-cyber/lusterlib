package com.lusterlib.api.text.halo1;

/**
 * 文字光晕的可变配置。
 *
 * <p>半径和模糊度均以 GUI 像素为单位。{@code blur == 0} 会得到清晰描边；
 * 更高的值会在半径内增加渐隐层。</p>
 */
public final class LusterTextHaloStyle {

    public static final int DEFAULT_COLOR = 0xFF66CCFF;

    private float radius = 4.0F;
    private float blur = 3.0F;
    private int color = DEFAULT_COLOR;
    private boolean enabled = true;

    public float getRadius() {
        return radius;
    }

    public LusterTextHaloStyle setRadius(float radius) {
        this.radius = Math.max(0.0F, radius);
        return this;
    }

    public float getBlur() {
        return blur;
    }

    public LusterTextHaloStyle setBlur(float blur) {
        this.blur = Math.max(0.0F, blur);
        return this;
    }

    /** 设置 ARGB 颜色，例如 {@code 0xCC66CCFF}。 */
    public LusterTextHaloStyle setColor(int color) {
        this.color = color;
        return this;
    }

    public int getColor() {
        return color;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LusterTextHaloStyle setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
