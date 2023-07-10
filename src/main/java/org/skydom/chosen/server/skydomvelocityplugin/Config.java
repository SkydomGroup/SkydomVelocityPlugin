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

    @Inject
    public Config(@DataDirectory Path dataDir) {
        this.dataDir = dataDir;

        loadFile();
        loadConfigs();

        this.urlReplacement = TextReplacementConfig.builder()
                .match(Pattern.compile(this.URLS_PATTERN))
                .replacement(text -> text.clickEvent(ClickEvent.openUrl(text.content())))
                .build();
    }

    private void loadFile() {
        File dataDirectoryFile = this.dataDir.toFile();
        if (!dataDirectoryFile.exists())
            dataDirectoryFile.mkdir(); // 保证无误

        File dataFile = new File(dataDirectoryFile, "config.toml");

        // 新建配置文件
        if (!dataFile.exists()) {
            try {
                InputStream in = this.getClass().getResourceAsStream("/config.toml");
                Files.copy(in, dataFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException("ERROR: Can't write default configuration file (permissions/filesystem error?)");
            }
        }

        this.toml = new Toml().read(dataFile);

        // 读取配置文件版本，判断是否与插件兼容
        long version = this.toml.getLong("config_version", 0L);
        if (version != CONFIG_VERSION) {
            throw new RuntimeException("ERROR: Can't use the existing configuration file: version mismatch (intended for another, older version?)");
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
    }
}
