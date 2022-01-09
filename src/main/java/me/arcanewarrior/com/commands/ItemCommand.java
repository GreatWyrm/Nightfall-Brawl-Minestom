package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.ItemManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

import java.util.List;

public class ItemCommand extends Command {
    public ItemCommand() {
        super("item");
        // TODO: Better feedback on command failure/success

        ArgumentWord modeArg = ArgumentType.Word("mode").from("give", "examine", "reload");
        ArgumentString nameArg = ArgumentType.String("name");
        ArgumentInteger stackNum = ArgumentType.Integer("amount");
        stackNum.setDefaultValue(1);
        ArgumentEntity playerArg = ArgumentType.Entity("targets").onlyPlayers(true);

        nameArg.setSuggestionCallback((sender, context, suggestion) -> ItemManager.getManager().getAllItemNames().forEach((itemName) -> suggestion.addEntry(new SuggestionEntry(itemName))));

        setDefaultExecutor((sender, context) -> sender.sendMessage("Usage: /item [give] [amount] <name>"));

        addSyntax((sender, context) -> {
            if("examine".equalsIgnoreCase(context.get(modeArg))) {
                if (sender instanceof Player player) {
                    player.sendMessage("Item Details: " + player.getItemInMainHand());
                } else {
                    sender.sendMessage("Console cannot use this command!");
                }
            } else if("reload".equalsIgnoreCase(context.get(modeArg))) {
                ItemManager.getManager().reloadItems();
                sender.sendMessage("Reloaded Item Pool");
            }
        }, modeArg);

        addSyntax((sender, context) -> {
            if ("give".equalsIgnoreCase(context.get(modeArg))) {
                if (sender instanceof Player player) {
                    ItemManager.getManager().giveItemToPlayer(context.get(nameArg), context.get(stackNum), player);
                } else {
                    sender.sendMessage("Console cannot use this command!");
                }
            } else {
                sender.sendMessage("Invalid Mode " + context.get(modeArg));
            }
        }, modeArg, nameArg, stackNum);

        addSyntax((sender, context) -> {
            if ("give".equalsIgnoreCase(context.get(modeArg))) {
                List<Entity> entityList = context.get(playerArg).find(sender);
                entityList.forEach(entity -> {
                    if(entity instanceof Player player) {
                        ItemManager.getManager().giveItemToPlayer(context.get(nameArg), context.get(stackNum), player);
                    }
                });
            } else {
                sender.sendMessage("Invalid Mode " + context.get(modeArg));
            }
        }, modeArg, nameArg, stackNum, playerArg);
    }
}
