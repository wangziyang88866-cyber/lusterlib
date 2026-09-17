# LusterTextEffect animation2 API 使用说明

## 概述

`com.lusterlib.api.text.animation2` 提供“整体连续波形”文字效果。它使用一个共享时间相位驱动整段文字：字符只是在同一条连续曲线上取样，因此位移、缩放和旋转保持连贯，而不是各自随机变化。

API 只计算变换数据；时间来源和实际渲染由调用方负责。

## 导入

```java
import com.lusterlib.api.text.animation2.LusterTextEffect;
import com.lusterlib.api.text.animation2.LusterTextEffects;
import com.lusterlib.api.text.animation2.LusterTextTransform;
```

## 创建并配置效果

```java
private static final LusterTextEffect EFFECT = LusterTextEffects.wave()
        .setIntensity(1.25F)
        .setSpeed(1.15F)
        .setFrequency(0.85F);
```

请保存并复用该效果实例，不要每一帧重新创建。

| 方法 | 默认值 | 说明 |
|---|---:|---|
| `setIntensity(float)` | `1.0F` | 整体变形强度；`0.0F` 为无效果，建议为 `0.0F` 至 `2.0F`。 |
| `setSpeed(float)` | `1.0F` | 波形动画速度。 |
| `setFrequency(float)` | `1.0F` | 整段文字内的完整起伏数量。`1.0F` 为一条完整波形。 |
| `setMoveX(float)` / `setMoveY(float)` | `0.75F` / `2.25F` | 横向 / 纵向位移幅度。 |
| `setScaleX(float)` / `setScaleY(float)` | `0.08F` / `0.14F` | 横向 / 纵向缩放幅度。 |
| `setRotation(float)` | `5.0F` | 文字沿波形倾斜时的最大旋转角度。 |

## 获取单个字符的变换

```java
LusterTextTransform transform = EFFECT.transform(
        characterIndex,
        text.length(),
        timeSeconds
);
```

`transform` 的含义：

```java
transform.getOffsetX();   // X 位移
transform.getOffsetY();   // Y 位移
transform.getScaleX();    // X 缩放，1.0F 为原大小
transform.getScaleY();    // Y 缩放，1.0F 为原大小
transform.getRotation();  // 旋转角度，单位为度
```

## 最小示例

```java
private static final LusterTextEffect EFFECT = LusterTextEffects.wave()
        .setIntensity(1.5F)
        .setFrequency(1.0F);

public void render(String text, float timeSeconds) {
    for (int index = 0; index < text.length(); index++) {
        LusterTextTransform transform = EFFECT.transform(
                index, text.length(), timeSeconds
        );

        // 将变换应用到当前字符的渲染矩阵。
        drawCharacter(text.charAt(index), transform);
    }
}
```

## 与 animation1 的区别

- animation1：局部鼓包、收缩和连续扰动，更像不规则蠕动。
- animation2：共享且规则的波形，更像整段文字是一条柔软的带子。

两个包各自定义了 `LusterTextEffect` 和 `LusterTextTransform`；请不要在二者之间混用类型。
