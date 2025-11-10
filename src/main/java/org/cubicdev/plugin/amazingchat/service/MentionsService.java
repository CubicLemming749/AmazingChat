/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.service;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.managers.PlayerManager;
import org.cubicdev.plugin.amazingchat.model.PlayerData;
import org.cubicdev.plugin.amazingchat.modules.impl.MentionsModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionsService {
    private final MentionsModule module;
    private final PlayerManager playerManager;

    public MentionsService(MentionsModule module, PlayerManager playerManager){
        this.module = module;
        this.playerManager = playerManager;
    }

    public void handleMention(String msg, Player sender){
        if(msg.contains(module.getMentionPrefix())) {
            Pattern pattern = Pattern.compile(module.getMentionPrefix() + "(\\w+)");
            Matcher matcher = pattern.matcher(msg);

            List<String> mentionedPeopleNames = new ArrayList<>();

            while (matcher.find()) {
                mentionedPeopleNames.add(matcher.group(1));
            }

            List<Player> mentionedPlayers = new ArrayList<>();

            for (String name : mentionedPeopleNames) {
                Player player = Bukkit.getPlayer(name);

                if (player != null) {
                    mentionedPlayers.add(player);
                }
            }

            if(mentionedPlayers.isEmpty()){
                return;
            }

            for(Player player : mentionedPlayers){
                if(player.getName().equals(sender.getName())) continue;

                Optional<PlayerData> dataOptional = playerManager.getDataByUuid(player.getUniqueId());

                if(dataOptional.isEmpty()){
                    continue;
                }

                PlayerData data = dataOptional.get();

                if(!data.isMentionable()){
                    continue;
                }

                mentionPlayer(player, sender);
            }
        }
    }

    public void mentionPlayer(Player mentioned, Player sender){
        mentioned.playSound(mentioned.getLocation(), Sound.valueOf(module.getMentionSoundName()), module.getMentionSoundVolume(), module.getMentionSoundPitch());
        mentioned.sendActionBar(MiniMessage.miniMessage().deserialize(module.getMentionActionBar()
                .replace("%sender%", sender.getName())));
    }
}
