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
import org.cubicdev.plugin.amazingchat.managers.ModulesManager;
import org.cubicdev.plugin.amazingchat.modules.impl.ChatClearModule;
import org.cubicdev.plugin.amazingchat.service.ChatClearService;

@SuppressWarnings("UnstableApiUsage")
public class ChatClearCommand implements IPluginCommand {
    private AmazingChat main;
    private ChatClearModule module;
    private ModulesManager modulesManager;
    private ChatClearService chatClearService;

    public ChatClearCommand(AmazingChat main, ChatClearModule module, ModulesManager modulesManager, ChatClearService chatClearService){
        this.main = main;
        this.module = module;
        this.modulesManager = modulesManager;
        this.chatClearService = chatClearService;

        setupCommand();
    }

    @Override
    public void setupCommand() {
        if(!modulesManager.isModuleEnabled(module)){
            return;
        }

        LiteralArgumentBuilder<CommandSourceStack> chatClear = LiteralArgumentBuilder.literal("chatclear");
        chatClear.requires(source -> source.getSender() == null || source.getSender().hasPermission(module.getClearChatCommandPermission()));
        chatClear.executes(context -> {
            chatClearService.clearChat(context.getSource().getSender());
            return Command.SINGLE_SUCCESS;
        });

        LiteralCommandNode<CommandSourceStack> chatClearNode = chatClear.build();

        main.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(chatClearNode, module.getClearChatCommandAliases());
        });
    }
}
