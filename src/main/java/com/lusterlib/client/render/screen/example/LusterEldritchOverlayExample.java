/*
package com.lusterlib.client.render.screen.example;

import com.lusterlib.LusterLib;
import com.lusterlib.api.render.screen.overlay2.LusterEldritchOverlay;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/*用于预览 overlay2 的测试类；不属于正式效果的状态管理。 */
/*@EventBusSubscriber(modid = LusterLib.MODID, value = Dist.CLIENT)
public final class LusterEldritchOverlayExample {

    private static final long START_NANOS = System.nanoTime();

    private LusterEldritchOverlayExample() {
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }

        float time = (System.nanoTime() - START_NANOS) / 1_000_000_000.0F;
        LusterEldritchOverlay.draw(event.getGuiGraphics(), 0xA0420C68, 0.9F, time);
    }
}
*/