/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.managers;

import org.cubicdev.plugin.amazingchat.modules.Module;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class ModulesManager {
    private final Map<Module, Boolean> modulesMap;

    public ModulesManager(){
        modulesMap = new HashMap<>();
    }

    public Optional<Module> getModuleByName(String name){
        return modulesMap.entrySet().stream()
                .filter(entry -> entry.getKey().getName().equalsIgnoreCase(name))
                .findFirst()
                .map(Map.Entry::getKey);
    }

    public void registerModule(Module module, boolean isEnabled){
        Utils.sendLog(LogLevel.INFO, "Registering chat module: "+module.getName());
        modulesMap.put(module, isEnabled);
    }

    public void toggleModule(Module module, boolean newValue){
        modulesMap.computeIfPresent(module, (m, v) -> newValue);
    }

    public boolean isModuleEnabled(Module module){
        return modulesMap.get(module);
    }

    public Set<Module> getEnabledModules() {
        return modulesMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<Module> getDisabledModules() {
        return modulesMap.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void reloadModules(){
        getEnabledModules().forEach(Module::reloadModule);
    }
}
