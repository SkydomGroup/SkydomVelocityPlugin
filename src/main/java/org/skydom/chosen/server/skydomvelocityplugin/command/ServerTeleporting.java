package org.skydom.chosen.server.skydomvelocityplugin.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.skydom.chosen.server.skydomvelocityplugin.Config;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ServerTeleporting implements SimpleCommand { // 定义一个类，实现Command接口
    private final ProxyServer server;
    private final Path dataDirectory;
    private Config config;

    public ServerTeleporting(ProxyServer server, Path dataDirectory) { // 定义一个公共的构造方法，参数是ProxyServer
        this.server = server; // 把参数赋值给常量
        this.dataDirectory = dataDirectory; // 给属性dataDirectory赋值，表示数据目录
        this.config = new Config(dataDirectory); // 调用Config类的构造方法，传入数据目录参数，创建一个配置文件的实例，并给属性config赋值
    }

    // 这个方法用于处理代理服务器重载的事件
    @Subscribe
    public void onProxyReload(ProxyReloadEvent event) {
        this.config = new Config(this.dataDirectory); // 调用Config类的构造方法，传入数据目录参数，创建一个新的配置文件的实例，并重新给属性config赋值
    }

    public void execute(SimpleCommand.Invocation invocation) {
        if (!config.STP) { // 判断配置文件
            return; // 结束方法
        }
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
            Player player = (Player) source; // 把来源转换为玩家对象
            if (player.getCurrentServer().get().getServer().equals(targetServer.get())) { // 如果玩家已经在目标服务器上
                source.sendMessage(Component.text("你已经在这个世界了。")); // 发送一条消息给来源，告诉他不需要传送
                return; // 结束方法
            }
            player.createConnectionRequest(targetServer.get()).fireAndForget(); // 创建一个连接请求，让来源玩家连接到目标服务器
            if (name.equals("1")){
                source.sendMessage(Component.text("正在传送到生存世界......")); // 发送一条消息给来源，告诉他正在传送
            }else if (name.equals("2")){
                source.sendMessage(Component.text("正在传送到资源世界......")); // 发送一条消息给来源，告诉他正在传送
            }
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

    public String[] getAliases() { // 实现SimpleCommand接口的getAliases方法，返回一个字符串数组，表示指令的别名
        return new String[]{"serverteleporting"};
    }
}
