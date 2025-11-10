/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.service;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.managers.FormatManager;
import org.cubicdev.plugin.amazingchat.model.Format;
import org.cubicdev.plugin.amazingchat.model.FormatElement;
import org.cubicdev.plugin.amazingchat.utils.SerializerUtils;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.*;

public class FormatsService {
    private FormatManager formatManager;

    public FormatsService(FormatManager formatManager){
        this.formatManager = formatManager;
    }

    public Optional<Format> getPlayerFormat(Player player){
        return formatManager.getFormats().stream()
                .filter(format -> player.hasPermission(format.getPermission()) ||
                        format.getPermission().equalsIgnoreCase("none"))
                .findFirst();
    }

    public Component processFormat(Player player, String originalMsg, Format format) {
        String message = format.getMessage().replace("%message%", originalMsg);
        List<FormatElement> elements = format.getElements();

        List<Component> temporalComponentList = new ArrayList<>();

        for (FormatElement element : elements) {
            Component textComponent = SerializerUtils.getLegacyToMinimessage(player, element.getText(), true);

            List<Component> hoverComponents = element.getHoverText().stream()
                            .map(line -> SerializerUtils.getLegacyToMinimessage(player, line, true))
                            .toList();

            Component hoverComponent = Component.join(JoinConfiguration.newlines(), hoverComponents);
            textComponent = textComponent.hoverEvent(HoverEvent.showText(hoverComponent));



            ClickEvent clickEvent = getClickAction(element.getClickActions().split(";"));

            if(clickEvent != null){
                textComponent = textComponent.clickEvent(clickEvent);
            }

            temporalComponentList.add(textComponent);
        }

        temporalComponentList.add(SerializerUtils.getLegacyToMinimessage(player, format.getMessageSeparator(), false));

        if(!player.hasPermission("amazingchat.chatcolor")){
            temporalComponentList.add(Component.text(message).asComponent());
        } else {
            temporalComponentList.add(SerializerUtils.getLegacyToMinimessage(player, message, false));
        }

        Component finalComponent = Component.join(JoinConfiguration.noSeparators(), temporalComponentList);

        Utils.sendDebug("Format processed for: " + player.getName() + ", final output: " + PlainTextComponentSerializer.plainText().serialize(finalComponent));
        return finalComponent;
    }

    //TO-DO: Create a more extensible version of this
    private ClickEvent getClickAction(String[] parameters){
        if(parameters.length < 2){
            return null;
        }

        String actionType = parameters[0].toLowerCase(Locale.ROOT);
        String argument = String.join(" ", Arrays.copyOfRange(parameters, 1, parameters.length));

        switch (actionType){
            case "suggest_command" -> {
                return ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, argument);
            }

            case "force_command" -> {
                return ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, argument);
            }

            case "open_url" -> {
                if (!argument.startsWith("http")) return null;
                return ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, argument);
            }

            case "copy_to_clipboard" -> {
                return ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, argument);
            }

            default -> {
                return null;
            }
        }
    }
}
