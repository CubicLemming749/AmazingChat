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

public class MessagesModule extends Module {
    private String messageSoundName;
    private float messageSoundVolume;
    private float messageSoundPitch;

    private String msgCommandPermission;
    private List<String> msgCommandAliases;

    private String replyCommandPermission;
    private List<String> replyCommandAliases;

    private String ignoreCommandPermission;
    private List<String> ignoreCommandAliases;

    private String unignoreCommandPermission;
    private List<String> unignoreCommandAliases;

    private String toggleMsgCommandPermission;
    private List<String> toggleMsgCommandAliases;

    private String showIgnoredCommandPermission;
    private List<String> showIgnoredCommandAliases;

    private String formatSenderMessage;
    private String formatReceiverMessage;
    private String alreadyIgnoredMessage;
    private String notIgnoredMessage;
    private String successfullyIgnoredMessage;
    private String successfullyUnignoredMessage;
    private String toggleMsgYesMessage;
    private String toggleMsgNoMessage;
    private String playerIsNotRecevingMsgMessage;
    private String youreIgnoredMessage;
    private String errorMessage;
    private String playerOfflineMessage;
    private String cannotReplyMessage;
    private String cannotMessageYourself;
    private String playerDoesntExistMessage;
    private String noIgnoredOnesMessage;
    private String ignoredListHeader;
    private String ignoredListFormat;

    public MessagesModule(ConfigsManager configsManager, String permission) {
        super(configsManager, "Messages", permission);
    }

    @Override
    public boolean checkPaths() {
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();
        ConfigurationSection section = yamlConfiguration.getConfigurationSection("modules.messages");

        if (section == null) {
            Utils.sendLog(LogLevel.ERROR, "Error detected in messages module configuration: 'modules.messages' path doesn't exists! It is recommended to execute '/achat reload' to regenerate removed paths.");
            return false;
        }

        String[] requiredPaths = {
                "modules.messages.enabled",
                "modules.messages.permission",
                "modules.messages.sound",
                "modules.messages.commands",
                "modules.messages.language"
        };

        return ConfigUtils.checkPathsArray(requiredPaths, this.getName(), yamlConfiguration);
    }

