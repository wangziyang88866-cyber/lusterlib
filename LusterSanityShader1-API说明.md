# Luster Sanity Shader 1 API 说明

`shader1` 是面向 Minecraft 1.21.1 NeoForge 客户端的全屏后处理效果。它会锐化画面、降低自然饱和度、
大幅提高对比度和曝光、压深阴影，并添加交叉处理、HDR 高光过度、噪点、
时域运动模糊、强烈的冷绿/青色整体色偏和轻微 RGB 色散，用来表现理智下降时的不适感。
当前版本不使用暗角、底部红光或局部红色增强。
效果本身不维护理智值，不订阅玩家事件；调用方完全控制每帧传入的强度。

## 基本调用

```java
import com.lusterlib.api.render.screen.shader1.LusterSanityShader;

@SubscribeEvent
public static void onRenderGui(RenderGuiEvent.Post event) {
    float timeSeconds = (System.nanoTime() - startNanos) / 1_000_000_000.0F;
    LusterSanityShader.draw(event.getGuiGraphics(), sanityEffectStrength, timeSeconds);
}
```

只在客户端 GUI/HUD 渲染回调中调用，并使用未经额外平移、旋转或缩放的全屏
`GuiGraphics.pose()`。推荐在 `RenderGuiEvent.Post` 中每帧调用一次，这样世界和 HUD 都会被着色。
若希望 HUD 保持清晰，可改在 `RenderGuiEvent.Pre` 中调用。不要在 Pre 和 Post 重复调用。

当暂停菜单或其他 `Screen` 存在时，请不要在 `RenderGuiEvent.Post` 绘制，改为在
`ScreenEvent.Render.Post` 调用。两个事件应互斥，避免同一帧重复后处理：

```java
// RenderGuiEvent.Post
if (Minecraft.getInstance().screen == null) {
    LusterSanityShader.draw(event.getGuiGraphics(), strength, timeSeconds);
}

// ScreenEvent.Render.Post
LusterSanityShader.draw(event.getGuiGraphics(), strength, timeSeconds);
```

`intensity` 会在内部限制到 0～2：

- `0` 不复制画面，不提交绘制。
- `0.2` 左右是轻微的不安感。
- `0.5`～`0.8` 适合持续的中等理智下降。
- `1` 是推荐的完整效果。
- `1`～`2` 是更强的失控效果，建议只短时使用。

`timeSeconds` 驱动色散幅度、运动模糊方向和噪点。传入持续递增的秒数即可正常播放；
传入固定值可暂停脉动，传入递减值则可倒放。

## API 参数与边界

- `LusterSanityShader.draw(graphics, intensity, timeSeconds)`：直接使用调用方给定的强度和时间。
- `intensity`：允许任意有限浮点数，内部限制到 `0～2`；负数等于 `0`，超过 `2` 等于 `2`。
- `timeSeconds`：可使用任意有限的秒数，不要传入 `NaN` 或无穷大。它不改变强度。
- `setTarget(value)`：设置绝对目标强度。
- `add(delta)`：对目标叠加有符号增量，正数增加、负数减少。
- `increase(amount)` / `decrease(amount)`：用非负值表示增减量的便捷方法。
- `setResponseTimes(riseSeconds, fallSeconds)`：自定义增强与减弱的速度；设为 `0` 时直接到达目标。
- `update(deltaSeconds)`：使用当帧真实时间更新平滑值，并返回可直接传给 `draw` 的强度。
- `snapTo(value)`：立即设置当前值与目标，适合初始化或重置。

## 随时间增强

API 不强制增长方式。例如在 10 秒内从无效果平滑增长到完整强度：

```java
float elapsedSeconds = (System.nanoTime() - startNanos) / 1_000_000_000.0F;
float strength = Math.min(elapsedSeconds / 10.0F, 1.0F);
// smoothstep，避免开始和结束时突变
strength = strength * strength * (3.0F - 2.0F * strength);
LusterSanityShader.draw(event.getGuiGraphics(), strength, elapsedSeconds);
```

## 平滑增强和减弱

`LusterSanityTransition` 可以在事件突然改变目标强度时避免画面跳变。控制器应由调用方持有：

```java
private static final LusterSanityTransition SANITY =
        new LusterSanityTransition(0.0F, 3.0F, 1.5F);

// 设置绝对目标：只改目标，不会立即跳变
SANITY.setTarget(1.0F); // 平滑增强
SANITY.setTarget(0.0F); // 平滑减弱

// 事件按增量修改目标；最终目标会自动限制在 0～2
SANITY.add(0.20F);      // 增加 0.20
SANITY.add(-0.10F);     // 减少 0.10
SANITY.increase(0.20F); // 等价的增加便捷方法
SANITY.decrease(0.10F); // 等价的减少便捷方法

// 每帧更新，deltaSeconds 应使用真实时间
float strength = SANITY.update(deltaSeconds);
LusterSanityShader.draw(graphics, strength, timeSeconds);
```

`riseSeconds` 和 `fallSeconds` 是独立的响应时间，经过该时间后完成约 95% 的变化。
`update` 使用帧率无关的指数阻尼，因此帧率波动或暂停界面不会造成突变。
`getValue()` 返回当前平滑后的强度，`getTarget()` 返回当前目标，
`snapTo(value)` 用于需要立即跳转或重置的情况。

## 由事件增减

调用方可以在自己的事件中更新强度，渲染回调只负责读取当前值：

```java
// 事件类型和增减量由调用模组决定。
SANITY.add(eventDelta);

// RenderGuiEvent.Post
float effectStrength = SANITY.update(deltaSeconds);
LusterSanityShader.draw(event.getGuiGraphics(), effectStrength, timeSeconds);
```

强度的网络同步、存档、定时增减和事件规则都由调用模组负责。这样多个模组或多种理智系统
不会被 LusterLib 的全局状态耦合。

## 资源释放

效果复用当前画面和历史画面两个与主帧缓冲同尺寸的纹理，窗口尺寸变化时会自动调整。
若长时间不再使用，
可在渲染线程释放：

```java
LusterSanityShader.releaseResources();
```

后续再次调用 `draw` 时会按需重建。稳定运行时每次有效调用执行一次 GPU 帧缓冲复制和一次全屏绘制；
当前帧与历史帧通过交换纹理复用，不再每帧执行第二次复制。
第一帧、窗口尺寸变化后或效果从完全透明重新开始时，会额外初始化一次历史画面。
片元着色从每像素 9 次纹理采样降为 6 次，动态三角函数改为 CPU 每帧计算，
不会从 GPU 读回像素。
强度低于 `1/1024` 时直接跳过整个后处理，避免在视觉上已经不可见时仍消耗 GPU。
