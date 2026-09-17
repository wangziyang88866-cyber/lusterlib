/*package com.lusterlib.client.render.screen.example;

import com.lusterlib.LusterLib;
import com.lusterlib.api.render.screen.shader2.LusterBlackEdgeShader;
import com.lusterlib.api.render.screen.shader2.LusterBlackEdgeStyle;
import com.lusterlib.api.render.screen.shader2.LusterBlackEdgeTransition;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

/** 仅用于开发环境查看 shader2；最终 JAR 会排除整个 example 包。 */
/*@EventBusSubscriber(modid = LusterLib.MODID, value = Dist.CLIENT)
public final class LusterBlackEdgeShaderExample {

    private static final LusterBlackEdgeTransition TRANSITION = new LusterBlackEdgeTransition();
    private static final LusterBlackEdgeStyle STYLE = new LusterBlackEdgeStyle()
            .setInteriorColor(0x000000)
            .setEdgeColor(0xFFFFFF);
    private static long previousFrameNanos = -1L;
    private static boolean resourcesInUse;

    private LusterBlackEdgeShaderExample() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            resetTest();
            return;
        }
        if (minecraft.screen == null) {
            render(event.getGuiGraphics());
        }
    }

    /** Screen 绘制完成后处理，暂停菜单、物品栏等界面也会保留效果。 */
    /*@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (Minecraft.getInstance().player != null) {
            render(event.getGuiGraphics());
        }
    }

    private static void render(GuiGraphics graphics) {
        long now = System.nanoTime();
        if (previousFrameNanos < 0L) {
            previousFrameNanos = now;
            // 4 秒淡入，完整保持 6 秒，再用 4 秒淡出；结束后自动重新播放。
            TRANSITION.play(4.0F, 6.0F, 4.0F);
        }
        float deltaSeconds = Math.min((now - previousFrameNanos) / 1_000_000_000.0F, 0.25F);
        previousFrameNanos = now;

        float intensity = TRANSITION.update(deltaSeconds);
        if (intensity > 0.0F) {
            LusterBlackEdgeShader.draw(graphics, intensity, STYLE);
            resourcesInUse = true;
        } else {
            releaseTestResources();
            if (!TRANSITION.isActive()) {
                TRANSITION.play(4.0F, 6.0F, 4.0F);
            }
        }
    }

    private static void resetTest() {
        previousFrameNanos = -1L;
        TRANSITION.snapTo(0.0F);
        releaseTestResources();
    }

    private static void releaseTestResources() {
        if (resourcesInUse) {
            LusterBlackEdgeShader.releaseResources();
            resourcesInUse = false;
        }
    }
}*/
