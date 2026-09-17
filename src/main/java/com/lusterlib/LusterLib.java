package com.lusterlib;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// @Mod 是 NeoForge 的模组入口注解，指定 modid
@Mod(LusterLib.MODID)
public class LusterLib {

    // 模组 ID与 mods.toml 一致
    public static final String MODID = "lusterlib";

    // 模组日志记录器
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // NeoForge 会自动将 Mod 事件总线和 Mod 容器传进来
    public LusterLib(IEventBus modEventBus, ModContainer container) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            com.lusterlib.client.ClientModBusEvents.register(modEventBus);
        }
        // 监听通用初始化事件（可在此进行一些早期设置）
        modEventBus.addListener(this::commonSetup);

        // 如果你需要监听游戏启动、服务器启动等全局事件，
        // 请注册到 NeoForge.EVENT_BUS（游戏事件总线）
        NeoForge.EVENT_BUS.register(this);
    }

    // FMLCommonSetupEvent 在模组加载时触发，适合做一些不依赖渲染的初始化
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("{} common setup completed.", MODID);
        // 例如：注册网络消息、配置加载等
    }

    // 示例：监听服务器启动事件（可删除，仅作展示）
    @net.neoforged.bus.api.SubscribeEvent
    public void onServerStarting(final net.neoforged.neoforge.event.server.ServerStartingEvent event) {
        LOGGER.info("{} is ready on server side.", MODID);
    }
}
