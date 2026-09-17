# Luster Screen Fracture 1 API 说明

`glitch1` 复制当前真实画面，按同一套 Voronoi 碎片网格进行分片旋转和平移采样，
再绘制对应的多边形玻璃表面、边界裂缝和边缘泛光。
裂缝沿直线碎片边界连续连接，起点分散在屏幕各处；泛光与裂缝严格共用坐标。
使用浮点顶点和透明度插值实现平滑边缘。画面分片使用原版纹理着色器，无需自定义着色器文件。
目的碎片边界保持固定，只变换内部画面的 UV，因此画面错位的拼接位置与裂缝、泛光准确对应。
边缘碎片会按需轻微放大采样以覆盖旋转后的边角，避免黑色空洞和纹理边缘拉伸。

```java
import com.lusterlib.api.render.screen.glitch1.LusterScreenFracture;

float elapsedSeconds = (System.nanoTime() - startNanos) / 1_000_000_000.0F;
LusterScreenFracture.draw(event.getGuiGraphics(), 0.85F, elapsedSeconds, 3.0F);
```

仅在客户端 GUI/HUD 渲染事件调用，使用未经平移、旋转、缩放的全屏 `GuiGraphics.pose()`。
建议放在 `RenderGuiEvent.Post`，使已经绘制的 HUD 和场景一起随对应碎片错位；
若希望 HUD 保持清晰，则在 `RenderGuiEvent.Pre` 调用，让 HUD 在场景分片之后正常绘制。
不要在 Pre 和 Post 各调用一次，也不要逐碎片重复调用 API。`intensity` 为裂痕程度：0 不可见，
约 0.4 为轻微裂纹，0.7 为明显破碎感，1 以上适合强烈的短暂冲击。四参数版本的
`duration` 为总持续时间；最后 30% 自动平滑淡出，时间结束后不提交绘制。
若传入小于等于 0 的 `duration`，效果将持续显示。

## 自定义碎片、裂缝和泛光

配置应在调用方创建一次并保存，避免逐帧生成新配置。

```java
import com.lusterlib.api.render.screen.glitch1.LusterScreenFractureStyle;

private static final LusterScreenFractureStyle STYLE = new LusterScreenFractureStyle()
        .setSeed(42L)                  // 改变布局；同一 seed 图案稳定
        .setFragmentCount(18)          // 密集程度：数值越大越密集，范围 2～64
        .setCrackWidth(0.42F)          // 裂缝宽度，GUI 单位
        .setGlowRadius(8.0F)           // 与裂缝对齐的泛光半径，GUI 单位
        .setPaneOpacity(0.035F)        // 碎片面反光程度；0 关闭表面反光
        .setRefractionOffset(8.0F)     // 内部画面错位幅度，GUI 单位
        .setFragmentRotation(3.0F)     // 内部画面倾斜幅度，单位为度；0 只平移
        .setCrackColor(0xA51A2530)     // ARGB 暗色裂缝
        .setGlowColor(0x78E4F3FF);     // ARGB 边缘反光

// 限时效果：最后 30% 淡出
LusterScreenFracture.draw(graphics, intensity, elapsedSeconds, durationSeconds, STYLE);

// 持续效果：调用方自行管理强度及时间
LusterScreenFracture.draw(graphics, intensity, timeSeconds, STYLE);
```

限时版本通过 `elapsedSeconds` 和 `durationSeconds` 完全由调用方控制开始时间、持续时间与淡出；
持续版本可传入任意 `timeSeconds`。`setFragmentCount` 决定碎片密集程度：12～18 适合大块玻璃，
24～32 会得到更密集的裂纹，数值越高绘制量也越大。

强度及淡出同时控制画面旋转、位移和装饰层透明度。结束时画面恢复正常，碎片边界始终保持固定。
将 `refractionOffset` 和 `fragmentRotation` 都设置为 0 可仅绘制玻璃装饰层，并省去屏幕复制。
几何仅在 GUI 尺寸、seed 或碎片数量变化时生成，最多缓存八套。常规帧执行一次 GPU 屏幕复制、
一次纹理分片绘制和一次 GUI 装饰绘制，不逐碎片复制、不从 GPU 读回像素；屏幕副本复用，仅在窗口尺寸变化时重新分配。

调用方在效果播放结束或退出世界后的渲染回调中可调用以下方法释放屏幕副本及几何缓存：

```java
// 仅在客户端渲染线程调用，后续 draw 会按需自动重建资源。
LusterScreenFracture.releaseResources();
```

多个效果共享这套可重建缓存；如果另一个效果仍在使用，可等所有效果结束后再释放。
同一帧建议只调用一次。测试类 `LusterScreenFractureExample` 在进入世界后显示八秒；
它仅供开发测试，发布正式模组时可移除该类或其 `@EventBusSubscriber` 注解。
