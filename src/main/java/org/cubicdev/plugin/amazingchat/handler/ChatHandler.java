/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.handler;

import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.managers.ModulesManager;
import org.cubicdev.plugin.amazingchat.modules.impl.LockdownModule;
import org.cubicdev.plugin.amazingchat.modules.impl.MentionsModule;
import org.cubicdev.plugin.amazingchat.service.LockdownService;
import org.cubicdev.plugin.amazingchat.service.MentionsService;

public class ChatHandler {
    private final ModulesManager modulesManager;
    private final LockdownService lockdownService;
    private final MentionsService mentionsService;

    private final LockdownModule lockdownModule;
    private final MentionsModule mentionsModule;

    public ChatHandler(ModulesManager modulesManager, LockdownService lockdownService, MentionsService mentionsService, LockdownModule lockdownModule, MentionsModule mentionsModule) {
        this.modulesManager = modulesManager;
        this.lockdownService = lockdownService;
        this.mentionsService = mentionsService;
        this.lockdownModule = lockdownModule;
        this.mentionsModule = mentionsModule;
    }

    public void handleChatMentions(Player player, String msg){
        if(!modulesManager.isModuleEnabled(lockdownModule)){
            return;
        }

        if(msg == null || msg.isEmpty()){
            return;
        }

        if(!player.hasPermission(mentionsModule.getPermission())){
            return;
        }

        mentionsService.handleMention(msg, player);
    }

    /**
     *
     * @param player The player
     * @return Whether the player can chat or not
     */
    public boolean handleLockdown(Player player){
        if(!modulesManager.isModuleEnabled(lockdownModule)){
            return true;
        }

        if(!lockdownService.isChatLocked()){
            return true;
        }

        if(!lockdownService.checkChatForPlayer(player)){
            lockdownService.tellPlayerChatIsClosed(player);
            return false;
        }

        return true;
    }
}
