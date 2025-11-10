/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.storage;

import java.sql.Connection;
import java.sql.SQLException;

public interface PluginDatabase {
    void configure();
    void connect();
    void close();

    /**
     * Usually, it creates the default tables in the database;
     */
    void createTables();

    Connection getConn() throws SQLException;
}
