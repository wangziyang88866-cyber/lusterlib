package com.lusterlib.api.render.screen.overlay2;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/** 供其他模组调用的边缘侵蚀式诡谲全屏光影 API。 */
@OnlyIn(Dist.CLIENT)
public final class LusterEldritchOverlay {

    private LusterEldritchOverlay() {
    }

    /**
     * 绘制从屏幕边缘向中心流动蔓延的程序化噪声光影。
     *
     * @param color ARGB 颜色，例如 {@code 0x904A1A68}
     * @param intensity 效果程度；0 为不可见，1 为完整强度
     * @param time 连续时间（秒），由调用方控制
     */
    public static void draw(GuiGraphics graphics, int color, float intensity, float time) {
        com.lusterlib.client.render.screen.overlay2.LusterEldritchOverlay.draw(
                graphics, color, intensity, time
        );
    }
}
