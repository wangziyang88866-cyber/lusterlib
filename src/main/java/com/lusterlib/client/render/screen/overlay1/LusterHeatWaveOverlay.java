package com.lusterlib.client.render.screen.overlay1;

import net.minecraft.client.gui.GuiGraphics;

/**
 * A lightweight, directly drawn heat-light overlay.  It deliberately does
 * not require a framebuffer copy or a custom shader, so it is safe to call
 * during ordinary GUI rendering.
 */
public final class LusterHeatWaveOverlay {

    private static final int BAND_COUNT = 9;

    private LusterHeatWaveOverlay() {
    }

    public static void draw(GuiGraphics graphics, int color, float intensity, float time) {
        float strength = Math.min(Math.max(intensity, 0.0F), 2.0F);
        int sourceAlpha = color >>> 24;
        if (strength <= 0.0F || sourceAlpha == 0) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        float baseHalfHeight = Math.max(8.0F, height / 16.0F);

        for (int band = 0; band < BAND_COUNT; band++) {
            float phase = time * 1.35F + band * 1.71F;
            float centerY = (band + 0.5F) * height / BAND_COUNT
                    + (float) Math.sin(phase) * height * 0.035F;
            float pulse = 0.5F + 0.5F * (float) Math.sin(phase * 1.37F + 0.8F);
            float halfHeight = baseHalfHeight * (0.72F + pulse * 0.58F);
            int alpha = Math.round(sourceAlpha * strength * (0.045F + pulse * 0.070F));
            int middleColor = withAlpha(color, alpha);
            int transparentColor = withAlpha(color, 0);
            int top = Math.max(0, Math.round(centerY - halfHeight));
            int middle = Math.round(centerY);
            int bottom = Math.min(height, Math.round(centerY + halfHeight));

            graphics.fillGradient(0, top, width, middle, transparentColor, middleColor);
            graphics.fillGradient(0, middle, width, bottom, middleColor, transparentColor);
        }
    }

    private static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (Math.min(255, Math.max(0, alpha)) << 24);
    }
}
