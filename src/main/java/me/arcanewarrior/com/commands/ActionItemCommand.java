package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.action.items.ActionItemType;
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

    private final GameCore gameCore = GameCore.getGameCore();

    public ActionItemCommand() {
        super("actionitem", "acti");

        ArgumentWord modeArg = ArgumentType.Word("mode").from("give", "take");
        ArgumentEnum<ActionItemType> nameArg = ArgumentType.Enum("name", ActionItemType.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentEntity actionPlayer = ArgumentType.Entity("players").onlyPlayers(true);

        actionPlayer.setSuggestionCallback((sender, context, suggestion) -> {
            for(String name : gameCore.getBrawlPlayerNames()) {
                suggestion.addEntry(new SuggestionEntry(name));
            }
        });

        addSyntax((sender, context) -> {
            ActionItemType itemName = context.get(nameArg);
            if(sender instanceof Player player) {
                if(gameCore.isBrawlPlayer(player)) {
                    switch (context.get(modeArg).toLowerCase(Locale.ROOT)) {
                        case "give" -> gameCore.giveActionItem(player, itemName);
                        case "take" -> gameCore.removeActionItem(player, itemName);
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
            List<Entity> actionPlayers = allEntites.stream().filter(entity -> entity instanceof Player player && gameCore.isBrawlPlayer(player)).toList();
            switch (context.get(modeArg).toLowerCase(Locale.ROOT)) {
                case "give" -> {
                    for (Entity entity : actionPlayers) {
                        gameCore.giveActionItem(entity.getUuid(), itemName);
                    }
                }
                case "take" -> {
                    for (Entity entity : actionPlayers) {
                        gameCore.removeActionItem(entity.getUuid(), itemName);
                    }
                }
            }
        }, modeArg, nameArg, actionPlayer);
    }
}
