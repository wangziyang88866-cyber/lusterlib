package com.lusterlib.api.text.halo1;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 供其他模组调用的文字光晕 API。
 *
 * <p>仅可从客户端渲染回调调用。调用点当前的 {@link GuiGraphics#pose()} 变换会同时
 * 应用到光晕和正文，适用于动态位移、缩放与旋转的文字。</p>
 */
@OnlyIn(Dist.CLIENT)
public final class LusterTextHaloRenderer {

    private LusterTextHaloRenderer() {
    }

    public static void drawHalo(GuiGraphics graphics, Font font, String text, float x, float y, LusterTextHaloStyle style) {
        com.lusterlib.client.text.halo1.LusterTextHaloRenderer.drawHalo(graphics, font, text, x, y, style);
    }

    public static void drawHalo(GuiGraphics graphics, Font font, Component text, float x, float y, LusterTextHaloStyle style) {
        com.lusterlib.client.text.halo1.LusterTextHaloRenderer.drawHalo(graphics, font, text, x, y, style);
    }

    public static void drawHalo(GuiGraphics graphics, Font font, FormattedCharSequence text, float x, float y, LusterTextHaloStyle style) {
        com.lusterlib.client.text.halo1.LusterTextHaloRenderer.drawHalo(graphics, font, text, x, y, style);
    }

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
        return com.lusterlib.client.text.halo1.LusterTextHaloRenderer.drawHaloText(
                graphics, font, text, x, y, textColor, dropShadow, style
        );
    }
}
