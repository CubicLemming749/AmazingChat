/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.handler;

import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.managers.PlayerManager;
import org.cubicdev.plugin.amazingchat.model.PlayerData;
import org.cubicdev.plugin.amazingchat.storage.PlayerStorage;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

public class JoinHandler {
    private PlayerStorage storage;
    private PlayerManager playerManager;

    public JoinHandler(PlayerStorage storage, PlayerManager playerManager){
        this.storage = storage;
        this.playerManager = playerManager;
    }

    public void handleJoin(Player player){
        storage.playerExists(player.getUniqueId()).thenAccept(exists -> {
            if(!exists){
                storage.createDefaultPlayerData(player);
                playerManager.addPlayer(player.getUniqueId());
                return;
            }

            storage.retrievePlayerData(player).thenAccept((data) -> {
                playerManager.addPlayerData(data);
            }).exceptionally(e -> {
                Utils.sendLog(LogLevel.ERROR, "Error while trying to retrieve player data from database for " + player.getName());
                Utils.sendLog(LogLevel.ERROR, "Stackrace:");
                e.printStackTrace();

                //Adding the player to memory to avoid errors
                playerManager.addPlayer(player.getUniqueId());
                return null;
            });
        });
    }

    public void handleLeave(Player player){
        PlayerData data = playerManager.getDataByUuid(player.getUniqueId()).get();
        storage.savePlayerData(data);
        playerManager.removePlayer(player.getUniqueId());
    }
}
