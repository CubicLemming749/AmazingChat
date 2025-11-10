/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.io.File;
import java.io.IOException;

public class Config {
    private AmazingChat main;
    private YamlConfiguration configuration;
    private final String fileName;

    public Config(String fileName, AmazingChat main) {
        this.fileName = fileName;
        this.main = main;

        loadConfig();
    }

    public void loadConfig(){
        File file = new File(main.getDataFolder(), fileName);
        configuration = YamlConfiguration.loadConfiguration(file);

        if(!file.exists()){
            main.saveResource(fileName, true);
        }

        Utils.sendLog(LogLevel.INFO, "Loading configuration file: "+fileName);
    }

    public void saveConfig(){
        File file = new File(main.getDataFolder(), fileName);
        try {
            configuration.save(file);
            Utils.sendLog(LogLevel.INFO, "Saving configuration file to disk: "+fileName);
        } catch (IOException e) {
            Utils.sendLog(LogLevel.WARNING, "A configuration file couldn't be saved! Name: "+fileName);
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public String getFileName() {
        return fileName;
    }
}
