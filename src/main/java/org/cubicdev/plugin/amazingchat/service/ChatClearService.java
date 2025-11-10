/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.service;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.modules.impl.ChatClearModule;
import org.cubicdev.plugin.amazingchat.utils.Utils;

public class ChatClearService {
    private AmazingChat main;
    private ChatClearModule chatClearModule;

    public ChatClearService(AmazingChat main, ChatClearModule chatClearModule){
        this.main = main;
        this.chatClearModule = chatClearModule;
    }

    public void clearChat(CommandSender executor){
        Utils.sendMessage(executor, chatClearModule.getChatClearedMessage());

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.hasPermission(chatClearModule.getBypassPermission()))
                .forEach(player -> {
                    for(int i = 0; i<chatClearModule.getBlankLines(); i++){
                        player.sendMessage("");
                    }
                });
    }
}
