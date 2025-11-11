/*
 * MIT License
 *
 * Copyright (c) 2025 CubicLemming749
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cubicdev.plugin.amazingchat;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubicdev.plugin.amazingchat.commands.*;
import org.cubicdev.plugin.amazingchat.config.Config;
import org.cubicdev.plugin.amazingchat.config.ConfigDefaults;
import org.cubicdev.plugin.amazingchat.formats.ChatFormatSerializer;
import org.cubicdev.plugin.amazingchat.handler.ChatHandler;
import org.cubicdev.plugin.amazingchat.handler.JoinHandler;
import org.cubicdev.plugin.amazingchat.listener.PlayerListener;
import org.cubicdev.plugin.amazingchat.managers.*;
import org.cubicdev.plugin.amazingchat.modules.impl.ChatClearModule;
import org.cubicdev.plugin.amazingchat.modules.impl.LockdownModule;
import org.cubicdev.plugin.amazingchat.modules.impl.MentionsModule;
import org.cubicdev.plugin.amazingchat.modules.impl.MessagesModule;
import org.cubicdev.plugin.amazingchat.service.*;
import org.cubicdev.plugin.amazingchat.storage.PlayerStorage;
import org.cubicdev.plugin.amazingchat.tasks.SaveDataTask;
import org.cubicdev.plugin.amazingchat.utils.LogLevel;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class AmazingChat extends JavaPlugin {
    public static Logger LOGGER;
    public static String PLUGIN_VERSION;

    private ChatFormatSerializer chatFormatSerializer;

    //Managers
    private ConfigsManager configsManager;
    private FormatManager formatManager;
    private ModulesManager modulesManager;
    private PlayerManager playerManager;
    private DatabaseManager databaseManager;

    //Modules
    private ChatClearModule chatClearModule;
    private LockdownModule lockdownModule;
    private MentionsModule mentionsModule;
    private MessagesModule messagesModule;

    //Services
    private FormatsService formatsService;
    private PrivateMessagesService privateMessagesService;
    private MentionsService mentionsService;
    private ChatClearService chatClearService;
    private LockdownService lockdownService;

    //Other
    private PlayerStorage playerStorage;
    private SaveDataTask saveDataTask;
    private ConfigDefaults configDefaults;


    @Override
    public void onEnable() {
        LOGGER = this.getLogger();
        PLUGIN_VERSION = this.getDescription().getVersion();

        Utils.sendLog(LogLevel.INFO, "Turning on AmazingChat!");
        Utils.sendLog(LogLevel.INFO, "Version: "+getDescription().getVersion());

        initialize();
        startModules();
        registerCommands();
        registerEvents();

        saveDataTask = new SaveDataTask(this, configsManager, playerManager, playerStorage);
        saveDataTask.startSavingData();
    }

    @Override
    public void onDisable() {
        try {
            saveDataTask.saveData().get();
        } catch (InterruptedException | ExecutionException e) {
            //nothing
        }

        playerStorage.getService().shutdown();
        databaseManager.closeConnections();
    }

    public void initialize(){
        UpdateChecker uc = new UpdateChecker();
        uc.checkForUpdates();

        Utils.sendLog(LogLevel.INFO, "Initializing necessary classes...");

        this.configsManager = new ConfigsManager();

        Utils.sendLog(LogLevel.INFO, "Creating and loading configuration files...");
        Config mainConfig = new Config("config.yml", this);
        Config formatsConfig = new Config("formats.yml", this);

        configsManager.addConfig(mainConfig);
        configsManager.addConfig(formatsConfig);

        configDefaults = new ConfigDefaults(this);
        configDefaults.setMainConfigDefaults(mainConfig);

        this.chatFormatSerializer = new ChatFormatSerializer(configsManager);
        this.formatManager = new FormatManager(chatFormatSerializer);
        this.formatsService = new FormatsService(formatManager);
        this.modulesManager = new ModulesManager();
        this.playerManager = new PlayerManager();
        this.databaseManager = new DatabaseManager(this, configsManager);

        this.playerStorage = new PlayerStorage(databaseManager);
        new Utils(mainConfig);
    }

    public void startModules(){
        YamlConfiguration config = configsManager.getConfigByName("config.yml").getConfiguration();
        this.chatClearModule = new ChatClearModule(configsManager, "amazingchat.module.chatclear");
        this.lockdownModule = new LockdownModule(configsManager, "amazingchat.module.chatlockdown");
        this.mentionsModule = new MentionsModule(configsManager, "amazingchat.module.mention");
        this.messagesModule = new MessagesModule(configsManager, "amazingchat.module.messages");

        modulesManager.registerModule(chatClearModule, config.getBoolean("modules.chat_clear.enabled"));
        modulesManager.registerModule(lockdownModule, config.getBoolean("modules.chat_lockdown.enabled"));
        modulesManager.registerModule(mentionsModule, config.getBoolean("modules.mentions.enabled"));
        modulesManager.registerModule(messagesModule, config.getBoolean("modules.messages.enabled"));

        this.privateMessagesService = new PrivateMessagesService(messagesModule, playerManager, playerStorage);
        this.mentionsService = new MentionsService(mentionsModule, playerManager);
        this.chatClearService = new ChatClearService(this, chatClearModule);
        this.lockdownService = new LockdownService(lockdownModule);
    }

    public void registerCommands(){
        if (modulesManager.isModuleEnabled(messagesModule)) {
            new MsgCommand(this, privateMessagesService, messagesModule);
        }

        if(modulesManager.isModuleEnabled(lockdownModule)){
            new ChatLockdownCommand(this, lockdownModule, lockdownService);
        }

        if(modulesManager.isModuleEnabled(chatClearModule)){
            new ChatClearCommand(this, chatClearModule, modulesManager, chatClearService);
        }

        if (modulesManager.isModuleEnabled(mentionsModule)) {
            new ToggleMentionsCommand(this, playerManager, mentionsModule);
        }
        new AmazingChatCommand(this, configsManager, formatManager, modulesManager, configDefaults);
    }

    public void registerEvents(){
        Utils.sendLog(LogLevel.INFO, "Registering events...");
        final ChatHandler chatHandler = new ChatHandler(modulesManager, lockdownService, mentionsService, lockdownModule, mentionsModule);
        final JoinHandler joinHandler = new JoinHandler(playerStorage, playerManager);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(formatsService, playerManager, chatHandler, joinHandler), this);
    }
}
