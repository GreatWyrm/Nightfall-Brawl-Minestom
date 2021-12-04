package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.ItemManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

import java.util.List;

public class ItemCommand extends Command {
    public ItemCommand() {
        super("item");
        // TODO: Better feedback on command failure/success

        ArgumentWord modeArg = ArgumentType.Word("mode").from("give");
        ArgumentString nameArg = ArgumentType.String("name");
        ArgumentEntity playerArg = ArgumentType.Entity("targets").onlyPlayers(true);
        nameArg.setSuggestionCallback((sender, context, suggestion) -> ItemManager.getManager().getAllItemNames().forEach((itemName) -> suggestion.addEntry(new SuggestionEntry(itemName))));

        setDefaultExecutor(((sender, context) -> sender.sendMessage("Usage: /item [give] <name>")));

        addSyntax(((sender, context) -> {
            if ("give".equalsIgnoreCase(context.get(modeArg))) {
                if (sender instanceof Player player) {
                    // TODO: Create an addInventory method so you aren't directly setting a slot
                    player.getInventory().setItemInMainHand(ItemManager.getManager().getItem(context.get(nameArg)));
                } else {
                    sender.sendMessage("Console cannot use this command!");
                }
            } else {
                sender.sendMessage("Unknown Mode " + context.get(modeArg));
            }
        }), modeArg, nameArg);

        addSyntax(((sender, context) -> {
            if ("give".equalsIgnoreCase(context.get(modeArg))) {
                List<Entity> entityList = context.get(playerArg).find(sender);
                entityList.forEach((entity -> {
                    if(entity instanceof Player player) {
                        player.getInventory().setItemInMainHand(ItemManager.getManager().getItem(context.get(nameArg)));
                    }
                }));
            } else {
                sender.sendMessage("Unknown Mode " + context.get(modeArg));
            }
        }), modeArg, nameArg, playerArg);
    }
}
