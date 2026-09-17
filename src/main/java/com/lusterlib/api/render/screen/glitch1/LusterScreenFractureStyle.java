package com.lusterlib.api.render.screen.glitch1;

/** 调用方持有的玻璃裂痕配置。尺寸以 GUI 单位计，不保存计时或播放状态。 */
public final class LusterScreenFractureStyle {

    private long seed = 20260915L;
    private int fragmentCount = 18;
    private float crackWidth = 0.42F;
    private float glowRadius = 8.0F;
    private float paneOpacity = 0.035F;
    private int crackColor = 0xA51A2530;
    private int glowColor = 0x78E4F3FF;
    private float refractionOffset = 8.0F;
    private float fragmentRotation = 3.0F;

    public long getSeed() {
        return seed;
    }

    /** 同一 seed 和碎片数量保持同一图案；不要逐帧改变 seed。 */
    public LusterScreenFractureStyle setSeed(long seed) {
        this.seed = seed;
        return this;
    }

    public int getFragmentCount() {
        return fragmentCount;
    }

    /**
     * 设置碎片密集程度；数值越大，碎片和裂缝越密集。
     * 适合常规画面的范围为 12～32，允许范围为 2～64。
     */
    public LusterScreenFractureStyle setFragmentCount(int count) {
        this.fragmentCount = Math.max(2, Math.min(64, count));
        return this;
    }

    public float getCrackWidth() {
        return crackWidth;
    }

    public LusterScreenFractureStyle setCrackWidth(float width) {
        this.crackWidth = finiteRange(width, 0.05F, 8.0F);
        return this;
    }

    public float getGlowRadius() {
        return glowRadius;
    }

    public LusterScreenFractureStyle setGlowRadius(float radius) {
        this.glowRadius = finiteRange(radius, 0.0F, 32.0F);
        return this;
    }

    public float getPaneOpacity() {
        return paneOpacity;
    }

    /** 碎片表面的反光程度；0 关闭表面反光，不影响画面错位和裂纹。 */
    public LusterScreenFractureStyle setPaneOpacity(float opacity) {
        this.paneOpacity = finiteRange(opacity, 0.0F, 1.0F);
        return this;
    }

    public int getCrackColor() {
        return crackColor;
    }

    public LusterScreenFractureStyle setCrackColor(int argb) {
        this.crackColor = argb;
        return this;
    }

    public int getGlowColor() {
        return glowColor;
    }

    public LusterScreenFractureStyle setGlowColor(int argb) {
        this.glowColor = argb;
        return this;
    }

    public float getRefractionOffset() {
        return refractionOffset;
    }

    /** 碎片内部画面错位幅度，GUI 单位；每块碎片的方向固定且不同。 */
    public LusterScreenFractureStyle setRefractionOffset(float offset) {
        this.refractionOffset = finiteRange(offset, 0.0F, 64.0F);
        return this;
    }

    public float getFragmentRotation() {
        return fragmentRotation;
    }

    /** 碎片内部画面倾斜幅度，单位为度；0 只进行平移。 */
    public LusterScreenFractureStyle setFragmentRotation(float degrees) {
        this.fragmentRotation = finiteRange(degrees, 0.0F, 20.0F);
        return this;
    }

    private static float finiteRange(float value, float minimum, float maximum) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException("Fracture style values must be finite");
        }
        return Math.max(minimum, Math.min(maximum, value));
    }
}
