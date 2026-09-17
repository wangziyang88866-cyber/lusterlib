/*
package com.lusterlib.client.render.screen.example;

import com.lusterlib.LusterLib;
import com.lusterlib.api.render.screen.overlay1.LusterHeatWaveOverlay;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = LusterLib.MODID, value = Dist.CLIENT)
public final class LusterHeatWaveOverlayExample {

    private static final long START_NANOS = System.nanoTime();
    private static final int HEAT_WAVE_COLOR = 0x78FF9A45;
    private static final float HEAT_WAVE_INTENSITY = 9.0F;

    private LusterHeatWaveOverlayExample() {
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }

        float time = (System.nanoTime() - START_NANOS) / 1_000_000_000.0F;
        LusterHeatWaveOverlay.draw(event.getGuiGraphics(), HEAT_WAVE_COLOR, HEAT_WAVE_INTENSITY, time);
    }
}
*/