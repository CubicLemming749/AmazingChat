/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.modules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.cubicdev.plugin.amazingchat.managers.ConfigsManager;

public abstract class Module {
    protected ConfigsManager configsManager;
    protected YamlConfiguration yamlConfiguration;

    protected final String name;
    protected String permission;

    public Module(ConfigsManager configsManager, String name, String permission){
        this.configsManager = configsManager;
        this.yamlConfiguration = configsManager.getConfigByName("config.yml").getConfiguration();

        this.name = name;
        this.permission = permission;

        checkPaths();
        setValues();
    }

    /**
     * Usually checks all the configuration paths related to the module
     * @return Whether all the configurations path related to the module exist, or not
     */
    public abstract boolean checkPaths();
    /**
     * Sets the values for the fields present in the Module.
     * Usually, it sets default values in case of error or misconfiguration.
     */
    public abstract void setValues();

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public void reloadModule(){
        setValues();
    }
}
