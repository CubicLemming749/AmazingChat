/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.storage;

import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.managers.DatabaseManager;
import org.cubicdev.plugin.amazingchat.model.PlayerData;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerStorage {
    private DatabaseManager databaseManager;
    private PluginDatabase pluginDatabase;

    private final ExecutorService service;

    public PlayerStorage(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.pluginDatabase = databaseManager.getDatabase();

        this.service = Executors.newFixedThreadPool(30);
    }

    public void savePlayerData(PlayerData playerData) {
        String query = "REPLACE INTO players (uuid, mentionable, receivesMessages, lastMessenger, blockedPlayers) VALUES (?, ?, ?, ?, ?)";

        CompletableFuture.runAsync(() -> {
            try (Connection conn = pluginDatabase.getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, playerData.getUuid().toString());
                ps.setBoolean(2, playerData.isMentionable());
                ps.setBoolean(3, playerData.receivesMessages());
                ps.setString(4, playerData.getLastMessageTarget() != null ?
                playerData.getLastMessageTarget().toString() : null);
                ps.setString(5, Utils.getJsonFromSet(playerData.getIgnoredPlayers()));

                ps.executeUpdate();
                playerData.modified = false;
            } catch (SQLException e) {
                Utils.sendLog(LogLevel.ERROR, "Error while saving data into the database for player: " + playerData.toBukkitPlayer().getName());
                e.printStackTrace();
            }
        }, service);
    }

    public void createDefaultPlayerData(Player player) {
        String query = "INSERT INTO players (uuid, mentionable, receivesMessages, lastMessenger, blockedPlayers) VALUES (?, ?, ?, ?, ?)";

        CompletableFuture.runAsync(() -> {
            try (Connection conn = pluginDatabase.getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setBoolean(2, true);
                ps.setBoolean(3, true);
                ps.setString(4, null);
                ps.setString(5, null);

                ps.executeUpdate();
            } catch (SQLException e) {
                Utils.sendLog(LogLevel.ERROR, "Error while creating data into the database for player: " + player.getName());
                e.printStackTrace();
            }
        }, service);
    }

    public CompletableFuture<PlayerData> retrievePlayerData(Player player) {
        String query = "SELECT * FROM players WHERE uuid = ?";
        PlayerData retrievedData = new PlayerData(player.getUniqueId());

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pluginDatabase.getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, player.getUniqueId().toString());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boolean mentionable = rs.getBoolean("mentionable");
                        boolean receivesMessages = rs.getBoolean("receivesMessages");
                        String lastMessengerRaw = rs.getString("lastMessenger");

                        UUID lastMessenger = lastMessengerRaw != null ? UUID.fromString(lastMessengerRaw) : null;
                        String blockedPlayersRaw = rs.getString("blockedPlayers");

                        retrievedData.setMentionable(mentionable);
                        retrievedData.setReceivesMessages(receivesMessages);
                        retrievedData.setLastMessageTarget(lastMessenger);

                        Set<UUID> blockedPlayers = Utils.getSetFromJson(blockedPlayersRaw, UUID.class);
                        retrievedData.setIgnoredPlayers(blockedPlayers);
                    }
                }

            } catch (SQLException e) {
                Utils.sendLog(LogLevel.ERROR, "Error while creating data into the database for player: " + player.getName());
                e.printStackTrace();
            }

            return retrievedData;
        }, service);
    }

    public CompletableFuture<Boolean> playerExists(UUID playerUuid) {
        String query = "SELECT 1 FROM players WHERE uuid = ?";

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pluginDatabase.getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, playerUuid.toString());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                //nothing
            }

            return false;
        }, service);
    }

    public ExecutorService getService() {
        return service;
    }
}
