package com.lusterlib.client;

import com.lusterlib.LusterLib;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

public final class ClientModBusEvents {

    private ClientModBusEvents() {
    }

    /** 由模组入口注册到 Mod 事件总线，避免使用已弃用的 EventBusSubscriber.Bus。 */
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientModBusEvents::registerGuiLayers);
        modEventBus.addListener(ClientModBusEvents::registerLayerDefinitions);
        modEventBus.addListener(ClientModBusEvents::registerRenderers);
        modEventBus.addListener(ClientModBusEvents::addEntityRendererLayers);
        modEventBus.addListener(ClientModBusEvents::registerItemColors);
        modEventBus.addListener(ClientModBusEvents::registerBlockColors);
        modEventBus.addListener(ClientModBusEvents::registerShaders);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        // 注册 GUI Layer
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(
            EntityRenderersEvent.RegisterLayerDefinitions event
    ) {
        // 注册 LayerDefinition
    }

    @SubscribeEvent
    public static void registerRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {
        // 注册 Renderer
    }

    @SubscribeEvent
    public static void addEntityRendererLayers(
            EntityRenderersEvent.AddLayers event
    ) {
        // 添加 RenderLayer
    }

    @SubscribeEvent
    public static void registerItemColors(
            RegisterColorHandlersEvent.Item event
    ) {
        // Item 颜色
    }

    @SubscribeEvent
    public static void registerBlockColors(
            RegisterColorHandlersEvent.Block event
    ) {
        // Block 颜色
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        com.lusterlib.client.render.screen.shader1.LusterSanityShader.register(event);
        com.lusterlib.client.render.screen.shader2.LusterBlackEdgeShader.register(event);
    }
}
