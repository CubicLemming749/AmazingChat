/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.modules.impl.MessagesModule;
import org.cubicdev.plugin.amazingchat.service.PrivateMessagesService;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class MsgCommand implements IPluginCommand {
    private final AmazingChat main;
    private final PrivateMessagesService privateMessagesService;
    private final MessagesModule module;

    public MsgCommand(AmazingChat main, PrivateMessagesService privateMessagesService, MessagesModule module) {
        this.main = main;
        this.privateMessagesService = privateMessagesService;
        this.module = module;

        setupCommand();
    }

    @Override
    public void setupCommand() {
        /*
        Handle the /msg command
         */
        LiteralArgumentBuilder<CommandSourceStack> msg = LiteralArgumentBuilder.literal("msg");

        msg.requires(executor -> executor.getExecutor() == null || executor.getExecutor().hasPermission(module.getMsgCommandPermission()));
        msg.then(Commands.argument("target", StringArgumentType.word())
                .suggests(this::getPlayerNames)
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> {

                            if(!(context.getSource().getSender() instanceof Player player)){
                                Utils.sendMessage(context.getSource().getSender(), "<red>Console cannot use this message.");
                                return 0;
                            }

                            String message = context.getArgument("message", String.class);
                            Player target = Bukkit.getPlayer(context.getArgument("target", String.class));
                            privateMessagesService.sendPrivateMessage(player, target, message);

                            return Command.SINGLE_SUCCESS;
                })));

        LiteralCommandNode<CommandSourceStack> msgCommandNode = msg.build();

        /*
        Handle the /reply command
         */
        LiteralArgumentBuilder<CommandSourceStack> reply = LiteralArgumentBuilder.literal("reply");

        reply.requires(executor -> executor.getExecutor() == null || executor.getExecutor().hasPermission(module.getReplyCommandPermission()));
        reply.then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(context -> {

                    if(!(context.getSource().getSender() instanceof Player player)){
                        Utils.sendMessage(context.getSource().getSender(), "<red>Console cannot use this message.");
                        return 0;
                    }

                    String message = context.getArgument("message", String.class);
                    privateMessagesService.replyPrivateMessage(player, message);
                    return Command.SINGLE_SUCCESS;
                }));

        LiteralCommandNode<CommandSourceStack> replyCommandNode = reply.build();

        /*
        Handle the /ignore command
         */
        LiteralArgumentBuilder<CommandSourceStack> ignore = LiteralArgumentBuilder.literal("ignore");

        ignore.requires(executor -> executor.getSender().hasPermission(module.getIgnoreCommandPermission()));
        ignore.then(Commands.argument("target", StringArgumentType.greedyString())
                .suggests(this::getPlayerNames)
                .executes(context -> {

                    if(!(context.getSource().getSender() instanceof Player player)){
                        Utils.sendMessage(context.getSource().getSender(), "<red>Console cannot use this message.");
                        return 0;
                    }

                    privateMessagesService.ignorePlayer(player, context.getArgument("target", String.class));
                    return Command.SINGLE_SUCCESS;
                }));

        LiteralCommandNode<CommandSourceStack> ignoreCommandNode = ignore.build();

        /*
        Handle the /unignore command
         */
        LiteralArgumentBuilder<CommandSourceStack> unignore = LiteralArgumentBuilder.literal("unignore");
        unignore.requires(executor -> executor.getSender().hasPermission(module.getUnignoreCommandPermission()));
        unignore.then(Commands.argument("target", StringArgumentType.greedyString())
                .suggests(this::getPlayerNames)
                .executes(context -> {

                    if(!(context.getSource().getSender() instanceof Player player)){
                        Utils.sendMessage(context.getSource().getSender(), "<red>Console cannot use this message.");
                        return 0;
                    }

                    privateMessagesService.unignorePlayer(player, context.getArgument("target", String.class));
                    return Command.SINGLE_SUCCESS;
                }));

        LiteralCommandNode<CommandSourceStack> unignoreCommandNode = unignore.build();

        /*
        Handle the /togglemsg command
         */
        LiteralArgumentBuilder<CommandSourceStack> toggleMsg = LiteralArgumentBuilder.literal("togglemsg");
        toggleMsg.requires(executor -> executor.getSender().hasPermission(module.getToggleMsgCommandPermission()));
        toggleMsg.executes(context -> {

            if(!(context.getSource().getSender() instanceof Player player)){
                Utils.sendMessage(context.getSource().getSender(), "<red>Console cannot use this message.");
                return 0;
            }

            privateMessagesService.toggleMessages(player);
            return Command.SINGLE_SUCCESS;
        });

        LiteralCommandNode<CommandSourceStack> toggleMsgNode = toggleMsg.build();

        /*
        Handle the /showignored command
         */
        LiteralArgumentBuilder<CommandSourceStack> showIgnored = LiteralArgumentBuilder.literal("showignored");
        showIgnored.requires(executor -> executor.getSender().hasPermission(module.getIgnoreCommandPermission()));
        showIgnored.executes(context -> {

            if(!(context.getSource().getSender() instanceof Player player)){
                Utils.sendMessage(context.getSource().getSender(), "<red>Console cannot use this message.");
                return 0;
            }

            privateMessagesService.showIgnoredPeople(player);
            return Command.SINGLE_SUCCESS;
        });

        LiteralCommandNode<CommandSourceStack> showIgnoredNode = showIgnored.build();

        main.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(msgCommandNode, module.getMsgCommandAliases());
            commands.registrar().register(replyCommandNode, module.getReplyCommandAliases());
            commands.registrar().register(ignoreCommandNode, module.getIgnoreCommandAliases());
            commands.registrar().register(unignoreCommandNode, module.getUnignoreCommandAliases());
            commands.registrar().register(toggleMsgNode, module.getToggleMsgCommandAliases());
            commands.registrar().register(showIgnoredNode, module.getShowIgnoredCommandAliases());
        });
    }

    private CompletableFuture<Suggestions> getPlayerNames(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder){
        List<String> temporal = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> temporal.add(player.getName()));

        temporal.stream()
                .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}
