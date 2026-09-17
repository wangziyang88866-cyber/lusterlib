package com.lusterlib.api.render.screen.glitch1;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/** 供其他模组调用的真实画面分片错位、玻璃裂缝及边缘反光 API。 */
@OnlyIn(Dist.CLIENT)
public final class LusterScreenFracture {

    private LusterScreenFracture() {
    }

    /**
     * 复制当前画面并按碎片网格旋转、平移采样；裂缝及反光与分割位置一致。
     *
     * @param intensity 裂痕程度；0 为不可见，1 为推荐完整强度
     * @param time 调用方的连续时间（秒）；图案保持稳定
     */
    public static void draw(GuiGraphics graphics, float intensity, float time) {
        com.lusterlib.client.render.screen.glitch1.LusterScreenFracture.draw(graphics, intensity, time);
    }

    /**
     * 绘制限时裂痕效果；在总时长最后 30% 自动平滑淡出。
     *
     * @param elapsed 效果开始后已经过的秒数
     * @param duration 总持续时间（秒）；小于等于 0 则持续显示
     */
    public static void draw(GuiGraphics graphics, float intensity, float elapsed, float duration) {
        com.lusterlib.client.render.screen.glitch1.LusterScreenFracture.draw(
                graphics, intensity, elapsed, duration
        );
    }

    /** 使用调用方持有的配置绘制持续效果。 */
    public static void draw(GuiGraphics graphics, float intensity, float time, LusterScreenFractureStyle style) {
        com.lusterlib.client.render.screen.glitch1.LusterScreenFracture.draw(graphics, intensity, time, style);
    }

    /** 使用调用方持有的配置绘制限时效果；最后 30% 平滑淡出。 */
    public static void draw(
            GuiGraphics graphics, float intensity, float elapsed, float duration, LusterScreenFractureStyle style
    ) {
        com.lusterlib.client.render.screen.glitch1.LusterScreenFracture.draw(graphics, intensity, elapsed, duration, style);
    }

    /** 在客户端渲染线程调用；释放复用的屏幕副本和几何缓存，后续 draw 可自动重建。 */
    public static void releaseResources() {
        com.lusterlib.client.render.screen.glitch1.LusterScreenFracture.releaseResources();
    }
}
