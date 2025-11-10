/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.formats;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cubicdev.plugin.amazingchat.managers.ConfigsManager;
import org.cubicdev.plugin.amazingchat.model.Format;
import org.cubicdev.plugin.amazingchat.model.FormatElement;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ChatFormatSerializer {
    private ConfigsManager configsManager;
    private YamlConfiguration yamlConfiguration;

    public ChatFormatSerializer(ConfigsManager configsManager){
        this.configsManager = configsManager;
    }

    /**
     * Retrieves all the chat formats declared in configuration files.
     * @return The formats list
     */
    public List<Format> deserializeFormats(){
        this.yamlConfiguration = configsManager.getConfigByName("formats.yml").getConfiguration();
        List<Format> temporal = new ArrayList<>();

        ConfigurationSection section = yamlConfiguration.getConfigurationSection("formats");
        if(section == null){
            Utils.sendLog(LogLevel.ERROR, "The formats path doesn't exist in formats.yml!");
            return Collections.emptyList();
        }

        Set<String> formatNames = section.getKeys(false);

        for(String formatName : formatNames){
            ConfigurationSection formatSection = section.getConfigurationSection(formatName);

            String formatPermission = formatSection.getString("permission");
            List<FormatElement> formatElements = new ArrayList<>();
            String formatMessageSeparator = formatSection.getString("message_separator");
            String formatMessage = formatSection.getString("message");

            ConfigurationSection elementsSection = formatSection.getConfigurationSection("elements");

            if (elementsSection != null) {
                formatElements = deserializeFormatSections(elementsSection, formatName);
            }

            Format format = new Format(formatName, formatPermission, formatElements, formatMessageSeparator, formatMessage);
            temporal.add(format);
            Utils.sendDebug("Successfully deserialized a format " + formatName);
        }

        return temporal;
    }

    /**
     *
     * @param section ConfigurationSection in which the elements declaration is present, inside a format
     * @param parentFormatName The name of format that "owns" these elements.
     * @return The list of elements.
     */
    private List<FormatElement> deserializeFormatSections(ConfigurationSection section, String parentFormatName){
        List<FormatElement> temporal = new ArrayList<>();
        Set<String> formatElementsNames = section.getKeys(false);

        for(String formatElementName : formatElementsNames){
            ConfigurationSection formatElementSection = section.getConfigurationSection(formatElementName);

            String text = formatElementSection.getString("text");

            if(text == null){
                Utils.sendLog(LogLevel.ERROR, "Error detected in format " + parentFormatName + "! " + formatElementName + "text value doesn't exists. Skipping...");
                continue;
            }

            List<String> hover = formatElementSection.getStringList("hover");
            String clickAction = formatElementSection.getString("click_action");

            FormatElement formatElement = new FormatElement(text, hover, clickAction);

            temporal.add(formatElement);
            Utils.sendDebug("Successfully deserialized a format element named " + formatElementName + " for the format " + parentFormatName);
        }

        return temporal;
    }
}
