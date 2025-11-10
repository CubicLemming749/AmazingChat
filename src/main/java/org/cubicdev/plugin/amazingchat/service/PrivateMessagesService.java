/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.service;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.managers.PlayerManager;
import org.cubicdev.plugin.amazingchat.model.PlayerData;
import org.cubicdev.plugin.amazingchat.modules.impl.MessagesModule;
import org.cubicdev.plugin.amazingchat.storage.PlayerStorage;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PrivateMessagesService {
    private final MessagesModule module;
    private final PlayerManager playerManager;
    private final PlayerStorage storage;

    public PrivateMessagesService(MessagesModule module, PlayerManager playerManager, PlayerStorage storage){
        this.module = module;
        this.playerManager = playerManager;
        this.storage = storage;
    }

    public void sendPrivateMessage(Player sender, Player receiver, String msg){
        if(receiver == null){
            Utils.sendMessage(sender, module.getPlayerOfflineMessage());
            return;
        }

        if(sender.getUniqueId().equals(receiver.getUniqueId())){
            Utils.sendMessage(sender, module.getCannotMessageYourself());
            return;
        }

        Optional<PlayerData> optionalSenderData = playerManager.getDataByUuid(sender.getUniqueId());
        Optional<PlayerData> optionalReceiverData = playerManager.getDataByUuid(receiver.getUniqueId());

        if(optionalReceiverData.isEmpty() || optionalSenderData.isEmpty()){
            Utils.sendMessage(sender, module.getErrorMessage());
            Utils.sendLog(LogLevel.ERROR, "There was an error while sending a private message to " + receiver.getName() + " from " + sender.getName() + ", their data doesn't exist in the plugin, " +
                    "it is recommended to contact the plugin developer.");
            return;
        }

        PlayerData senderData = optionalSenderData.get();
        PlayerData receiverData = optionalReceiverData.get();

        if(receiverData.isIgnoring(senderData.getUuid())){
            Utils.sendMessage(sender, module.getYoureIgnoredMessage().replace("%player%", receiver.getName()));
            return;
        }

        if(!receiverData.receivesMessages()){
            Utils.sendMessage(sender, module.getPlayerIsNotRecevingMsgMessage().replace("%player%", receiver.getName()));
            return;
        }

        final String senderMessageFormat = module.getFormatSenderMessage().replace("%player%", sender.getName())
                .replace("%target%", receiver.getName())
                .replace("%message%", msg);

        final String receiverMessageFormat = module.getFormatReceiverMessage().replace("%sender%", sender.getName())
                .replace("%player%", receiver.getName())
                .replace("%message%", msg);

        Utils.sendMessage(sender, senderMessageFormat);
        Utils.sendMessage(receiver, receiverMessageFormat);

        senderData.setLastMessageTarget(receiverData.getUuid());
        receiverData.setLastMessageTarget(senderData.getUuid());

        receiver.playSound(sender.getLocation(), Sound.valueOf(module.getMessageSoundName()), module.getMessageSoundVolume(), module.getMessageSoundPitch());
    }

    public void replyPrivateMessage(Player player, String msg){
        Optional<PlayerData> dataOptional = playerManager.getDataByUuid(player.getUniqueId());
        if(dataOptional.isEmpty()){
            Utils.sendMessage(player, module.getErrorMessage());
            return;
        }

        PlayerData data = dataOptional.get();

        if(data.getLastMessageTarget() == null){
            Utils.sendMessage(player, module.getCannotReplyMessage());
            return;
        }

        sendPrivateMessage(player, Bukkit.getPlayer(data.getLastMessageTarget()), msg);
    }

    public void ignorePlayer(Player player, String targetName){
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);

        if(player.getName().equals(targetName)){
            Utils.sendMessage(player, module.getCannotMessageYourself());
            return;
        }

        if(!offlineTarget.isConnected() && !offlineTarget.hasPlayedBefore()){
            Utils.sendMessage(player, module.getPlayerDoesntExistMessage());
            return;
        }

        Optional<PlayerData> dataOptional = playerManager.getDataByUuid(player.getUniqueId());

        if(dataOptional.isEmpty()){
            return;
        }

        PlayerData data = dataOptional.get();

        if(data.isIgnoring(offlineTarget.getUniqueId())){
            Utils.sendMessage(player, module.getAlreadyIgnoredMessage().replace("%player%", targetName));
            return;
        }

        data.addIgnoredPlayer(offlineTarget.getUniqueId());
        data.modified = true;
        Utils.sendMessage(player, module.getSuccessfullyIgnoredMessage().replace("%player%", targetName));
    }

    public void unignorePlayer(Player player, String targetName){
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);

        if(!offlineTarget.isConnected() && !offlineTarget.hasPlayedBefore()){
            Utils.sendMessage(player, module.getPlayerDoesntExistMessage());
            return;
        }

        Optional<PlayerData> dataOptional = playerManager.getDataByUuid(player.getUniqueId());

        if(dataOptional.isEmpty()){
            return;
        }

        PlayerData data = dataOptional.get();

        if(!data.isIgnoring(offlineTarget.getUniqueId())){
            Utils.sendMessage(player, module.getNotIgnoredMessage().replace("%player%", targetName));
            return;
        }

        data.modified = true;
        data.removeIgnoredPlayer(offlineTarget.getUniqueId());
        Utils.sendMessage(player, module.getSuccessfullyUnignoredMessage().replace("%player%", targetName));
    }

    public void toggleMessages(Player player){
        Optional<PlayerData> dataOptional = playerManager.getDataByUuid(player.getUniqueId());

        if(dataOptional.isEmpty()){
            return;
        }

        PlayerData data = dataOptional.get();

        if(data.receivesMessages()){
            data.modified = true;
            data.setReceivesMessages(false);
            Utils.sendMessage(player, module.getToggleMsgNoMessage());
        } else {
            data.modified = true;
            data.setReceivesMessages(true);
            Utils.sendMessage(player, module.getToggleMsgYesMessage());
        }
    }

    public void showIgnoredPeople(Player player){
        Optional<PlayerData> dataOptional = playerManager.getDataByUuid(player.getUniqueId());

        if(dataOptional.isEmpty()){
            return;
        }

        PlayerData data = dataOptional.get();
        Set<UUID> ignoredOnes = data.getIgnoredPlayers();

        if(ignoredOnes.isEmpty()){
            Utils.sendMessage(player, module.getNoIgnoredOnesMessage());
            return;
        }

        Utils.sendMessage(player, module.getIgnoredListHeader());
        ignoredOnes.forEach(ignored -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(ignored);
            Utils.sendMessage(player, module.getIgnoredListFormat().replace("%player%", offlinePlayer.getName()));
        });
    }
}
