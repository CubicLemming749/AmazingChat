/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.listener;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.cubicdev.plugin.amazingchat.UpdateChecker;
import org.cubicdev.plugin.amazingchat.handler.ChatHandler;
import org.cubicdev.plugin.amazingchat.handler.JoinHandler;
import org.cubicdev.plugin.amazingchat.managers.ModulesManager;
import org.cubicdev.plugin.amazingchat.managers.PlayerManager;
import org.cubicdev.plugin.amazingchat.model.Format;
import org.cubicdev.plugin.amazingchat.modules.impl.LockdownModule;
import org.cubicdev.plugin.amazingchat.modules.impl.MentionsModule;
import org.cubicdev.plugin.amazingchat.service.FormatsService;
import org.cubicdev.plugin.amazingchat.service.LockdownService;
import org.cubicdev.plugin.amazingchat.service.MentionsService;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.Optional;

public class PlayerListener implements Listener, ChatRenderer {
    private FormatsService formatsService;
    private PlayerManager playerManager;
    private ChatHandler chatHandler;
    private JoinHandler joinHandler;

    public PlayerListener(FormatsService formatsService, PlayerManager playerManager, ChatHandler chatHandler, JoinHandler joinHandler){
        this.formatsService = formatsService;
        this.playerManager = playerManager;
        this.chatHandler = chatHandler;
        this.joinHandler = joinHandler;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();

        joinHandler.handleJoin(player);

        if(player.hasPermission("amazingchat.notify.updates") && UpdateChecker.IS_NEW_UPDATE){
            Utils.sendMessage(player, "<green>New update for AmazingChat detected! Version: <yellow> " + UpdateChecker.NEW_VERSION);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();

        joinHandler.handleLeave(player);
    }

    @EventHandler
    public void onChat(AsyncChatEvent e){
        if(!chatHandler.handleLockdown(e.getPlayer())){
            e.setCancelled(true);
        }

        e.renderer(this);

        chatHandler.handleChatMentions(e.getPlayer(), MiniMessage.miniMessage().serialize(e.message()));
    }

    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        Optional<Format> format = formatsService.getPlayerFormat(source.getPlayer());

        if(format.isEmpty()){
            return message;
        }

        String originalMsg = PlainTextComponentSerializer.plainText().serialize(message);
        return formatsService.processFormat(source.getPlayer(), originalMsg, format.get());
    }
}
