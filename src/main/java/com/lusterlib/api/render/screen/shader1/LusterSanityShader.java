package com.lusterlib.api.render.screen.shader1;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 供其他模组调用的理智降低全屏着色 API。
 *
 * <p>调用方持有强度与时间状态，因此可以自由地随时间递增，
 * 也可以根据游戏事件即时增减。本 API 不保存玩家或理智值。</p>
 */
@OnlyIn(Dist.CLIENT)
public final class LusterSanityShader {

    private LusterSanityShader() {
    }

    /**
     * 对已经绘制的画面应用锐化、低饱和度、高对比度、高曝光、深阴影、
     * 交叉处理、时域运动模糊、噪点、整体色偏与轻微色散。
     *
     * @param graphics 当前 GUI 渲染事件提供的全屏绘图上下文
     * @param intensity 效果强度；0 为无效果，1 为推荐完整强度，最多按 2 处理
     * @param timeSeconds 连续时间（秒），用于驱动色散、运动模糊方向与噪点
     */
    public static void draw(GuiGraphics graphics, float intensity, float timeSeconds) {
        com.lusterlib.client.render.screen.shader1.LusterSanityShader.draw(
                graphics, intensity, timeSeconds
        );
    }

    /**
     * 释放复用的当前画面与历史画面缓冲。
     * 仅在客户端渲染线程调用；后续 draw 会自动重建。
     */
    public static void releaseResources() {
        com.lusterlib.client.render.screen.shader1.LusterSanityShader.releaseResources();
    }
}
