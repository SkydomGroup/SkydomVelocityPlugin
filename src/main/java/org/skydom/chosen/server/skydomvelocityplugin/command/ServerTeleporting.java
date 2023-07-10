package org.skydom.chosen.server.skydomvelocityplugin.command;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ServerTeleporting implements Command { // 定义一个类，实现Command接口
    private final ProxyServer server; // 定义一个私有的常量，类型是ProxyServer

    public ServerTeleporting(ProxyServer server) { // 定义一个公共的构造方法，参数是ProxyServer
        this.server = server; // 把参数赋值给常量
    }

    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource source = invocation.source(); // 获取执行指令的来源
        String[] args = invocation.arguments(); // 获取指令的参数
        if (!(source instanceof Player)) { // 如果来源不是玩家
            source.sendMessage(Component.text("这个指令只有玩家可以使用哦！")); // 发送一条消息给来源
            return; // 结束方法
        }
        if (args.length != 1) { // 如果参数的长度不等于1
            source.sendMessage(Component.text("使用方法： /stp <生存世界>或<资源世界>")); // 发送一条消息给来源，提示正确的用法
            return; // 结束方法
        }
        String name = args[0]; // 获取第一个参数，也就是服务器的名称
        Optional<RegisteredServer> targetServer = server.getServer(name); // 根据名称获取服务器对象，可能为空
        if (targetServer.isPresent()) { // 如果服务器对象不为空
            ((Player) source).createConnectionRequest(targetServer.get()).fireAndForget(); // 创建一个连接请求，让来源玩家连接到目标服务器
            source.sendMessage(Component.text("正在传送到" + name + "......")); // 发送一条消息给来源，告诉他正在传送
        } else {
        if (!name.equals("help")) { // 如果你想获取帮助
            source.sendMessage(Component.text("输入 '/stp 1' 传送到生存世界"));
            source.sendMessage(Component.text("输入 '/stp 2' 传送到资源世界"));
            return; // 结束方法
        }
        source.sendMessage(Component.text(name + "服务器没有找到。")); // 发送一条消息给来源，告诉他服务器不存在
        }
    }

    public List<String> suggest(SimpleCommand.Invocation invocation) {
        if (invocation.arguments().length == 0) { // 如果参数的长度等于0，也就是没有输入任何参数
            return Arrays.asList("1", "2"); // 返回一个列表，包含两个建议的服务器名称
        } else { // 如果参数的长度不等于0，也就是已经输入了一些参数
            return Collections.emptyList(); // 返回一个空列表，表示没有任何建议
        }
    }
}