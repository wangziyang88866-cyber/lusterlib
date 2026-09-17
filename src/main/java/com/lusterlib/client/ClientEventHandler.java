package com.lusterlib.client;

import com.lusterlib.LusterLib;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(
        modid = LusterLib.MODID,
        value = Dist.CLIENT
)
public final class ClientEventHandler {

    private ClientEventHandler() {
    }

    // Client Tick

    @SubscribeEvent
    public static void onClientTickPre(ClientTickEvent.Pre event) {
        // 更新动画状态
    }

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        // 更新客户端状态
    }


    // GUI

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        // GUI 渲染前
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        // GUI 渲染后
    }


    // Screen

    @SubscribeEvent
    public static void onScreenRenderPre(ScreenEvent.Render.Pre event) {
        // Screen 渲染前
    }

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        // Screen 渲染后
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        // Screen 初始化
    }

    @SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing event) {
        // Screen 关闭
    }

    @SubscribeEvent
    public static void onMouseDraggedPre(ScreenEvent.MouseDragged.Pre event) {
        // Screen 拖拽前
    }

    @SubscribeEvent
    public static void onMouseDraggedPost(ScreenEvent.MouseDragged.Post event) {
        // Screen 拖拽后
    }

    @SubscribeEvent
    public static void onMouseButtonPressed(
            ScreenEvent.MouseButtonPressed.Pre event
    ) {
        // Screen 鼠标按下前
    }

    @SubscribeEvent
    public static void onMouseButtonReleased(
            ScreenEvent.MouseButtonReleased.Pre event
    ) {
        // Screen 鼠标释放前
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        // 全局鼠标输入、Vanilla 处理前
    }


    // Input

    @SubscribeEvent
    public static void onInteractionKeyMappingTriggered(
            InputEvent.InteractionKeyMappingTriggered event
    ) {
        // 攻击、使用物品等
    }


    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        // 全局键盘输入
    }


    // Tooltip

    @SubscribeEvent
    public static void onTooltipPre(RenderTooltipEvent.Pre event) {
        // Tooltip 渲染前
    }

    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        // Tooltip 颜色
    }

    @SubscribeEvent
    public static void onTooltipGatherComponents(
            RenderTooltipEvent.GatherComponents event
    ) {
        // Tooltip 内容
    }


    // World Rendering

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        // 世界空间渲染
    }


    // Player Interaction

    @SubscribeEvent
    public static void onRightClickBlock(
            PlayerInteractEvent.RightClickBlock event
    ) {
        if (!event.getLevel().isClientSide()) {
            return;
        }

        // 客户端交互
    }
}