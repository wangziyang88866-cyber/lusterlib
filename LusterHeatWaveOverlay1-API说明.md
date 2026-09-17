# Luster Heat Wave Overlay 1 API 说明

`overlay1` 是一个客户端全屏流动光影效果。它在 GUI 渲染阶段直接叠加少量连续渐变光带，
形成平滑移动的热浪式明暗起伏；不依赖帧缓冲副本、贴图或自定义着色器。

## 引入 API

```java
import com.lusterlib.api.render.screen.overlay1.LusterHeatWaveOverlay;
```

该 API 只能在客户端使用。请在 GUI 或 HUD 渲染事件（如 `RenderGuiEvent.Post`）中调用，
不要在服务端、注册阶段或普通游戏逻辑线程调用。

## 基本调用

```java
float timeSeconds = (System.nanoTime() - startNanos) / 1_000_000_000.0F;

LusterHeatWaveOverlay.draw(
        event.getGuiGraphics(),
        0x78FF9A45, // ARGB：透明度、红、绿、蓝
        0.8F,       // 程度：0 为不可见，1 为完整强度
        timeSeconds // 连续时间，单位为秒
);
```

调用会覆盖当前 GUI 区域。建议在 `RenderGuiEvent.Post` 调用以显示在 HUD 上层；
若希望光影位于 HUD 下方，则改在 `RenderGuiEvent.Pre` 调用。

## 自由控制时间

效果不保存状态，时间完全由调用方管理：

```java
// 两倍流速
LusterHeatWaveOverlay.draw(graphics, 0x80A8D8FF, 0.55F, gameSeconds * 2.0F);

// 冻结在当前画面
LusterHeatWaveOverlay.draw(graphics, 0x80FFAA66, 0.7F, frozenTime);

// 反向流动
LusterHeatWaveOverlay.draw(graphics, 0x80FFAA66, 0.7F, -gameSeconds);
```

## 性能与组合

每次调用只绘制九条渐变光带，没有帧缓冲复制与自定义着色器加载成本。避免在同一帧重复调用
多次；若需叠加多个色彩效果，应优先在调用方合成颜色和强度后只调用一次。
