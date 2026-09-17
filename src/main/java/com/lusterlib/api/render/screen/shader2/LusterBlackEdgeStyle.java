package com.lusterlib.api.render.screen.shader2;

/** 黑色轮廓着色器的颜色配置。实例由调用方持有，可以复用。 */
public final class LusterBlackEdgeStyle {

    private int edgeColor = 0xFFFFFF;
    private int interiorColor = 0x000000;

    /** 创建默认的黑色内部、白色边缘样式。 */
    public LusterBlackEdgeStyle() {
    }

    /**
     * 设置轮廓及轮廓最亮处的 RGB 颜色；高 8 位会被忽略。
     *
     * @param color {@code 0xRRGGBB} 颜色
     * @return 当前样式，便于链式设置
     */
    public LusterBlackEdgeStyle setEdgeColor(int color) {
        edgeColor = color & 0xFFFFFF;
        return this;
    }

    /**
     * 设置远离轮廓区域的 RGB 颜色；高 8 位会被忽略。
     *
     * @param color {@code 0xRRGGBB} 颜色
     * @return 当前样式，便于链式设置
     */
    public LusterBlackEdgeStyle setInteriorColor(int color) {
        interiorColor = color & 0xFFFFFF;
        return this;
    }

    /**
     * 获取边缘颜色。
     *
     * @return 当前边缘 RGB 颜色
     */
    public int getEdgeColor() {
        return edgeColor;
    }

    /**
     * 获取内部颜色。
     *
     * @return 当前内部 RGB 颜色
     */
    public int getInteriorColor() {
        return interiorColor;
    }
}
