/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.storage.DatabaseType;
import org.cubicdev.plugin.amazingchat.storage.PluginDatabase;
import org.cubicdev.plugin.amazingchat.storage.databases.MariaDB;
import org.cubicdev.plugin.amazingchat.storage.databases.SQLite;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class DatabaseManager {
    private AmazingChat main;
    private ConfigsManager configsManager;
    private YamlConfiguration yamlConfiguration;

    private PluginDatabase database;

    private final Map<DatabaseType, Supplier<PluginDatabase>> databaseMap;

    private static final DatabaseType DEFAULT_DATABASE_TYPE = DatabaseType.SQLITE;

    public DatabaseManager(AmazingChat main, ConfigsManager configsManager) {
        this.main = main;
        this.configsManager = configsManager;
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();

        databaseMap = new HashMap<>();
        setupDatabaseMap();
        setupDatabase();
    }

    public void setupDatabaseMap(){
        String databaseName = yamlConfiguration.getString("database.database_name");
        String ipAddress = yamlConfiguration.getString("database.address");
        String port = yamlConfiguration.getString("database.port");
        String username = yamlConfiguration.getString("database.database_username");
        String password = yamlConfiguration.getString("database.database_password");

        databaseMap.put(DatabaseType.SQLITE, () -> {
            SQLite pluginDatabase = new SQLite(main);
            pluginDatabase.configure();
            pluginDatabase.connect();
            pluginDatabase.createTables();

            return pluginDatabase;
        });

        databaseMap.put(DatabaseType.MARIADB, () -> {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Utils.sendLog(LogLevel.ERROR, "MariaDB driver not found! Report this to the plugin developer. Stackrace:");
                e.printStackTrace();
                Utils.sendLog(LogLevel.ERROR, "Switching to fallback: " + DEFAULT_DATABASE_TYPE);
                return databaseMap.get(DEFAULT_DATABASE_TYPE).get();
            }

            MariaDB pluginDatabase = new MariaDB(main, databaseName, ipAddress, port, username, password);

            pluginDatabase.configure();

            try{
                pluginDatabase.connect();
                pluginDatabase.createTables();
            } catch (Exception e){
                Utils.sendLog(LogLevel.ERROR, "Error while trying to connect to MariaDB. Stackrace:");
                e.printStackTrace();
                Utils.sendLog(LogLevel.ERROR, "Switching to fallback: " + DEFAULT_DATABASE_TYPE);
                return databaseMap.get(DEFAULT_DATABASE_TYPE).get();
            }

            return pluginDatabase;
        });
    }

    public void setupDatabase(){
        String databaseType = yamlConfiguration.getString("database.type");

        if(databaseType == null){
            Utils.sendLog(LogLevel.ERROR, "Error while trying to fetch database type!");
            Utils.sendLog(LogLevel.ERROR, "Switching to fallback: " + DEFAULT_DATABASE_TYPE);
            database = databaseMap.get(DEFAULT_DATABASE_TYPE).get();
            return;
        }

        try{
            DatabaseType.valueOf(databaseType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e){
            Utils.sendLog(LogLevel.ERROR, "Error while trying to fetch database type: " + databaseType.toUpperCase(Locale.ROOT) + " is invalid!");
            Utils.sendLog(LogLevel.ERROR, "Switching to fallback: " + DEFAULT_DATABASE_TYPE);
            database = databaseMap.get(DEFAULT_DATABASE_TYPE).get();
            return;
        }

        database = databaseMap.get(DatabaseType.valueOf(databaseType.toUpperCase(Locale.ROOT))).get();
    }

    public void closeConnections(){
        database.close();
    }

    public PluginDatabase getDatabase() {
        return database;
    }
}
