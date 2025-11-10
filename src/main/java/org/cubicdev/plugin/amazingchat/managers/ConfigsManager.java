/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.managers;

import org.cubicdev.plugin.amazingchat.config.Config;

import java.util.HashSet;

public class ConfigsManager {
    private HashSet<Config> configs;

    public ConfigsManager(){
        configs = new HashSet<>();
    }

    public void addConfig(Config config){
        configs.add(config);
    }

    public Config getConfigByName(String name){
        return configs.stream()
                .filter(config -> config.getFileName().equalsIgnoreCase(name))
                .findFirst().get();
    }

    public void reloadConfigs(){
        configs.forEach(Config::loadConfig);
    }

    public void saveConfigs(){
        configs.forEach(Config::saveConfig);
    }
}
