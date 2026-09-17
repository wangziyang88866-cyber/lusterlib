package com.lusterlib.client.render.screen.overlay2;

import net.minecraft.client.gui.GuiGraphics;

/** Directly drawn, breathing edge overlay with no custom shader dependency. */
public final class LusterEldritchOverlay {

    private static final int LAYER_COUNT = 6;

    private LusterEldritchOverlay() {
    }

    public static void draw(GuiGraphics graphics, int color, float intensity, float time) {
        float strength = Math.min(Math.max(intensity, 0.0F), 2.0F);
        int sourceAlpha = color >>> 24;
        if (strength <= 0.0F || sourceAlpha == 0) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        float shortestSide = Math.min(width, height);
        float breath = 0.72F + 0.28F * (float) Math.sin(time * 0.65F);
        int clear = withAlpha(color, 0);

        for (int layer = 0; layer < LAYER_COUNT; layer++) {
            float progress = (layer + 1.0F) / LAYER_COUNT;
            int size = Math.max(2, Math.round(shortestSide * (0.026F + progress * 0.052F) * breath));
            int alpha = Math.round(sourceAlpha * strength * breath * (0.105F - layer * 0.012F));
            int edge = withAlpha(color, alpha);

            graphics.fillGradient(0, 0, width, size, edge, clear);
            graphics.fillGradient(0, height - size, width, height, clear, edge);
            graphics.fillGradient(0, 0, size, height, edge, clear);
            graphics.fillGradient(width - size, 0, width, height, clear, edge);
        }
    }

    private static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (Math.min(255, Math.max(0, alpha)) << 24);
    }
}
