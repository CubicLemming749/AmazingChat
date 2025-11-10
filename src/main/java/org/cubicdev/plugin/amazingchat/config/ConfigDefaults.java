/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigDefaults {
    private AmazingChat main;

    public ConfigDefaults(AmazingChat main){
        this.main = main;
    }

    public void setMainConfigDefaults(Config config){
        InputStream inputStream = main.getResource("config.yml");

        if(inputStream == null){
            Utils.sendLog(LogLevel.ERROR, "Could not load defaults for "+config.getFileName());
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        YamlConfiguration yamlConfiguration = config.getConfiguration();
        yamlConfiguration.addDefaults(YamlConfiguration.loadConfiguration(reader));
        yamlConfiguration.options().copyDefaults(true);

        config.saveConfig();
    }
}
