package org.skydom.chosen.server.skydomvelocityplugin.chat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

public class SendMessageOnPlayerJoin {
    private final ProxyServer server; // 定义一个私有的常量，类型是ProxyServer
    private final Logger logger; // 定义一个私有的常量，类型是Logger

    public SendMessageOnPlayerJoin(ProxyServer server, Logger logger) { // 定义一个公共的构造方法，参数是ProxyServer和Logger
        this.server = server; // 把参数赋值给常量
        this.logger = logger; // 把参数赋值给常量
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) { // 定义一个公共的方法，使用@Subscribe注解，表示这个方法是一个事件监听器，参数是PostLoginEvent
        Player player = event.getPlayer(); // 调用event对象的getPlayer方法，获取玩家对象，赋值给player变量
        String name = player.getUsername(); // 调用player对象的getUsername方法，获取玩家的名字，赋值给name变量
        String message = "你好！" + name; // 拼接玩家名字和"Message"字符串，赋值给message变量
        boolean isOnline = player.isActive(); // 调用player对象的isActive方法，获取玩家是否在线的状态，赋值给isOnline变量
        while (isOnline) { // 使用while语句，当isOnline变量为真时，执行循环体
            String server = player.getCurrentServer().get().getServerInfo().getName(); // 调用player对象的getCurrentServer方法，获取玩家所在的服务器对象，然后调用get方法，再调用getServerInfo方法，再调用getName方法，获取服务器的名字，赋值给server变量
            if (server.equals("1") || server.equals("2")) { // 使用if语句，判断server变量是否等于"服务器1"或者"服务器2"
                player.sendMessage(Component.text(message)); // 调用player对象的sendMessage方法，给玩家发送message变量的内容
                player.sendMessage(Component.text("欢迎游玩 Minecraft Skydom Server")); // 调用player对象的sendMessage方法，给玩家发送字符串
                player.sendMessage(Component.text("你可以通过以下指令传送到生存世界或者资源世界")); // 调用player对象的sendMessage方法，给玩家发送字符串
                player.sendMessage(Component.text("'/stp 1' 这个指令可以将你传送到生存世界")); // 调用player对象的sendMessage方法，给玩家发送字符串
                player.sendMessage(Component.text("'/stp 2' 这个指令可以将你传送到资源世界")); // 调用player对象的sendMessage方法，给玩家发送字符串
                logger.info("给玩家" + name + "发送了欢迎消息"); // 使用logger对象的info方法，打印一条信息到控制台
                break; // 使用break语句，跳出循环
            }
            isOnline = player.isActive(); // 再次调用player对象的isActive方法，更新isOnline变量的值
        }
    }
}