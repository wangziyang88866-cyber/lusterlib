# Luster Black Edge Shader 2 API 说明

`shader2` 是 Minecraft 1.21.1 NeoForge 客户端的全屏后处理效果。它先读取已经绘制完成的画面，
再把原有色彩覆盖成黑色，并从原画面的亮度变化中提取白色轮廓。轮廓附近同时使用多尺度采样生成
由黑色、灰色到白色的向内聚拢泛光。因为处理的是完整帧缓冲，所以方块、实体、粒子和天空都能
进入效果；HUD 或 Screen 是否进入效果由调用时机决定。

正式 API 位于：

```java
com.lusterlib.api.render.screen.shader2.LusterBlackEdgeShader
com.lusterlib.api.render.screen.shader2.LusterBlackEdgeStyle
com.lusterlib.api.render.screen.shader2.LusterBlackEdgeTransition
```

## 最小调用

```java
import com.lusterlib.api.render.screen.shader2.LusterBlackEdgeShader;

@SubscribeEvent(priority = EventPriority.LOWEST)
public static void onRenderGuiPost(RenderGuiEvent.Post event) {
    if (Minecraft.getInstance().screen == null) {
        LusterBlackEdgeShader.draw(event.getGuiGraphics(), effectStrength);
    }
}
```

`intensity` 的有效范围为 `0～1`：

- `0` 不复制画面，也不提交 GPU 绘制。
- `0～1` 会用 smoothstep 在原画面和目标效果之间连续过渡。
- `1` 完全移除原色，只留下黑底、灰白泛光和白色轮廓。
- 负数按 `0` 处理，大于 `1` 按 `1` 处理；`NaN` 和无穷值会被忽略。

应该只在客户端渲染事件中调用，并使用事件原始的、没有额外变换的 `GuiGraphics`。
在 `RenderGuiEvent.Post` 调用会处理世界与 HUD；在 `RenderGuiEvent.Pre` 调用，后绘制的 HUD
则不受影响。不要在同一帧的 Pre 和 Post 中重复调用。

## 自定义内部与边缘颜色

不传样式时默认使用黑色内部（`0x000000`）和白色边缘（`0xFFFFFF`）。需要自定义时，
创建并复用一个 `LusterBlackEdgeStyle`：

```java
private static final LusterBlackEdgeStyle STYLE = new LusterBlackEdgeStyle()
        .setInteriorColor(0x080018)
        .setEdgeColor(0x80F8FF);

LusterBlackEdgeShader.draw(graphics, strength, STYLE);
```

颜色格式为 `0xRRGGBB`；若传入 ARGB，最高 8 位会被忽略。泛光会自动从内部颜色过渡到边缘颜色。
样式可以在运行时修改，但应和 `draw` 一样只在客户端线程操作。向三参数 `draw` 传入 `null`
等价于使用默认黑白颜色。

## 自定义持续时间

`LusterBlackEdgeTransition` 由调用模组自行持有。下面的序列用 2 秒淡入，保持 8 秒，
再用 3 秒淡出，总持续时间为 13 秒：

```java
private static final LusterBlackEdgeTransition BLACK_EDGE =
        new LusterBlackEdgeTransition();

// 在一次触发事件中只调用一次，不要在每个 tick 中反复 play
BLACK_EDGE.play(2.0F, 8.0F, 3.0F);

// 在每帧渲染处推进并绘制
float strength = BLACK_EDGE.update(deltaSeconds);
LusterBlackEdgeShader.draw(event.getGuiGraphics(), strength);
```

三个时间参数都以秒为单位，负值按 `0` 处理。过渡使用 smoothstep，能在指定时间准确到达目标，
且 `update` 可以在一帧内跨越多个阶段，不会因为低帧率把总时长不断拉长。调用方应提供真实的
帧间隔，并在每个显示帧只调用一次 `update`。如果两个互斥渲染事件共用一个渲染函数，便能自然
满足这一点。从长时间失去焦点的状态恢复时，可将过大的 `deltaSeconds` 限制到合理值。

## 单独淡入、淡出或过渡

```java
BLACK_EDGE.fadeIn(5.0F);              // 从当前强度用 5 秒变到 1
BLACK_EDGE.fadeOut(1.5F);             // 从当前强度用 1.5 秒变到 0
BLACK_EDGE.transitionTo(0.65F, 2.0F); // 用 2 秒变到指定强度
BLACK_EDGE.snapTo(1.0F);              // 立即变为 1，并取消当前序列
```

