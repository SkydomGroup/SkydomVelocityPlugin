package org.skydom.chosen.server.skydomvelocityplugin.chat;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.skydom.chosen.server.skydomvelocityplugin.Config;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GlobalChat {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private Config config;

    public GlobalChat(ProxyServer server, Logger logger, Path dataDirectory) {
        this.server = server; // 给属性server赋值，表示代理服务器
        this.logger = logger; // 给属性logger赋值，表示日志记录器
        this.dataDirectory = dataDirectory; // 给属性dataDirectory赋值，表示数据目录

        this.config = new Config(dataDirectory); // 调用Config类的构造方法，传入数据目录参数，创建一个配置文件的实例，并给属性config赋值
    }

    // 这个方法用于处理代理服务器重载的事件
    @Subscribe
    public void onProxyReload(ProxyReloadEvent event) {
        this.config = new Config(this.dataDirectory); // 调用Config类的构造方法，传入数据目录参数，创建一个新的配置文件的实例，并重新给属性config赋值
    }

    // 这个方法用于处理玩家聊天的事件
    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(PlayerChatEvent event) {
        if (!config.GLOBAL_CHAT_ENABLED) // 如果配置文件中没有启用全局聊天功能，就直接返回
            return;

        Optional<ServerConnection> currentServer = event.getPlayer().getCurrentServer(); // 获取玩家当前所在的服务器
        if (currentServer.isEmpty()) // 如果玩家没有连接到任何服务器，就直接返回
            return;

        String player = event.getPlayer().getUsername(); // 获取玩家的用户名
        String server = currentServer.get().getServerInfo().getName(); // 获取服务器的名称
        String message = event.getMessage(); // 获取玩家发送的消息

        Component msg = parseMessage(config.GLOBAL_CHAT_FORMAT, List.of( // 调用parseMessage方法，传入配置文件中的全局聊天消息格式和一个包含玩家，服务器和消息信息的模板列表，得到一个组件类型的消息
                new ChatTemplate("player", player, false), // 创建一个名为"player"，值为玩家用户名，不需要解析的模板
                new ChatTemplate("server", server, false), // 创建一个名为"server"，值为服务器名称，不需要解析的模板
                new ChatTemplate("message", message, config.GLOBAL_CHAT_ALLOW_MSG_FORMATTING) // 创建一个名为"message"，值为玩家消息，是否需要解析由配置文件中的设置决定的模板
        ));

        if (config.URLS_CLICKABLE) // 如果配置文件中启用了可点击的网址功能
            msg = msg.replaceText(config.urlReplacement); // 就调用消息的replaceText方法，传入配置文件中的网址替换规则参数，将消息中的网址替换为可点击的组件

        if (config.GLOBAL_CHAT_PASSTHROUGH) // 如果配置文件中启用了全局聊天通道
            sendMessage(msg,currentServer.get().getServer()); // 就调用sendMessage方法，传入消息和当前服务器，给除了当前服务器以外的所有服务器发送消息
        else //否则
            sendMessage(msg); // 就调用sendMessage方法，只传入消息，给所有的服务器发送消息

        if (config.GLOBAL_CHAT_TO_CONSOLE) // 如果配置文件中启用了将全局聊天消息输出到控制台功能
            this.logger.info("GLOBAL: <{}> {}", player, message); // 就调用日志记录器的info方法，传入格式化字符串和玩家用户名和消息参数，将全局聊天消息记录到控制台

        if (!config.GLOBAL_CHAT_PASSTHROUGH) // 如果配置文件中没有启用全局聊天通道
            event.setResult(ChatResult.denied()); // 就调用事件的setResult方法，传入拒绝结果参数，阻止玩家在当前服务器发送消息
    }

    // 这个方法用于处理玩家连接到代理服务器的事件
    @Subscribe
    public void onConnect(LoginEvent event) {
        if (!config.JOIN_ENABLE) // 如果配置文件中没有启用加入消息，就直接返回
            return;

        String player = event.getPlayer().getUsername(); // 获取玩家的用户名

        Component msg = parseMessage(config.JOIN_FORMAT, List.of( // 调用parseMessage方法，传入配置文件中的加入消息格式和一个包含玩家信息的模板列表，得到一个组件类型的消息
                new ChatTemplate("player", player, false) // 创建一个名为"player"，值为玩家用户名，不需要解析的模板
        ));

        sendMessage(msg); // 调用sendMessage方法，传入消息，给所有的服务器发送消息
    }


    // 这个方法用于处理玩家断开连接的事件
    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        // Quit
        if (!config.QUIT_ENABLE) // 如果配置文件中没有启用退出消息，就直接返回
            return;

        Optional<ServerConnection> currentServer = event.getPlayer().getCurrentServer(); // 获取玩家当前所在的服务器
        if (currentServer.isEmpty()) // 如果玩家没有连接到任何服务器，就直接返回
            return;

        String player = event.getPlayer().getUsername(); // 获取玩家的用户名
        String server = currentServer.get().getServerInfo().getName(); // 获取服务器的名称

        Component msg = parseMessage(config.QUIT_FORMAT, List.of( // 调用parseMessage方法，传入配置文件中的退出消息格式和一个包含玩家和服务器信息的模板列表，得到一个组件类型的消息
                new ChatTemplate("player", player, false), // 创建一个名为"player"，值为玩家用户名，不需要解析的模板
                new ChatTemplate("server", server, false) // 创建一个名为"server"，值为服务器名称，不需要解析的模板
        ));

        if (config.GLOBAL_CHAT_PASSTHROUGH) // 如果配置文件中启用了全局聊天通道
            sendMessage(msg,currentServer.get().getServer()); // 就调用sendMessage方法，传入消息和当前服务器，给除了当前服务器以外的所有服务器发送消息
        else // 否则
            sendMessage(msg); // 就调用sendMessage方法，只传入消息，给所有的服务器发送消息
    }

    // 这个方法用于解析消息，将其中的占位符替换为对应的值或组件
    private Component parseMessage(String input, List<ChatTemplate> templates) {
        // 使用MiniMessage处理信息
        List<TagResolver.Single> list = new ArrayList<>(); // 创建一个空的标签解析器列表

        for (ChatTemplate tmpl : templates) { // 遍历模板列表
            if (tmpl.parse) // 如果模板需要解析
                list.add(Placeholder.parsed(tmpl.name, tmpl.value)); // 就创建一个名为模板名称，值为模板值的占位符，并添加到标签解析器列表中
            else //否则
                list.add(Placeholder.parsed(tmpl.name, Component.text(tmpl.value).content())); // 就创建一个名为模板名称，值为模板值转换为文本组件后再取出内容的占位符，并添加到标签解析器列表中
        }

        return MiniMessage.miniMessage().deserialize(input, list.toArray(TagResolver[]::new)); // 使用MiniMessage类的miniMessage方法创建一个MiniMessage实例，然后调用它的deserialize方法，传入输入字符串和标签解析器列表转换为数组后的参数，得到一个组件类型的结果，并返回它
    }


    // 这个方法用于给所有的服务器发送一条消息
    private void sendMessage(Component msg) {
        for (RegisteredServer server : this.server.getAllServers()) // 遍历所有的服务器
            server.sendMessage(msg); // 调用服务器的sendMessage方法，传入消息参数
    }

    // 这个方法用于给除了当前服务器以外的所有服务器发送一条消息
    private void sendMessage(Component msg, RegisteredServer currentServer) {
        for (RegisteredServer server : this.server.getAllServers()) // 遍历所有的服务器
            if (server != currentServer) { // 判断是否是当前服务器
                server.sendMessage(msg); // 如果不是，就调用服务器的sendMessage方法，传入消息参数
            }
    }

    // 这个内部类用于表示一个聊天模板，包含三个属性：名称，值和是否解析
    static final class ChatTemplate {
        final String name; // 模板的名称，例如"welcome"
        final String value; // 模板的值，例如"Hello, {player}!"
        final Boolean parse; // 模板是否需要解析，例如将{player}替换为玩家的名字

        // 这个构造方法用于创建一个聊天模板的实例，传入三个参数：名称，值和是否解析
        public ChatTemplate(String name, String value, Boolean shouldParse) {
            this.name = name; // 给属性name赋值
            this.value = value; // 给属性value赋值
            this.parse = shouldParse; // 给属性parse赋值
        }
    }
}