package com.lusterlib.api.render.screen.overlay1;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 供其他模组调用的全屏热浪光影 API。
 *
 * <p>仅可在客户端渲染回调中调用。本 API 没有全局效果状态；颜色、强度和时间
 * 均由每次调用传入，因此不同调用方可以完全独立地控制效果。</p>
 */
@OnlyIn(Dist.CLIENT)
public final class LusterHeatWaveOverlay {

    private LusterHeatWaveOverlay() {
    }

    /**
     * 绘制由连续程序化噪声生成的全屏流动光影。
     *
     * @param color ARGB 颜色，例如 {@code 0x90FF8833}
     * @param intensity 效果程度；0 为不可见，1 为完整强度
     * @param time 连续时间（秒）；可传入自定义时间以控制速度、暂停或倒放
     */
    public static void draw(GuiGraphics graphics, int color, float intensity, float time) {
        com.lusterlib.client.render.screen.overlay1.LusterHeatWaveOverlay.draw(
                graphics, color, intensity, time
        );
    }
}
