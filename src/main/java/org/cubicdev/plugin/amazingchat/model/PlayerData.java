/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.model;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
    public boolean modified;

    private final UUID uuid;
    private Set<UUID> ignoredPlayers;
    private boolean mentionable;
    private boolean receivesMessages;
    private UUID lastMessageTarget;

    public PlayerData(UUID uuid){
        this.uuid = uuid;
        this.ignoredPlayers = new HashSet<>();
        this.mentionable = true;
        this.receivesMessages = true;
        this.lastMessageTarget = null;
        this.modified = false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<UUID> getIgnoredPlayers(){
        return ignoredPlayers;
    }

    public boolean isMentionable() {
        return mentionable;
    }

    public void setMentionable(boolean isMentionable) {
        mentionable = isMentionable;
    }

    public void addIgnoredPlayer(UUID uuid) {
        ignoredPlayers.add(uuid);
    }

    public void removeIgnoredPlayer(UUID uuid){
        ignoredPlayers.remove(uuid);
    }

    public boolean isIgnoring(UUID uuid){
        return ignoredPlayers.contains(uuid);
    }

    public Player toBukkitPlayer(){
        return Bukkit.getPlayer(uuid);
    }

    public boolean receivesMessages() {
        return receivesMessages;
    }

    public void setReceivesMessages(boolean receivesMessages) {
        this.receivesMessages = receivesMessages;
    }

    public void setLastMessageTarget(UUID lastMessageTarget) {
        this.lastMessageTarget = lastMessageTarget;
    }

    public UUID getLastMessageTarget() {
        return lastMessageTarget;
    }

    public void setIgnoredPlayers(Set<UUID> ignored){
        this.ignoredPlayers = ignored;
    }
}
