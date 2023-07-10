package org.skydom.chosen.server.skydomvelocityplugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.skydom.chosen.server.skydomvelocityplugin.chat.GlobalChat;
import org.skydom.chosen.server.skydomvelocityplugin.command.ServerTeleporting;
import org.skydom.chosen.server.skydomvelocityplugin.ping.GlobalPing;
import org.slf4j.Logger;

import java.nio.file.Path;


@com.velocitypowered.api.plugin.Plugin( // 使用@Plugin注解，表示这个类是一个Velocity插件
        id = "skydomvelocityplugin", // 设置插件的ID
        name = "SkydomVelocityPlugin", // 设置插件的名称
        version = BuildConstants.VERSION, // 设置插件的版本号，使用BuildConstants类的VERSION常量，它会读取build.gradle中设置的版本号
        authors = {"Chosen_1st"} // 设置插件的作者
)
public class SkydomVelocityPlugin {
    public final ProxyServer server; // 定义一个公共的常量，类型是ProxyServer
    private final Logger logger; // 定义一个私有的常量，类型是Logger
    private final Path dataDirectory; // 定义一个私有的常量，类型是Path
    private Config config; // 导入配置文件设置

    @Inject // 使用@Inject注解，表示这个构造方法需要依赖注入
    public SkydomVelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) { // 定义一个公共的构造方法，参数是ProxyServer, Logger和Path
        logger.info("加载插件中......"); // 使用logger对象的info方法，打印一条信息到控制台
        this.server = server; // 把参数赋值给常量
        this.logger = logger; // 把参数赋值给常量
        this.dataDirectory = dataDirectory; // 把参数赋值给常量
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.register(new GlobalChat(this.server, this.logger, this.dataDirectory)); // 启用消息同步
        this.register(new GlobalPing(this.server,this.dataDirectory)); // 启用Ping同步
        server.getCommandManager().register("stp", new ServerTeleporting(this.server,this.dataDirectory)); // 启用跨服传送
    }

    private void register(Object x) { // 定义一个私有的方法，参数是Object
        this.server.getEventManager().register(this, x); // 调用server对象的getEventManager方法，获取事件管理器，然后调用register方法，把当前类和参数注册为事件监听器
    }
}