/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.modules.impl.LockdownModule;
import org.cubicdev.plugin.amazingchat.service.LockdownService;

@SuppressWarnings("UnstableApiUsage")
public class ChatLockdownCommand implements IPluginCommand {
    private AmazingChat main;
    private LockdownModule lockdownModule;
    private LockdownService lockdownService;

    public ChatLockdownCommand(AmazingChat main, LockdownModule lockdownModule, LockdownService lockdownService) {
        this.main = main;
        this.lockdownModule = lockdownModule;
        this.lockdownService = lockdownService;

        setupCommand();
    }

    @Override
    public void setupCommand() {
        LiteralArgumentBuilder<CommandSourceStack> lockdownCommand = LiteralArgumentBuilder.literal("chatlockdown");
        lockdownCommand.requires(source -> source.getSender() == null || source.getSender().hasPermission(lockdownModule.getLockdownCommandPermission()));
        lockdownCommand.executes(context -> {
            if(lockdownService.isChatLocked()){
                lockdownService.openChat(context.getSource().getSender());
                return Command.SINGLE_SUCCESS;
            }

            lockdownService.lockChat(context.getSource().getSender());

           return Command.SINGLE_SUCCESS;
        });

        LiteralCommandNode<CommandSourceStack> lockdownCommandNode = lockdownCommand.build();

        main.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(lockdownCommandNode, lockdownModule.getLockdownCommandAliases());
        });
    }
}
