package org.skydom.chosen.server.skydomvelocityplugin.ping;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import java.util.ArrayList;
import java.util.List;

public class GlobalPing { // 定义一个类，用来显示全局的玩家列表
    private final ProxyServer server; // 定义一个私有的常量，类型是ProxyServer

    public GlobalPing(ProxyServer server) { // 定义一个公共的构造方法，参数是ProxyServer
        this.server = server; // 把参数赋值给常量
    }

    @Subscribe // 使用@Subscribe注解，表示这个方法是一个事件监听器
    public void onPing(ProxyPingEvent event) { // 定义一个公共的方法，参数是ProxyPingEvent，这个事件会在服务器被ping时触发
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