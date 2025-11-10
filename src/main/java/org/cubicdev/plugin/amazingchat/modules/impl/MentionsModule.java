/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.modules.impl;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.cubicdev.plugin.amazingchat.managers.ConfigsManager;
import org.cubicdev.plugin.amazingchat.modules.Module;
import org.cubicdev.plugin.amazingchat.utils.ConfigUtils;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.List;

public class MentionsModule extends Module {

    private String mentionSoundName;
    private float mentionSoundVolume;
    private float mentionSoundPitch;

    private String mentionPrefix;
    private String mentionActionBar;

    private String toggleMentionsCommandPermission;
    private List<String> toggleMentionsCommandAliases;

    private String mentionsOnMessage;
    private String mentionsOffMessage;

    public MentionsModule(ConfigsManager configsManager, String permission) {
        super(configsManager, "Mentions", permission);
    }

    @Override
    public boolean checkPaths() {
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();
        ConfigurationSection section = yamlConfiguration.getConfigurationSection("modules.mentions");

        if(section == null){
            Utils.sendLog(LogLevel.ERROR, "Error detected in mentions module configuration: 'modules.mentions' path doesn't exists! It is recommended to execute '/achat reload' to regenerate removed paths.");
            return false;
        }

        String[] requiredPaths = {
                "modules.mentions.enabled",
                "modules.mentions.permission",
                "modules.mentions.sound",
                "modules.mentions.commands",
                "modules.mentions.prefix"
        };

        return ConfigUtils.checkPathsArray(requiredPaths, this.getName(), yamlConfiguration);
    }

    @Override
    public void setValues() {
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();
        permission = yamlConfiguration.getString("modules.mentions.permission",
                "amazingchat.module.mention");

        mentionPrefix = yamlConfiguration.getString("modules.mentions.prefix"
                , "@");

        mentionActionBar = yamlConfiguration.getString("modules.mentions.actionbar_message"
                , "<gold>You have been mentioned by <yellow>%sender% <gold>in the chat!");

        String[] mentionSoundParams = (yamlConfiguration.getString("modules.mentions.sound") == null) ?
               new String[]{""} : yamlConfiguration.getString("modules.mentions.sound").split(";");

        if(mentionSoundParams.length != 3){
            mentionSoundName = "ENTITY_ITEM_PICKUP";
            mentionSoundVolume = 1.0F;
            mentionSoundPitch = 1.0F;
            Utils.sendLog(LogLevel.ERROR, "Error detected in mentions module configuration: Incomplete sound configuration. Syntax expected: <SOUND>;<VOLUME>;<PITCH>");
            return;
        }

        mentionSoundName = mentionSoundParams[0];

        try {
            Sound.valueOf(mentionSoundName);
        } catch (IllegalArgumentException e) {
            Utils.sendLog(LogLevel.ERROR, "Error detected in mentions module configuration: Invalid sound: "+mentionSoundName);
            mentionSoundName = "ENTITY_ITEM_PICKUP";
        }

        try {
            mentionSoundVolume = Float.parseFloat(mentionSoundParams[1]);
            mentionSoundPitch = Float.parseFloat(mentionSoundParams[2]);
        } catch (NumberFormatException e) {
            Utils.sendLog(LogLevel.ERROR, "Error detected in mentions module configuration: Sound volume and pitch need to be numbers.");
            mentionSoundVolume = 1.0F;
            mentionSoundPitch = 1.0F;
        }

        toggleMentionsCommandAliases = yamlConfiguration.getStringList("modules.mentions.commands.togglementions.aliases");
        toggleMentionsCommandPermission = yamlConfiguration.getString("modules.mentions.commands.togglementions.permission", "amazingchat.module.mention.togglementions");

        mentionsOnMessage = yamlConfiguration.getString("modules.mentions.language.mentions_on",
                "%prefix% <white>You turned <green><b>ON <white>the mentions!");
        mentionsOffMessage = yamlConfiguration.getString("modules.mentions.language.mentions_off",
                "%prefix% <white>You turned <red><b>OFF <white>the mentions!");
    }

    public String getMentionSoundName() {
        return mentionSoundName;
    }

    public float getMentionSoundVolume() {
        return mentionSoundVolume;
    }

    public float getMentionSoundPitch() {
        return mentionSoundPitch;
    }

    public String getMentionPrefix() {
        return mentionPrefix;
    }

    public String getMentionActionBar() {
        return mentionActionBar;
    }

    public String getToggleMentionsCommandPermission() {
        return toggleMentionsCommandPermission;
    }

    public List<String> getToggleMentionsCommandAliases() {
        return toggleMentionsCommandAliases;
    }

    public String getMentionsOnMessage() {
        return mentionsOnMessage;
    }

    public String getMentionsOffMessage() {
        return mentionsOffMessage;
    }
}