    @Override
    public void setValues() {
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();
        permission = yamlConfiguration.getString("modules.messages.permission",
                "amazingchat.module.messages");

        String[] messageSoundParams = yamlConfiguration.getString("modules.messages.sound", "BLOCK_NOTE_BLOCK_PLING;1;1").split(";");

        if(messageSoundParams.length != 3){
            Utils.sendLog(LogLevel.ERROR, "Error detected in messages module: Invalid sound configuration. Expected: <SOUND>;<VOLUME>;<PITCH>. Using fallback default values.");
            messageSoundParams = "BLOCK_NOTE_BLOCK_PLING;1;1".split(";");
        }

        messageSoundName = messageSoundParams[0];

        try {
            //This is not stored in any object, we only want to validate the name.
            Sound.valueOf(messageSoundName);

            messageSoundVolume = Float.parseFloat(messageSoundParams[1]);
            messageSoundPitch = Float.parseFloat(messageSoundParams[2]);
        } catch (NumberFormatException e) {
            Utils.sendLog(LogLevel.ERROR, "Error detected in messages module configuration: Sound volume and pitch need to be numbers. Setting default values...");
            messageSoundVolume = 1.0f;
            messageSoundPitch = 1.0f;
        } catch (IllegalArgumentException e) {
            Utils.sendLog(LogLevel.ERROR, "Error detected in messages module configuration: Invalid sound: " + messageSoundName + ". Setting default values...");
            messageSoundName = "BLOCK_NOTE_BLOCK_PLING";
        }

        String commandSectionPath = "modules.messages.commands";

        //This object only exists for checking.
        ConfigurationSection commandSection = yamlConfiguration.getConfigurationSection(commandSectionPath);

        if (commandSection == null) {
            Utils.sendLog(LogLevel.ERROR, "Error detected in messages module configuration: '" + commandSectionPath + "' path doesn't exists! It is recommended to execute '/achat reload' to regenerate removed paths.");
        }

        msgCommandPermission = yamlConfiguration.getString(commandSectionPath + ".msg.permission", "amazingchat.module.messages.msg");
        msgCommandAliases = yamlConfiguration.getStringList(commandSectionPath + ".msg.aliases");

        replyCommandPermission = yamlConfiguration.getString(commandSectionPath + ".reply.permission", "amazingchat.module.messages.reply");
        replyCommandAliases = yamlConfiguration.getStringList(commandSectionPath + ".reply.aliases");

        ignoreCommandPermission = yamlConfiguration.getString(commandSectionPath + ".ignore.permission", "amazingchat.module.messages.ignore");
        ignoreCommandAliases = yamlConfiguration.getStringList(commandSectionPath + ".ignore.aliases");

        unignoreCommandPermission = yamlConfiguration.getString(commandSectionPath + ".unignore.permission", "amazingchat.module.messages.unignore");
        unignoreCommandAliases = yamlConfiguration.getStringList(commandSectionPath + ".unignore.aliases");

        toggleMsgCommandPermission = yamlConfiguration.getString(commandSectionPath + ".togglemsg.permission", "amazingchat.module.messages.togglemsg");
        toggleMsgCommandAliases = yamlConfiguration.getStringList(commandSectionPath + ".togglemsg.aliases");

        showIgnoredCommandPermission = yamlConfiguration.getString(commandSectionPath + ".showignored.permission", "amazingchat.module.messages.showignored");
        showIgnoredCommandAliases = yamlConfiguration.getStringList(commandSectionPath + ".showignored.aliases");

        String languageSectionPath = "modules.messages.language";
        ConfigurationSection languageSection = yamlConfiguration.getConfigurationSection(languageSectionPath);

        if (languageSection == null) {
            Utils.sendLog(LogLevel.ERROR, "Error detected in messages module configuration: '" + languageSectionPath + "' path doesn't exists! It is recommended to execute '/achat reload' to regenerate removed paths.");
        }

        formatSenderMessage = yamlConfiguration.getString(languageSectionPath + ".format_sender",
                "<gray>[PM]</gray> <green>%player% <dark_gray>-></dark_gray> %target%<dark_gray>: <white>%message%");

        formatReceiverMessage = yamlConfiguration.getString(languageSectionPath + ".format_receiver",
                "<gray>[PM]</gray> <green>%sender% <dark_gray>-></dark_gray> %player%<dark_gray>: <white>%message%");

        alreadyIgnoredMessage = yamlConfiguration.getString(languageSectionPath + ".already_ignored",
                "%prefix% <green>%player% was already ignored.");

        notIgnoredMessage = yamlConfiguration.getString(languageSectionPath + ".not_ignored",
                "%prefix% <green>%player% <gold>was never already ignored.");

        successfullyIgnoredMessage = yamlConfiguration.getString(languageSectionPath + ".successfully_ignored",
                "%prefix% <gold>You're now ignoring <green>%player%</green>.");

        successfullyUnignoredMessage = yamlConfiguration.getString(languageSectionPath + ".successfully_unignored",
                "%prefix% <gold>You're now not ignoring <green>%player%</green>.");

        toggleMsgYesMessage = yamlConfiguration.getString(languageSectionPath + ".togglemsg_yes",
                "%prefix% <gold>You enabled private messages.");

        toggleMsgNoMessage = yamlConfiguration.getString(languageSectionPath + ".togglemsg_no",
                "%prefix% <gold>You disabled private messages..");

        playerIsNotRecevingMsgMessage = yamlConfiguration.getString(languageSectionPath + ".player_not_receiving_messages",
                "%prefix% <red>The player <gold>%player%</gold> is not receiving messages right now.");

        youreIgnoredMessage = yamlConfiguration.getString(languageSectionPath + ".youre_ignored",
                "%prefix% <red>Oops! The player <gold>%player%</gold> is ignoring you.");

        errorMessage = yamlConfiguration.getString(languageSectionPath + ".error_while_sending",
                "%prefix% <red>There was an error while sending your message, contact an administrator.");

        playerOfflineMessage = yamlConfiguration.getString(languageSectionPath + ".player_offline",
                "%prefix% <red>Couldn't send message to %player%, they're offline!");

        cannotReplyMessage = yamlConfiguration.getString(languageSectionPath + ".cannot_reply",
                "%prefix% <red>You haven't chatted with someone.");

        cannotMessageYourself = yamlConfiguration.getString(languageSectionPath + ".cannot_message_or_ignore_yourself",
                "%prefix% <red>You can't message or ignore yourself.");

        playerDoesntExistMessage = yamlConfiguration.getString(languageSectionPath + ".player_doesnt_exist",
                "%prefix% <red>That player doesn't exist in this server!");

        noIgnoredOnesMessage = yamlConfiguration.getString(languageSectionPath + ".you_havent_ignored"
                , "%prefix% <red>You haven't ignored anyone!");

        ignoredListHeader = yamlConfiguration.getString(languageSectionPath + "ignored_list_header", "%prefix% <yellow>You have ignored:");

        ignoredListFormat = yamlConfiguration.getString(languageSectionPath+".ignored_list_format",
                "<gray>•</gray> <color:#d9ff4f><click:suggest_command:'/unignore %player%'><hover:show_text:'<color:#2bff39>Want to unignore them?</color> <color:#9cff33>Click here.</color>'>%player%</hover></click></color>");
    }


