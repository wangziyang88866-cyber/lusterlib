# LusterTextEffect API 使用说明

## 概述

`com.lusterlib.api.text.animation1` 是 animation1 的公开调用层。它只负责计算每个字符的位移、缩放和旋转；不负责获取时间，也不负责渲染文字。

当前提供的 `retching()` 是“反胃/蠕动”效果。调用方创建效果实例、设置参数，并在每帧逐字符调用 `transform(...)`。

## 导入

```java
import com.lusterlib.api.text.animation1.LusterTextEffect;
import com.lusterlib.api.text.animation1.LusterTextEffects;
import com.lusterlib.api.text.animation1.LusterTextTransform;
```

## 创建并配置效果

```java
private static final LusterTextEffect EFFECT = LusterTextEffects.retching()
        .setIntensity(1.35F)
        .setSpeed(1.0F)
        .setFrequency(0.65F)
        .setSeed(20260907);
```

应保存并复用实例，不要在每一帧重新创建效果实例。

| 方法 | 默认值 | 说明 |
|---|---:|---|
| `setIntensity(float)` | `1.0F` | 整体扭曲程度。`0.0F` 为无扭曲；建议使用 `0.0F` 至 `2.0F`。 |
| `setSpeed(float)` | `1.0F` | 动画运行速度。 |
| `setFrequency(float)` | `0.45F` | 局部褶皱的紧凑程度；数值越大，起伏越紧密。 |
| `setSeed(int)` | `0` | 细微扰动的固定种子；相同种子会得到稳定的效果细节。 |

`setIntensity` 是全局强度控制：它会同时影响位移、缩放和旋转，而不是只放大某一种变换。

## 计算单个字符的变换

```java
LusterTextTransform transform = EFFECT.transform(
        characterIndex,
        text.length(),
        timeSeconds
);
```

| 参数 | 说明 |
|---|---|
| `characterIndex` | 当前字符的索引，从 `0` 开始。 |
| `text.length()` | 整段文字的字符数。 |
| `timeSeconds` | 连续时间，单位为秒，由调用方提供。 |

返回的 `LusterTextTransform` 提供：

```java
transform.getOffsetX();   // X 位移
transform.getOffsetY();   // Y 位移
transform.getScaleX();    // X 缩放，1.0F 为原大小
transform.getScaleY();    // Y 缩放，1.0F 为原大小
transform.getRotation();  // 旋转角度，单位为度
```

## 最小调用示例

```java
private static final LusterTextEffect EFFECT = LusterTextEffects.retching()
        .setIntensity(1.5F);

public void render(String text, float timeSeconds) {
    for (int index = 0; index < text.length(); index++) {
        LusterTextTransform transform = EFFECT.transform(
                index, text.length(), timeSeconds
        );

        // 将 transform 的位移、缩放与旋转应用到当前字符的绘制矩阵。
        drawCharacter(text.charAt(index), transform);
    }
}
```

## 注意事项

- `LusterTextEffect` 与 `LusterTextTransform` 不依赖 Minecraft 或 NeoForge；渲染适配由调用方处理。
- `transform(...)` 会按“每帧 × 每字符”调用。保存效果实例即可，变换结果会随时间自动更新。
- 当前 `setIntensity(float)` 会将负数视为 `0.0F`，以避免反向且难以预期的扭曲。
- animation2 是另一套内部效果实现，当前尚未提供公开 API；不要混用 animation1 和 animation2 的 `LusterTextTransform` 类型。
