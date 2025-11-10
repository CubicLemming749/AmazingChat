/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.utils;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigUtils {
    public static boolean pathExists(YamlConfiguration configuration, String path){
        return configuration.get(path) != null;
    }

    public static boolean checkPathsArray(String[] paths, String moduleName, YamlConfiguration yamlConfiguration){
        for(String path : paths){
            if(!ConfigUtils.pathExists(yamlConfiguration, path)){
                Utils.sendLog(LogLevel.ERROR, "Error detected in" + moduleName + "module configuration: '" + path + "' path doesn't exists! It is recommended to execute '/achat reload' to regenerate removed paths.");
                return false;
            }
        }

        return true;
    }
}
