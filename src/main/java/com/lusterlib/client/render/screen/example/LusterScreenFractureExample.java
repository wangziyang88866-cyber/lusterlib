/*package com.lusterlib.client.render.screen.example;

import com.lusterlib.api.render.screen.glitch1.LusterScreenFracture;
import com.lusterlib.api.render.screen.glitch1.LusterScreenFractureStyle;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "lusterlib", value = Dist.CLIENT)
public final class LusterScreenFractureExample {

    private static final float INTENSITY = 1.0F;
    private static final float DURATION_SECONDS = 8.0F;

    // 测试配置由调用方持有，正式 API 不负责播放或设置效果。
    private static final LusterScreenFractureStyle STYLE = new LusterScreenFractureStyle()
            .setSeed(20260915L)
            .setFragmentCount(18)
            .setCrackWidth(0.1F)
            .setGlowRadius(16.0F)
            .setPaneOpacity(0.001F)
            .setRefractionOffset(64.0F)
            .setFragmentRotation(32.0F)
            .setCrackColor(0xA51A2530)
            .setGlowColor(0x78E4F3FF);

    /** -1 表示尚未开始；每次重新进入世界启动一次测试。 */
    /*private static long startNanos = -1L;
    private static boolean resourcesInUse;

    private LusterScreenFractureExample() {
    }

    // 等其他 HUD 测试绘制结束，再统一按玻璃碎片处理它们及场景。
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGui(RenderGuiEvent.Post event) {
        // 等到玩家真正进入世界再启动，以免在主菜单/载入阶段耗尽测试时长。
        if (Minecraft.getInstance().player == null) {
            releaseTestResources();
            startNanos = -1L;
            return;
        }

        if (startNanos < 0L) {
            startNanos = System.nanoTime();
        }

        float time = (System.nanoTime() - startNanos) / 1_000_000_000.0F;
        if (time > DURATION_SECONDS) {
            releaseTestResources();
            return;
        }

        LusterScreenFracture.draw(event.getGuiGraphics(), INTENSITY, time, DURATION_SECONDS, STYLE);
        resourcesInUse = true;
    }

    private static void releaseTestResources() {
        if (resourcesInUse) {
            LusterScreenFracture.releaseResources();
            resourcesInUse = false;
        }
    }
}*/
