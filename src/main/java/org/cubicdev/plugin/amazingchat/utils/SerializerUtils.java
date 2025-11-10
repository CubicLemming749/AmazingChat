package org.cubicdev.plugin.amazingchat.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerializerUtils {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final String LEGACY_PATTERN_STRING = "&(?<normalCode>[0-9a-fk-or])|[&{\\[<]?[#x](?<hexCode>[A-Fa-f0-9]{6})[}\\]>]?";

    private static final Pattern LEGACY_PATTERN = Pattern.compile(LEGACY_PATTERN_STRING, Pattern.CASE_INSENSITIVE);

    private static final Map<Character, String> LEGACY_TO_MINIMESSAGE = Map.ofEntries(
            Map.entry('1', "dark_blue"),
            Map.entry('2', "dark_green"),
            Map.entry('3', "dark_aqua"),
            Map.entry('4', "dark_red"),
            Map.entry('5', "dark_purple"),
            Map.entry('6', "gold"),
            Map.entry('7', "gray"),
            Map.entry('8', "dark_gray"),
            Map.entry('9', "blue"),
            Map.entry('a', "green"),
            Map.entry('b', "aqua"),
            Map.entry('c', "red"),
            Map.entry('d', "light_purple"),
            Map.entry('e', "yellow"),
            Map.entry('f', "white"),
            Map.entry('k', "obfuscated"),
            Map.entry('l', "bold"),
            Map.entry('m', "strikethrough"),
            Map.entry('n', "underlined"),
            Map.entry('o', "italic"),
            Map.entry('r', "reset")
    );

    /**
     * This method was specifically created to handle placeholders in the chat formats and
     * other places, because the almost all the placeholders return the value in legacy format.
     * @param player The player we want to parse placeholders
     * @param msg The message
     * @return The text transformed from legacy to minimessage
     */
    private static String legacyToMinimessage(Player player, String msg, boolean checkPlaceholders){
        if(msg == null || msg.isEmpty()) return msg;

        String proccesedMessage = msg;

        if (checkPlaceholders) {
            proccesedMessage = PlaceholderAPI.setPlaceholders(player, msg);
        }

        if(!proccesedMessage.contains("&")){
            return proccesedMessage;
        }

        Stack<String> formatters = new Stack<>();

        String result = LEGACY_PATTERN.matcher(proccesedMessage.replace("§", "&")).replaceAll(matchResult -> {
            Matcher legacyMatcher = (Matcher) matchResult;
            String hexCode = legacyMatcher.group("hexCode");
            String normalCode = legacyMatcher.group("normalCode");

            String finalCode = hexCode != null ? "#" + hexCode.replace("&", "") :
                    LEGACY_TO_MINIMESSAGE.get(normalCode.charAt(0));

            if(normalCode != null && isFormatter(normalCode.charAt(0))){
                String formatCode = LEGACY_TO_MINIMESSAGE.get(normalCode.charAt(0));
                formatters.push(formatCode);
                return "<" + formatCode + ">";
            } else {
                StringBuilder builder = closeFormats(formatters);
                builder.append("<").append(finalCode).append(">");
                return builder.toString();
            }
        });

        return result;
    }

    /**
     * It will close all the formatters that are stacked in a message.
     * @param formatters The formatters stack
     * @return The StringBuilder with the closed formats.
     */
    private static StringBuilder closeFormats(Stack<String> formatters){
        StringBuilder output = new StringBuilder();
        while(!formatters.isEmpty()){
            output.append("</").append(formatters.pop()).append(">");
        }

        return output;
    }

    public static boolean isFormatter(char code){
        switch (code){
            case 'k', 'l', 'n', 'm', 'o' -> {
                return true;
            }

            default -> {
                return false;
            }
        }
    }

    public static Component getLegacyToMinimessage(Player player, String input, boolean checkPlaceholders){
        return MINI_MESSAGE.deserialize(legacyToMinimessage(player, input, checkPlaceholders));
    }
}
