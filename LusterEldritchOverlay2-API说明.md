# Luster Eldritch Overlay 2 API 说明

`overlay2` 是从屏幕四周向中心蔓延的全屏呼吸式光影。它直接绘制多层渐变边缘，适合
克苏鲁、精神污染或诡谲场景氛围；不依赖帧缓冲副本或自定义着色器。效果仅限客户端渲染阶段调用。

```java
import com.lusterlib.api.render.screen.overlay2.LusterEldritchOverlay;
```

```java
float timeSeconds = (System.nanoTime() - startNanos) / 1_000_000_000.0F;

LusterEldritchOverlay.draw(
        event.getGuiGraphics(),
        0xA0420C68, // ARGB 颜色
        0.9F,       // 程度：0 为不可见，1 为完整强度
        timeSeconds // 连续时间（秒）
);
```

调用方完全控制颜色、程度和时间；可传入静止时间以冻结侵蚀画面、负时间以反向流动。
建议在 `RenderGuiEvent.Post` 调用以显示在 HUD 上方。每次调用只绘制六层渐变边缘；
应避免同一帧重复叠加多个实例。
