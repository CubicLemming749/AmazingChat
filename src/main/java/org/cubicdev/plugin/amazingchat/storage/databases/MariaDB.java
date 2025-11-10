/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.storage.databases;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.storage.PluginDatabase;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MariaDB implements PluginDatabase {
    private final AmazingChat main;
    private HikariConfig config;
    private HikariDataSource dataSource;

    private final String databaseName;
    private final String ipAddress;
    private final String port;

    private final String username;
    private final String password;

    public MariaDB(AmazingChat main, String databaseName, String ipAddress, String port, String username, String password){
        this.main = main;
        this.databaseName = databaseName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    public void configure() {
        Utils.sendLog(LogLevel.INFO, "Configuring database...");

        config = new HikariConfig();
        //Credential settings
        final String jdbcUrl = "jdbc:mariadb://" + ipAddress + ":" + port + "/"+databaseName;
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        //Parameters
        config.addDataSourceProperty("prepStmtCacheSize", 350);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("useServerPrepStmts", true);

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
    }

    @Override
    public void connect() {
        Utils.sendLog(LogLevel.INFO, "Connecting to database...");
        dataSource = new HikariDataSource(config);
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            Utils.sendLog(LogLevel.INFO,"Closing database...");
            dataSource.close();
        }
    }

    @Override
    public void createTables() {
        String query = "CREATE TABLE IF NOT EXISTS players(" +
                "uuid VARCHAR(36) NOT NULL," +
                "mentionable TINYINT(1) NOT NULL DEFAULT 1," +
                "receivesMessages TINYINT(1) NOT NULL DEFAULT 1," +
                "lastMessenger VARCHAR(36)," +
                "blockedPlayers TEXT DEFAULT '[]'," +
                "PRIMARY KEY (uuid)" +
                ")";

        try(Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            Utils.sendLog(LogLevel.INFO, "Creating the necessary tables in the database...");
            ps.executeUpdate();
        } catch (SQLException e) {
            Utils.sendLog(LogLevel.ERROR, "Error while trying to create tables in database. Stackrace:");
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConn() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            Utils.sendLog(LogLevel.ERROR, "Error while getting a database connection. Stackrace:");
            e.printStackTrace();
            Utils.sendLog(LogLevel.ERROR, "If the problem persists, contact the plugin developer.");
            throw e;
        }
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getPort() {
        return port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
