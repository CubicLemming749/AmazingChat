/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.service;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.modules.impl.LockdownModule;
import org.cubicdev.plugin.amazingchat.utils.Utils;

public class LockdownService {
    private boolean isChatLocked;

    private LockdownModule module;

    public LockdownService(LockdownModule module) {
        this.module = module;
    }

    public void lockChat(CommandSender executor) {
        isChatLocked = true;

        Utils.sendMessage(executor, module.getChatLockedMessage());
        Bukkit.getOnlinePlayers().forEach(player -> {
            for(String line : module.getChatLockedGlobalMessage()){
                Utils.sendMessage(player, line);
            }
        });
    }

    public void openChat(CommandSender executor){
        isChatLocked = false;

        Utils.sendMessage(executor, module.getChatUnlockedMessage());
        Bukkit.getOnlinePlayers().forEach(player -> {
            for(String line : module.getChatUnlockedGlobalMessage()){
                Utils.sendMessage(player, line);
            }
        });
    }

    public void tellPlayerChatIsClosed(Player player){
        Utils.sendMessage(player, module.getCannotChatMessage());
    }

    public boolean isChatLocked() {
        return isChatLocked;
    }

    public boolean checkChatForPlayer(Player player){
        return isChatLocked && player.hasPermission(module.getLockdownBypassPermission());
    }
}