    public String getMessageSoundName() {
        return messageSoundName;
    }

    public float getMessageSoundVolume() {
        return messageSoundVolume;
    }

    public float getMessageSoundPitch() {
        return messageSoundPitch;
    }

    public String getMsgCommandPermission() {
        return msgCommandPermission;
    }

    public List<String> getMsgCommandAliases() {
        return msgCommandAliases;
    }

    public String getReplyCommandPermission() {
        return replyCommandPermission;
    }

    public List<String> getReplyCommandAliases() {
        return replyCommandAliases;
    }

    public String getIgnoreCommandPermission() {
        return ignoreCommandPermission;
    }

    public List<String> getIgnoreCommandAliases() {
        return ignoreCommandAliases;
    }

    public String getUnignoreCommandPermission() {
        return unignoreCommandPermission;
    }

    public List<String> getUnignoreCommandAliases() {
        return unignoreCommandAliases;
    }

    public String getToggleMsgCommandPermission() {
        return toggleMsgCommandPermission;
    }

    public List<String> getToggleMsgCommandAliases() {
        return toggleMsgCommandAliases;
    }

    public String getFormatSenderMessage() {
        return formatSenderMessage;
    }

    public String getFormatReceiverMessage() {
        return formatReceiverMessage;
    }

    public String getAlreadyIgnoredMessage() {
        return alreadyIgnoredMessage;
    }

    public String getNotIgnoredMessage() {
        return notIgnoredMessage;
    }

    public String getSuccessfullyIgnoredMessage() {
        return successfullyIgnoredMessage;
    }

    public String getSuccessfullyUnignoredMessage() {
        return successfullyUnignoredMessage;
    }

    public String getToggleMsgYesMessage() {
        return toggleMsgYesMessage;
    }

    public String getToggleMsgNoMessage() {
        return toggleMsgNoMessage;
    }


    public String getPlayerIsNotRecevingMsgMessage() {
        return playerIsNotRecevingMsgMessage;
    }

    public String getYoureIgnoredMessage() {
        return youreIgnoredMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getPlayerOfflineMessage() {
        return playerOfflineMessage;
    }

    public String getCannotReplyMessage() {
        return cannotReplyMessage;
    }

    public String getCannotMessageYourself() {
        return cannotMessageYourself;
    }

    public String getPlayerDoesntExistMessage() {
        return playerDoesntExistMessage;
    }

    public String getShowIgnoredCommandPermission() {
        return showIgnoredCommandPermission;
    }

    public List<String> getShowIgnoredCommandAliases() {
        return showIgnoredCommandAliases;
    }

    public String getNoIgnoredOnesMessage() {
        return noIgnoredOnesMessage;
    }

    public String getIgnoredListFormat() {
        return ignoredListFormat;
    }

    public String getIgnoredListHeader() {
        return ignoredListHeader;
    }
}
