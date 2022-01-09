package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.managers.ActionPlayerManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

import java.util.List;
import java.util.Locale;

public class ActionItemCommand extends Command {
    public ActionItemCommand() {
        super("actionitem", "acti");

        ArgumentWord modeArg = ArgumentType.Word("mode").from("give", "take");
        ArgumentEnum<ActionItemType> nameArg = ArgumentType.Enum("name", ActionItemType.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentEntity actionPlayer = ArgumentType.Entity("players").onlyPlayers(true);

        actionPlayer.setSuggestionCallback((sender, context, suggestion) -> {
            for(String name : ActionPlayerManager.getManager().getSetOfNames()) {
                suggestion.addEntry(new SuggestionEntry(name));
            }
        });

        addSyntax((sender, context) -> {
            ActionItemType itemName = context.get(nameArg);
            if(sender instanceof Player player) {
                if(ActionPlayerManager.getManager().isActionPlayer(player)) {
                    switch (context.get(modeArg).toLowerCase(Locale.ROOT)) {
                        case "give" -> ActionPlayerManager.getManager().getActionPlayer(player).giveActionItemType(itemName);
                        case "take" -> ActionPlayerManager.getManager().getActionPlayer(player).removeActionItemType(itemName);
                    }
                } else {
                    sender.sendMessage("You are not an action player!");
                }
            } else {
                sender.sendMessage("Console cannot use this command!");
            }
        }, modeArg, nameArg);

        addSyntax((sender, context) -> {
            ActionItemType itemName = context.get(nameArg);
            List<Entity> allEntites = context.get(actionPlayer).find(sender);
            List<Entity> actionPlayers = allEntites.stream().filter(entity -> entity instanceof Player player && ActionPlayerManager.getManager().isActionPlayer(player)).toList();
            switch (context.get(modeArg).toLowerCase(Locale.ROOT)) {
                case "give" -> {
                    for (Entity entity : actionPlayers) {
                        ActionPlayerManager.getManager().getActionPlayer(entity.getUuid()).giveActionItemType(itemName);
                    }
                }
                case "take" -> {
                    for (Entity entity : actionPlayers) {
                        ActionPlayerManager.getManager().getActionPlayer(entity.getUuid()).removeActionItemType(itemName);
                    }
                }
            }
        }, modeArg, nameArg, actionPlayer);
    }
}
