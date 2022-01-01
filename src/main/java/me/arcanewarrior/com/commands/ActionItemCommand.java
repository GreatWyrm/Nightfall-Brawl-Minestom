package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.managers.ActionPlayerManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;

import java.util.Locale;

public class ActionItemCommand extends Command {
    public ActionItemCommand() {
        super("actionitem", "acti");

        ArgumentWord modeArg = ArgumentType.Word("mode").from("give", "take");
        ArgumentEnum<ActionItemType> nameArg = ArgumentType.Enum("name", ActionItemType.class);
        ArgumentWord actionPlayer = ArgumentType.Word("actionplayers").from(ActionPlayerManager.getManager().getListOfNames().toArray(new String[0]));

        addSyntax((sender, context) -> {
            ActionItemType itemName = context.get(nameArg);
            String actionPlayerName = context.get(actionPlayer);
            ActionPlayer actPlayer = ActionPlayerManager.getManager().getActionPlayer(actionPlayerName);
            switch (context.get(modeArg).toLowerCase(Locale.ROOT)) {
                case "give" -> actPlayer.giveActionItemType(itemName);
                case "take" -> actPlayer.removeActionItemType(itemName);
            }
        }, modeArg, nameArg, actionPlayer);
    }
}
