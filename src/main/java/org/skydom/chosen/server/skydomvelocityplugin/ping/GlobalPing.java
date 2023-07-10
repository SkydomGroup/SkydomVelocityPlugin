package org.skydom.chosen.server.skydomvelocityplugin.ping;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.skydom.chosen.server.skydomvelocityplugin.Config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GlobalPing { // 定义一个类，用来显示全局的玩家列表
    private final ProxyServer server; // 定义一个私有的常量，类型是ProxyServer
    private final Path dataDirectory;
    private Config config; // 导入配置文件设置

    public GlobalPing(ProxyServer server,Path dataDirectory) { // 定义一个公共的构造方法，参数是ProxyServer
        this.server = server; // 把参数赋值给常量
        this.dataDirectory = dataDirectory; // 给属性dataDirectory赋值，表示数据目录
        this.config = new Config(dataDirectory); // 调用Config类的构造方法，传入数据目录参数，创建一个配置文件的实例，并给属性config赋值
    }

    @Subscribe
    public void onProxyReload(ProxyReloadEvent event) {
        this.config = new Config(this.dataDirectory); // 调用Config类的构造方法，传入数据目录参数，创建一个新的配置文件的实例，并重新给属性config赋值
    }

    @Subscribe // 使用@Subscribe注解，表示这个方法是一个事件监听器
    public void onPing(ProxyPingEvent event) { // 定义一个公共的方法，参数是ProxyPingEvent，这个事件会在服务器被ping时触发
        if (!config.GLOBAL_PING) { // 判断配置文件
            return; // 结束方法
        }
        List<ServerPing.SamplePlayer> samplePlayers = new ArrayList<>(); // 创建一个列表，用来存储所有的玩家信息
        for (RegisteredServer s : server.getAllServers()) { // 遍历所有的子服务器
            for (Player p : s.getPlayersConnected()) { // 遍历每个子服务器上的玩家
                samplePlayers.add(new ServerPing.SamplePlayer(p.getUsername(), p.getUniqueId())); // 创建一个玩家信息对象，包含玩家的用户名和UUID，然后添加到列表中
            }
        }
        ServerPing ping = event.getPing(); // 获取事件中的服务器信息对象
        ServerPing newPing = ping.asBuilder().samplePlayers(samplePlayers.toArray(ServerPing.SamplePlayer[]::new)).build(); // 创建一个新的服务器信息对象，使用原来的对象的构建器，然后设置玩家列表为刚刚创建的列表，最后构建出新的对象
        event.setPing(newPing); // 把事件中的服务器信息对象替换为新的对象
    }
}