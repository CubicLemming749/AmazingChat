package org.cubicdev.plugin.amazingchat.storage.databases;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.storage.PluginDatabase;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLite implements PluginDatabase {
    private AmazingChat main;
    private HikariConfig config;
    private HikariDataSource dataSource;

    private File sqliteFile;

    public SQLite(AmazingChat main) {
        this.main = main;
        this.sqliteFile = new File(main.getDataFolder(), "amazingdata.db");
    }

    @Override
    public void configure() {
        Utils.sendLog(LogLevel.INFO, "Configuring database...");
        config = new HikariConfig();

        final String url = "jdbc:sqlite:"+sqliteFile.getAbsolutePath();
        config.setJdbcUrl(url);

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
        if(dataSource != null && !dataSource.isClosed()){
            Utils.sendLog(LogLevel.INFO,"Closing database...");
            dataSource.close();
        }
    }

    @Override
    public void createTables() {
        String query = "CREATE TABLE IF NOT EXISTS players (" +
                "uuid TEXT PRIMARY KEY," +
                "mentionable INTEGER NOT NULL DEFAULT 1," +
                "receivesMessages INTEGER NOT NULL DEFAULT 1," +
                "lastMessenger TEXT," +
                "blockedPlayers TEXT DEFAULT '[]'" +
                ")";
        try(Connection connection = getConn(); PreparedStatement ps = connection.prepareStatement(query)){
            Utils.sendLog(LogLevel.INFO, "Creating the necessary tables in the database...");
            ps.executeUpdate();
        } catch (SQLException e) {
            Utils.sendLog(LogLevel.ERROR, "Error while trying to create tables. Stackrace:");
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
}
