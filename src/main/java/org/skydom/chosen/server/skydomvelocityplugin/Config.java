package org.skydom.chosen.server.skydomvelocityplugin;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class Config {
    static final long CONFIG_VERSION = 1;

    Path dataDir;
    Toml toml;

    public boolean GLOBAL_CHAT_ENABLED;
    public boolean GLOBAL_CHAT_TO_CONSOLE;
    public boolean GLOBAL_CHAT_PASSTHROUGH;
    public boolean GLOBAL_CHAT_ALLOW_MSG_FORMATTING;
    public String GLOBAL_CHAT_FORMAT;

    public boolean URLS_CLICKABLE;
    public String URLS_PATTERN;
    public TextReplacementConfig urlReplacement;

    public boolean JOIN_ENABLE;
    public String JOIN_FORMAT;

    public boolean QUIT_ENABLE;
    public String QUIT_FORMAT;
    public boolean GLOBAL_PING;
    public boolean STP;

    @Inject // 使用@Inject注解，表示这个构造方法是依赖注入的
    public Config(@DataDirectory Path dataDir) { // 定义一个公共的构造方法，参数是数据目录的路径
        this.dataDir = dataDir; // 把参数赋值给成员变量

        loadFile(); // 调用loadFile方法，加载配置文件
        loadConfigs(); // 调用loadConfigs方法，加载配置文件中的各项设置

        this.urlReplacement = TextReplacementConfig.builder() // 创建一个文本替换配置的构建器，并赋值给成员变量
                .match(Pattern.compile(this.URLS_PATTERN)) // 设置匹配的正则表达式为判断网址的正则表达式
                .replacement(text -> text.clickEvent(ClickEvent.openUrl(text.content()))) // 设置替换的函数为给文本添加点击事件，打开文本内容对应的网址
                .build(); // 构建出文本替换配置对象
    }

    private void loadFile() { // 定义一个私有的方法，用来加载配置文件
        File dataDirectoryFile = this.dataDir.toFile(); // 把数据目录转换为文件对象
        if (!dataDirectoryFile.exists()) // 如果文件不存在
            dataDirectoryFile.mkdir(); // 创建一个目录

        File dataFile = new File(dataDirectoryFile, "config.toml"); // 创建一个文件对象，表示配置文件

        if (!dataFile.exists()) { // 如果文件不存在
            try {
                InputStream in = this.getClass().getResourceAsStream("/config.toml"); // 获取类路径下的默认配置文件的输入流
                Files.copy(in, dataFile.toPath()); // 把输入流的内容复制到文件路径
            } catch (IOException e) { // 捕获可能的输入输出异常
                throw new RuntimeException("ERROR: Can't write default configuration file (permissions/filesystem error?)"); //抛出运行时异常，表示无法写入默认配置文件
            }
        }

        this.toml = new Toml().read(dataFile); // 创建一个Toml对象，用来读取配置文件的内容，并赋值给成员变量

        long version = this.toml.getLong("config_version", 0L); // 从Toml对象中获取配置文件版本的长整数值，如果没有则默认为0
        if (version != CONFIG_VERSION) { // 如果版本不等于常量CONFIG_VERSION，表示不兼容
            throw new RuntimeException("ERROR: Can't use the existing configuration file: version mismatch (intended for another, older version?)"); //抛出运行时异常，表示无法使用现有的配置文件，因为版本不匹配
        }
    }


    public void loadConfigs() {
        // 读取配置文件
        this.GLOBAL_CHAT_ENABLED = this.toml.getBoolean("chat.enable", true);
        this.GLOBAL_CHAT_TO_CONSOLE = this.toml.getBoolean("chat.log_to_console", true);
        this.GLOBAL_CHAT_PASSTHROUGH = this.toml.getBoolean("chat.passthrough", true);
        this.GLOBAL_CHAT_ALLOW_MSG_FORMATTING = this.toml.getBoolean("chat.parse_player_messages", true);
        this.GLOBAL_CHAT_FORMAT = this.toml.getString("chat.format", "<<player>> <message>");

        this.URLS_CLICKABLE = this.toml.getBoolean("urls.clickable", true);
        this.URLS_PATTERN = this.toml.getString("urls.pattern", "https?:\\/\\/\\S+");

        this.JOIN_ENABLE = this.toml.getBoolean("join.enable", false);
        this.JOIN_FORMAT = this.toml.getString("join.format", "<yellow><player> 加入了游戏</yellow>");

        this.QUIT_ENABLE = this.toml.getBoolean("quit.enable", true);
        this.QUIT_FORMAT = this.toml.getString("quit.format", "<yellow><player> 离开了游戏</yellow>");

        this.GLOBAL_PING = this.toml.getBoolean("ping.enable", true);

        this.STP = this.toml.getBoolean("stp.enable", true);
    }
}
