# LusterText Halo 1 API 说明

`halo1` 为 GUI 文字提供轻量、可配置的光晕效果。它仅适用于客户端渲染回调，
例如 `RenderGuiEvent.Post` 或 Screen 的渲染事件；不要在服务端、注册事件或游戏逻辑线程调用。

## 引入 API

```java
import com.lusterlib.api.text.halo1.LusterTextHaloRenderer;
import com.lusterlib.api.text.halo1.LusterTextHaloStyle;
```

## 绘制一段带光晕的文字

```java
private static final LusterTextHaloStyle HALO = new LusterTextHaloStyle()
        .setRadius(5.0F)          // 光晕尺寸，GUI 像素
        .setBlur(3.0F)            // 边缘模糊程度；0 为清晰描边
        .setColor(0xCC66CCFF);    // ARGB：透明度、红、绿、蓝

LusterTextHaloRenderer.drawHaloText(
        graphics, font, "Hello", x, y,
        0xFFFFFFFF, false, HALO
);
```

`drawHaloText` 会先绘制光晕再绘制正文，适合绝大多数情况。若正文由其他逻辑绘制，
则先调用 `drawHalo(...)`，再调用自己的 `graphics.drawString(...)`。

## 只给部分文字添加光晕

该 API 不会全局修改 Minecraft 的所有文字。哪一段传给 API，哪一段才有光晕：

```java
float currentX = x;
LusterTextHaloRenderer.drawHaloText(graphics, font, "重要", currentX, y, 0xFFFFCC55, false, HALO);
currentX += font.width("重要");
graphics.drawString(font, "：这一部分没有光晕", currentX, y, 0xFFFFFFFF, false);
```

单个字符、子串和整段文字都可以分别调用。对于逐字动画，应在每个字符对应的
`pose.pushPose()` 与 `pose.popPose()` 之间调用；光晕会沿用当前 `PoseStack` 的平移、缩放和旋转。

## 性能

实现不会创建帧缓冲或纹理。每个模糊层固定进行八方向采样，且最多八层；因此较大的
`blur` 不会无限增加绘制次数。建议常规 HUD 使用 `radius = 2~6`、`blur = 1~4`，并复用
`LusterTextHaloStyle` 实例，不要每帧新建配置对象。
