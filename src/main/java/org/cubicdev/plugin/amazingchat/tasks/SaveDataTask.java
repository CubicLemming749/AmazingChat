/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.tasks;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.managers.ConfigsManager;
import org.cubicdev.plugin.amazingchat.managers.PlayerManager;
import org.cubicdev.plugin.amazingchat.model.PlayerData;
import org.cubicdev.plugin.amazingchat.storage.PlayerStorage;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SaveDataTask {
    private AmazingChat main;
    private ConfigsManager configsManager;
    private PlayerManager playerManager;
    private PlayerStorage playerStorage;

    private YamlConfiguration config;
    private final ExecutorService executor;

    public SaveDataTask(AmazingChat main, ConfigsManager configsManager, PlayerManager playerManager, PlayerStorage playerStorage) {
        this.main = main;
        this.configsManager = configsManager;
        this.playerManager = playerManager;
        this.playerStorage = playerStorage;

        this.executor = Executors.newFixedThreadPool(20);
    }

    public void startSavingData(){
        this.config = configsManager.getConfigByName("config.yml").getConfiguration();
        long minutes = config.getLong("save_data_interval", 20);

        if(minutes <= 0){
            return;
        }

        Bukkit.getAsyncScheduler().runAtFixedRate(main, (task) -> {
            saveData();
        }, minutes, minutes, TimeUnit.MINUTES);
    }

    public void saveData(){
        Utils.sendLog(LogLevel.INFO, "Saving player data into database...");
        Set<PlayerData> copy = new HashSet<>(playerManager.getPlayers());

        for(PlayerData playerData : copy){
            if(playerData.modified){
                executor.submit( () -> playerStorage.savePlayerData(playerData));
                playerData.modified = false;
            }
        }
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
