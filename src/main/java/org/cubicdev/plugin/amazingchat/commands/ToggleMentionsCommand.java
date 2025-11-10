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
import org.bukkit.entity.Player;
import org.cubicdev.plugin.amazingchat.AmazingChat;
import org.cubicdev.plugin.amazingchat.managers.PlayerManager;
import org.cubicdev.plugin.amazingchat.model.PlayerData;
import org.cubicdev.plugin.amazingchat.modules.impl.MentionsModule;
import org.cubicdev.plugin.amazingchat.utils.Utils;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class ToggleMentionsCommand implements IPluginCommand{
    private AmazingChat main;
    private PlayerManager playerManager;
    private MentionsModule mentionsModule;

    public ToggleMentionsCommand(AmazingChat main, PlayerManager playerManager, MentionsModule mentionsModule) {
        this.main = main;
        this.playerManager = playerManager;
        this.mentionsModule = mentionsModule;

        setupCommand();
    }

    @Override
    public void setupCommand() {
        LiteralArgumentBuilder<CommandSourceStack> toggleMentionsCommand = LiteralArgumentBuilder.literal("togglementions");
        toggleMentionsCommand.requires(source -> source.getSender() == null || source.getSender().hasPermission(mentionsModule.getToggleMentionsCommandPermission()));
        toggleMentionsCommand.executes(context -> {
            if(!(context.getSource().getSender() instanceof Player player)){
                return 0;
            }

            Optional<PlayerData> dataOptional = playerManager.getDataByUuid(player.getUniqueId());

            if(dataOptional.isEmpty()){
                return 0;
            }

            PlayerData data = dataOptional.get();

            if(data.isMentionable()){
                data.modified = true;
                data.setMentionable(false);
                Utils.sendMessage(player, mentionsModule.getMentionsOffMessage());
                return Command.SINGLE_SUCCESS;
            }

            data.modified = true;
            data.setMentionable(true);
            Utils.sendMessage(player, mentionsModule.getMentionsOnMessage());

            return Command.SINGLE_SUCCESS;
        });

        LiteralCommandNode<CommandSourceStack> toggleMentionsNode = toggleMentionsCommand.build();

        main.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands ->{
            commands.registrar().register(toggleMentionsNode, mentionsModule.getToggleMentionsCommandAliases());
        });
    }
}
