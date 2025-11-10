package org.cubicdev.plugin.amazingchat.modules.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.cubicdev.plugin.amazingchat.managers.ConfigsManager;
import org.cubicdev.plugin.amazingchat.modules.Module;
import org.cubicdev.plugin.amazingchat.utils.ConfigUtils;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.List;

public class ChatClearModule extends Module {
    private String bypassPermission;
    private int blankLines;

    private String clearChatCommandPermission;
    private List<String> clearChatCommandAliases;

    private String chatClearedMessage;

    public ChatClearModule(ConfigsManager configsManager, String permission) {
        super(configsManager, "ChatClear", permission);
    }

    @Override
    public boolean checkPaths() {
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();
        ConfigurationSection section = yamlConfiguration.getConfigurationSection("modules.chat_clear");
        if(section == null) {
            Utils.sendLog(LogLevel.ERROR, "Error detected in clear chat module configuration: 'modules.chat_clear' path doesn't exists!");
            return false;
        }

        String[] requiredPaths = {
            "modules.chat_clear.enabled",
            "modules.chat_clear.permission",
            "modules.chat_clear.commands",
            "modules.chat_clear.language",
            "modules.chat_clear.blank_messages"
        };

        return ConfigUtils.checkPathsArray(requiredPaths, this.getName(), yamlConfiguration);
    }

    @Override
    public void setValues() {
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();
        permission = yamlConfiguration.getString("modules.chat_clear.permission",
                "amazingchat.module.chatclear");

        bypassPermission = yamlConfiguration.getString("modules.chat_clear.bypass_permission",
                "amazingchat.module.chatclear.bypass");

        blankLines = yamlConfiguration.getInt("modules.chat_clear.blank_messages", 200);

        String commandSectionPath = "modules.chat_clear.commands";
        ConfigurationSection commandSection = yamlConfiguration.getConfigurationSection(commandSectionPath);

        if(commandSection == null) {
            Utils.sendLog(LogLevel.ERROR, "Error detected in messages module configuration: '" + commandSectionPath + "' path doesn't exists! It is recommended to execute '/achat reload' to regenerate removed paths.");
        }

        clearChatCommandPermission = yamlConfiguration.getString(commandSectionPath + ".clearchat.permission", "amazingchat.module.chatclear.clearchat");
        clearChatCommandAliases = yamlConfiguration.getStringList(commandSectionPath + ".clearchat.aliases");

        String languageSectionPath = "modules.chat_clear.language";
        ConfigurationSection languageSection = yamlConfiguration.getConfigurationSection(languageSectionPath);

        if(languageSection == null){
            Utils.sendLog(LogLevel.ERROR, "Error detected in messages module configuration: '" + languageSectionPath + "' path doesn't exists! It is recommended to execute '/achat reload' to regenerate removed paths.");
        }

        chatClearedMessage = yamlConfiguration.getString(languageSectionPath + ".chat_cleared", "%prefix% <green>Successfully cleared the chat of all non-administrators.");
    }

    public String getBypassPermission() {
        return bypassPermission;
    }

    public int getBlankLines() {
        return blankLines;
    }

    public String getClearChatCommandPermission() {
        return clearChatCommandPermission;
    }

    public List<String> getClearChatCommandAliases() {
        return clearChatCommandAliases;
    }

    public String getChatClearedMessage() {
        return chatClearedMessage;
    }
}
