package com.lusterlib.api.render.screen.shader2;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 将已绘制画面覆盖为黑底、白色轮廓和向轮廓聚拢的灰白泛光。
 *
 * <p>这是无状态的客户端 API。调用方负责保存强度并在合适的渲染事件中每帧调用；
 * 方块、实体、粒子和 HUD 是否被处理，取决于调用发生在它们绘制之前还是之后。</p>
 */
@OnlyIn(Dist.CLIENT)
public final class LusterBlackEdgeShader {

    private LusterBlackEdgeShader() {
    }

    /**
     * 对当前完整帧应用黑白内发光轮廓效果。
     *
     * @param graphics 当前 GUI 渲染事件提供的绘图上下文
     * @param intensity 覆盖强度，限制到 0～1；0 保留原画面，1 完全覆盖
     */
    public static void draw(GuiGraphics graphics, float intensity) {
        com.lusterlib.client.render.screen.shader2.LusterBlackEdgeShader.draw(graphics, intensity);
    }

    /**
     * 使用自定义内部颜色和边缘颜色绘制效果。
     *
     * @param graphics 当前 GUI 渲染事件提供的绘图上下文
     * @param intensity 覆盖强度，限制到 0～1
     * @param style 颜色配置；传入 {@code null} 时使用黑色内部与白色边缘
     */
    public static void draw(GuiGraphics graphics, float intensity, LusterBlackEdgeStyle style) {
        com.lusterlib.client.render.screen.shader2.LusterBlackEdgeShader.draw(graphics, intensity, style);
    }

    /** 释放复用的画面缓冲；只能在客户端渲染线程调用。 */
    public static void releaseResources() {
        com.lusterlib.client.render.screen.shader2.LusterBlackEdgeShader.releaseResources();
    }
}
