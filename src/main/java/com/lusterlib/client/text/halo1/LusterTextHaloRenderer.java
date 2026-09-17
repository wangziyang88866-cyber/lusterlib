package com.lusterlib.client.text.halo1;

import com.lusterlib.api.text.halo1.LusterTextHaloStyle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/**
 * 轻量的 GUI 文字光晕绘制器。
 *
 * <p>本类只在调用时围绕文字进行多次采样绘制，不创建帧缓冲或纹理，也不缓存文字
 * 坐标。因此它适合每帧位置、缩放或旋转都可能变化的文字。调用前若对
 * {@link GuiGraphics#pose()} 应用了变换，光晕和正文会使用完全相同的变换。</p>
 */
public final class LusterTextHaloRenderer {

    /** 每个半径层固定八次采样；最多八层，控制最坏情况下的 draw call 数。 */
    private static final int DIRECTIONS = 8;
    private static final int MAX_LAYERS = 8;
    private static final float[] COS = new float[DIRECTIONS];
    private static final float[] SIN = new float[DIRECTIONS];

    static {
        for (int index = 0; index < DIRECTIONS; index++) {
            double angle = Math.PI * 2.0D * index / DIRECTIONS;
            COS[index] = (float) Math.cos(angle);
            SIN[index] = (float) Math.sin(angle);
        }
    }

    private LusterTextHaloRenderer() {
    }

    /** 只绘制光晕；通常应在同一位置绘制正文之前调用。 */
    public static void drawHalo(
            GuiGraphics graphics,
            Font font,
            String text,
            float x,
            float y,
            LusterTextHaloStyle style
    ) {
        if (text.isEmpty() || !style.isEnabled() || style.getRadius() <= 0.0F) {
            return;
        }

        int sourceAlpha = style.getColor() >>> 24;
        if (sourceAlpha == 0) {
            return;
        }

        float radius = style.getRadius();
        int layers = style.getBlur() == 0.0F
                ? 1
                : Math.min(MAX_LAYERS, Math.max(1, (int) Math.ceil(style.getBlur())));

        // 由外向内绘制，内圈不透明度更高，形成平滑且不会覆盖正文的柔光。
        for (int layer = layers; layer >= 1; layer--) {
            float progress = (float) layer / layers;
            float distance = radius * progress;
            float opacity = layers == 1 ? 1.0F : (1.0F - progress * 0.72F);
            opacity *= opacity;
            int color = withAlpha(style.getColor(), Math.round(sourceAlpha * opacity));

            for (int direction = 0; direction < DIRECTIONS; direction++) {
                graphics.drawString(
                        font,
                        text,
                        x + COS[direction] * distance,
                        y + SIN[direction] * distance,
                        color,
                        false
                );
            }
        }
    }

    /** 已排版文字版本，适用于带样式或本地化后的文字。 */
    public static void drawHalo(
            GuiGraphics graphics,
            Font font,
            FormattedCharSequence text,
            float x,
            float y,
            LusterTextHaloStyle style
    ) {
        if (!style.isEnabled() || style.getRadius() <= 0.0F) {
            return;
        }

        int sourceAlpha = style.getColor() >>> 24;
        if (sourceAlpha == 0) {
            return;
        }

        float radius = style.getRadius();
        int layers = style.getBlur() == 0.0F
                ? 1
                : Math.min(MAX_LAYERS, Math.max(1, (int) Math.ceil(style.getBlur())));

        for (int layer = layers; layer >= 1; layer--) {
            float progress = (float) layer / layers;
            float distance = radius * progress;
            float opacity = layers == 1 ? 1.0F : (1.0F - progress * 0.72F);
            opacity *= opacity;
            int color = withAlpha(style.getColor(), Math.round(sourceAlpha * opacity));

            for (int direction = 0; direction < DIRECTIONS; direction++) {
                graphics.drawString(
                        font,
                        text,
                        x + COS[direction] * distance,
                        y + SIN[direction] * distance,
                        color,
                        false
                );
            }
        }
    }

    /** Component 版本，避免调用方为了光晕丢失本地化文字。 */
    public static void drawHalo(
            GuiGraphics graphics,
            Font font,
            Component text,
            float x,
            float y,
            LusterTextHaloStyle style
    ) {
        drawHalo(graphics, font, text.getVisualOrderText(), x, y, style);
    }

    /**
     * 在当前坐标及当前 PoseStack 变换下，先绘制光晕再绘制正文。
     *
     * @return 与 {@link GuiGraphics#drawString(Font, String, float, float, int, boolean)} 相同的右边界坐标
     */
    public static int drawHaloText(
            GuiGraphics graphics,
            Font font,
            String text,
            float x,
            float y,
            int textColor,
            boolean dropShadow,
            LusterTextHaloStyle style
    ) {
        drawHalo(graphics, font, text, x, y, style);
        return graphics.drawString(font, text, x, y, textColor, dropShadow);
    }

    private static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (Math.min(255, Math.max(0, alpha)) << 24);
    }
}
