package org.skydom.chosen.server.skydomvelocityplugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.skydom.chosen.server.skydomvelocityplugin.modules.GlobalChat;
import org.slf4j.Logger;

import java.nio.file.Path;


@com.velocitypowered.api.plugin.Plugin(
        id = "skydomvelocityplugin",
        name = "SkydomVelocityPlugin",
        version = BuildConstants.VERSION, // 读取 build.gradle 中设置的版本号
        authors = {"Chosen_1st"}
)
public class SkydomVelocityPlugin {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public SkydomVelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;

        logger.info("加载插件中...");
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.register(new GlobalChat(this.server, this.logger, this.dataDirectory));
    }

    private void register(Object x) {
        this.server.getEventManager().register(this, x);
    }
}
