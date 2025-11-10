/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.managers;

import org.cubicdev.plugin.amazingchat.model.PlayerData;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {
    private Set<PlayerData> players;

    public PlayerManager(){
        players = new HashSet<>();
    }

    public void addPlayer(UUID uuid){
        Utils.sendDebug("Added new player to data: "+uuid.toString());
        players.add(new PlayerData(uuid));
    }

    public void addPlayerData(PlayerData data){
        Utils.sendDebug("Added new player to memory data: " + data.toBukkitPlayer().getName());
        players.add(data);
    }

    public void removePlayer(UUID uuid){
        Utils.sendDebug("Removed a player from data: "+uuid.toString());
        players.removeIf(player -> player.getUuid().equals(uuid));
    }

    public Optional<PlayerData> getDataByUuid(UUID uuid){
        return players.stream().filter(player -> player.getUuid().equals(uuid)).findFirst();
    }

    public Set<PlayerData> getPlayers() {
        return players;
    }
}
