/*
package com.lusterlib.client.render.screen.example;

import com.lusterlib.LusterLib;
import com.lusterlib.api.render.screen.shader1.LusterSanityShader;
import com.lusterlib.api.render.screen.shader1.LusterSanityTransition;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

// 仅用于开发环境预览理智降低着色，不保存任何正式游戏状态。
@EventBusSubscriber(modid = LusterLib.MODID, value = Dist.CLIENT)
public final class LusterSanityShaderExample {

    private static final float SWITCH_SECONDS = 90.0F;
    private static final LusterSanityTransition TRANSITION =
            new LusterSanityTransition(0.0F, 8.0F, 5.0F);

    // -1 表示尚未进入世界；每次重新进入世界会重新播放。
    /*private static long startNanos = -1L;
    private static long previousFrameNanos = -1L;
    private static boolean resourcesInUse;

    private LusterSanityShaderExample() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            resetTest();
            return;
        }
        if (minecraft.screen == null) {
            draw(event.getGuiGraphics());
        }
    }

    // 在暂停菜单、物品栏等 Screen 完成后再着色，让界面也受效果影响。
    /*@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (Minecraft.getInstance().player != null) {
            draw(event.getGuiGraphics());
        }
    }

    private static void draw(net.minecraft.client.gui.GuiGraphics graphics) {
        long now = System.nanoTime();
        if (startNanos < 0L) {
            startNanos = now;
            previousFrameNanos = now;
        }

        float elapsedSeconds = (now - startNanos) / 1_000_000_000.0F;
        float deltaSeconds = Math.min((now - previousFrameNanos) / 1_000_000_000.0F, 0.25F);
        previousFrameNanos = now;

        // 每 12 秒切换一次目标，演示平滑增强与平滑减弱。
        boolean increasing = ((int) (elapsedSeconds / SWITCH_SECONDS) & 1) == 0;
        TRANSITION.setTarget(increasing ? 10.0F : 0.0F);
        float intensity = TRANSITION.update(deltaSeconds);

        if (intensity > 0.0F) {
            LusterSanityShader.draw(graphics, intensity* 2.0F, elapsedSeconds);
            resourcesInUse = true;
        } else if (resourcesInUse) {
            LusterSanityShader.releaseResources();
            resourcesInUse = false;
        }
    }

    private static void resetTest() {
        startNanos = -1L;
        previousFrameNanos = -1L;
        TRANSITION.snapTo(0.0F);
        if (resourcesInUse) {
            LusterSanityShader.releaseResources();
            resourcesInUse = false;
        }
    }
}
*/
