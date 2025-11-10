/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.config.Config;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Utils {
    private Config config;
    private Utils instance;

    private static Gson gson;
    private static YamlConfiguration yamlConfiguration;

    public Utils(Config config){
        instance = this;
        this.config = config;

        yamlConfiguration = instance.config.getConfiguration();
        gson = new Gson();
    }

    public static void sendMessage(CommandSender sender, String message){
        String prefix = yamlConfiguration.getString("prefix", "<gray>[<yellow>AmazingChat<gray>]");
        message = message.replace("%prefix%", prefix);

        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component parsedMessage = (sender instanceof Player) ?
                miniMessage.deserialize(PlaceholderAPI.setPlaceholders((Player) sender, message)) :
                miniMessage.deserialize(message);

        sender.sendMessage(parsedMessage);
    }

    public static void sendDebug(String msg){
        sendLog(LogLevel.WARNING, msg);
    }

    public static void sendLog(LogLevel level, String msg){
        Logger logger = AmazingChat.LOGGER;

        switch(level){
            case LogLevel.INFO -> logger.info(msg);
            case LogLevel.WARNING -> logger.warning(msg);
            case LogLevel.ERROR -> logger.severe(msg);
        }
    }

    public static String getJsonFromSet(Set<?> set){
        return gson.toJson(set);
    }

    public static <T> Set<T> getSetFromJson(String json, Class<T> classType){
        if(json == null || json.isEmpty()){
            return HashSet.newHashSet(0);
        }

        Type type = TypeToken.getParameterized(Set.class, classType).getType();
        return gson.fromJson(json, type);
    }
}