新的 `fadeIn`、`fadeOut`、`transitionTo`、`add` 或 `snapTo` 会取代尚未完成的 `play` 序列，
因此事件可以随时改变当前走向。`getValue()` 返回当前可见强度，`getTarget()` 返回当前目标；
`isActive()` 表示是否仍有过渡、保持或排队的淡出阶段。

## 由事件决定增加或减少

API 不限定事件类型、网络同步或存档方式。事件只需修改由调用方持有的控制器：

```java
// 立即增减
BLACK_EDGE.add(0.20F);
BLACK_EDGE.add(-0.10F);

// 在给定时间内增减当前目标
BLACK_EDGE.add(0.25F, 0.8F);
BLACK_EDGE.add(-0.40F, 1.2F);
```

所有结果都会限制到 `0～1`。如果事件系统已经维护了自己的强度或时间线，也可以完全不使用
`LusterBlackEdgeTransition`，直接把事件系统给出的浮点强度传给 `draw`。

## 暂停菜单、Screen 与 HUD

`RenderGuiEvent.Post` 发生在 Screen 完成绘制之前，因此仅监听它无法覆盖暂停菜单、物品栏等界面。
完整用法是让 `RenderGuiEvent.Post` 和 `ScreenEvent.Render.Post` 互斥地调用同一个方法：

```java
private static long previousFrameNanos = System.nanoTime();

// RenderGuiEvent.Post
if (Minecraft.getInstance().screen == null) {
    renderBlackEdge(event.getGuiGraphics());
}

// ScreenEvent.Render.Post
if (Minecraft.getInstance().player != null) {
    renderBlackEdge(event.getGuiGraphics());
}

private static void renderBlackEdge(GuiGraphics graphics) {
    long now = System.nanoTime();
    float deltaSeconds = Math.min(
            (now - previousFrameNanos) / 1_000_000_000.0F,
            0.25F
    );
    previousFrameNanos = now;

    float strength = BLACK_EDGE.update(deltaSeconds);
    if (strength > 0.0F) {
        LusterBlackEdgeShader.draw(graphics, strength, STYLE);
    }
}
```

正常游戏帧只有第一个分支执行，打开 Screen 后只有第二个分支执行，因此不会重复推进动画或重复
处理同一帧。暂停只会停止世界 tick，不会停止该基于真实帧间隔的过渡动画。若只需要世界、方块
和实体而不需要任何 Screen，可只保留第一个回调。

## 资源释放

渲染器会复用一个与主帧缓冲同尺寸的 GPU 纹理，并在窗口尺寸变化时自动调整。长时间不再使用时，
可在渲染线程释放它：

```java
LusterBlackEdgeShader.releaseResources();
```

后续再次调用 `draw` 会自动重建。不要从服务端线程调用 `draw` 或 `releaseResources`。

## 性能与光影兼容性

该效果只读取光影或原版已经输出到主帧缓冲的最终颜色，不读取深度纹理、阴影图或任何光影模组的
G-buffer，因此不会要求 Iris/NeOculus 等模组暴露内部纹理。它在光影开启时会把光影的最终画面
一起变为轮廓效果；光影产生的阴影、泛光和 SSAO 也会作为画面细节参与轮廓检测。

每次有效 `draw` 都会复制一次完整颜色帧缓冲，并进行一次全屏绘制。边缘检测使用三组四点中心
梯度加一次原图读取，约为 **13 次纹理读取/像素**；这是为了在不改变光影后处理链的前提下保留
三层黑→灰→边缘色泛光。`intensity` 严格为 `0` 时不执行复制或全屏绘制，因此效果完全结束后应
停止调用，或让 `LusterBlackEdgeTransition` 返回 `0`。

在 1080p 与普通光影下通常可接受；在 2K/4K、重型光影或集成显卡上，应避免同时叠加多个全屏
后处理效果。当前 API 故意不采用深度边缘检测或直接读写光影内部缓冲，以优先保证兼容性。

## 开发测试

`com.lusterlib.client.render.screen.example.LusterBlackEdgeShaderExample` 会在开发客户端进入世界后
循环演示 4 秒淡入、6 秒保持和 4 秒淡出，并在暂停菜单与其他 Screen 上继续显示和推进效果。
它只依赖正式 API；正式代码不依赖该测试类。
Gradle 的 `jar` 任务已排除 `com/lusterlib/client/render/screen/example/**`，因此测试类不会进入发布包。
