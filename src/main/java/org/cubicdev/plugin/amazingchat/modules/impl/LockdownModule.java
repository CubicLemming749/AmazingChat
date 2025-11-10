/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.modules.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.cubicdev.plugin.amazingchat.managers.ConfigsManager;
import org.cubicdev.plugin.amazingchat.modules.Module;
import org.cubicdev.plugin.amazingchat.utils.ConfigUtils;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.List;

public class LockdownModule extends Module {
    private String lockdownBypassPermission;

    private String lockdownCommandPermission;
    private List<String> lockdownCommandAliases;

    private String chatLockedMessage;
    private String chatUnlockedMessage;
    private List<String> chatLockedGlobalMessage;
    private List<String> chatUnlockedGlobalMessage;
    private String cannotChatMessage;

    public LockdownModule(ConfigsManager configsManager, String permission) {
        super(configsManager, "Lockdown", permission);
    }

    @Override
    public boolean checkPaths() {
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();
        ConfigurationSection section = yamlConfiguration.getConfigurationSection("modules.chat_lockdown");

        if(section == null){
            Utils.sendLog(LogLevel.ERROR, "Error detected in lockdown module configuration: 'modules.chat_lockdown' path doesn't exists!");
            return false;
        }

        String[] requiredPaths = {
                "modules.chat_lockdown.enabled",
                "modules.chat_lockdown.permission",
                "modules.chat_lockdown.bypass_permission",
                "modules.chat_lockdown.commands",
                "modules.chat_lockdown.language"
        };

        return ConfigUtils.checkPathsArray(requiredPaths, this.getName(), yamlConfiguration);
    }

    @Override
    public void setValues() {
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();
        permission = yamlConfiguration.getString("modules.chat_lockdown.permission",
                "amazingchat.module.mention");

        lockdownBypassPermission = yamlConfiguration.getString("modules.chat_lockdown.bypass_permission",
                "amazingchat.module.chatlockdown.bypass");

        String commandSectionPath = "modules.chat_lockdown.commands";
        ConfigurationSection commandSection = yamlConfiguration.getConfigurationSection(commandSectionPath);

        if(commandSection == null){
            Utils.sendLog(LogLevel.ERROR, "Error detected in lockdown module configuration: '" + commandSectionPath + "' path doesn't exists! It is recommended to execute '/achat reload' to regenerate removed paths.");
        }

        lockdownCommandPermission = yamlConfiguration.getString(commandSectionPath + ".lockdown.permission", "amazingchat.module.chatlockdown.lockdown");
        lockdownCommandAliases = yamlConfiguration.getStringList(commandSectionPath + ".lockdown.aliases");

        String languageSectionPath = "modules.chat_lockdown.language";
        ConfigurationSection languageSection = yamlConfiguration.getConfigurationSection(languageSectionPath);

        if(languageSection == null){
            Utils.sendLog(LogLevel.ERROR, "Error detected in messages module configuration: '" + languageSectionPath + "' path doesn't exists! It is recommended to execute '/achat reload' to regenerate removed paths.");
        }

        chatLockedMessage = yamlConfiguration.getString(languageSectionPath + ".chat_locked", "%prefix% <green>Chat locked successfully.");
        chatUnlockedMessage = yamlConfiguration.getString(languageSectionPath + ".chat_unlocked", "%prefix% <green>Chat unlocked successfully.");
        chatLockedGlobalMessage = yamlConfiguration.getStringList(languageSectionPath + ".chat_locked_global");
        chatUnlockedGlobalMessage = yamlConfiguration.getStringList(languageSectionPath + ".chat_unlocked_global");
        cannotChatMessage = yamlConfiguration.getString(languageSectionPath + ".cannot_chat", "%prefix% <red>Sorry! The chat is currently locked and no one can speak. Ask an administrator.");
    }

    public String getLockdownBypassPermission() {
        return lockdownBypassPermission;
    }

    public String getLockdownCommandPermission() {
        return lockdownCommandPermission;
    }

    public List<String> getLockdownCommandAliases() {
        return lockdownCommandAliases;
    }

    public String getChatLockedMessage() {
        return chatLockedMessage;
    }

    public List<String> getChatLockedGlobalMessage() {
        return chatLockedGlobalMessage;
    }

    public String getCannotChatMessage() {
        return cannotChatMessage;
    }

    public String getChatUnlockedMessage() {
        return chatUnlockedMessage;
    }

    public List<String> getChatUnlockedGlobalMessage() {
        return chatUnlockedGlobalMessage;
    }
}
