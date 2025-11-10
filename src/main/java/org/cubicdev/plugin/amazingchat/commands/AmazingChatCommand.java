/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.config.ConfigDefaults;
import org.cubicdev.plugin.amazingchat.managers.ConfigsManager;
import org.cubicdev.plugin.amazingchat.managers.FormatManager;
import org.cubicdev.plugin.amazingchat.managers.ModulesManager;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AmazingChatCommand implements IPluginCommand {
    private AmazingChat main;
    private ConfigsManager configsManager;
    private FormatManager formatManager;
    private ModulesManager modulesManager;
    private ConfigDefaults configDefaults;

    public AmazingChatCommand(AmazingChat main, ConfigsManager configsManager, FormatManager formatManager, ModulesManager modulesManager, ConfigDefaults configDefaults) {
        this.main = main;
        this.configsManager = configsManager;
        this.formatManager = formatManager;
        this.modulesManager = modulesManager;
        this.configDefaults = configDefaults;

        setupCommand();
    }

    @Override
    public void setupCommand() {
        LiteralArgumentBuilder<CommandSourceStack> amazingchat = LiteralArgumentBuilder.literal("amazingchat");

        amazingchat
                .then(Commands.literal("reload")
                        .requires(executor -> executor.getExecutor() == null || executor.getExecutor().hasPermission("amazingchat.command.reload"))
                        .executes(context -> {
                            configsManager.reloadConfigs();
                            formatManager.reloadFormats();
                            modulesManager.reloadModules();
                            configDefaults.setMainConfigDefaults(configsManager.getConfigByName("config.yml"));
                            Utils.sendMessage(context.getSource().getSender(), "%prefix% <green>Successfully reloaded the plugin.");
                            return Command.SINGLE_SUCCESS;
                        }));

        LiteralCommandNode<CommandSourceStack> amazingChatCommand = amazingchat.build();

        main.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(amazingChatCommand, List.of("achat"));
        });
    }
}
