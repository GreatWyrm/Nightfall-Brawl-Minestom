package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.ActionPlayerManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.Player;

public class ActionPlayerCommand extends Command {
    public ActionPlayerCommand() {
        super("actionplayer", "actp");

        ArgumentWord mode = ArgumentType.Word("mode").from("add", "remove");
        ArgumentEntity player = ArgumentType.Entity("players").onlyPlayers(true);
        ArgumentWord actionPlayer = ArgumentType.Word("actionplayers").from(ActionPlayerManager.getManager().getListOfNames().toArray(new String[0]));

        addSyntax((sender, context) -> {
            if(context.get(mode).equalsIgnoreCase("add")) {
                for(var arg : context.get(player).find(sender)) {
                    if(arg instanceof Player p) {
                        ActionPlayerManager.getManager().addActionPlayer(p);
                    }
                }
            } else {
                sender.sendMessage("Invalid mode for player argument");
            }
        }, mode, player);

        addSyntax((sender, context) -> {
            if(context.get(mode).equalsIgnoreCase("remove")) {
                String name = context.get(actionPlayer);
                ActionPlayerManager.getManager().removeActionPlayer(name);
            } else {
                sender.sendMessage("Invalid mode for Action Player argument");
            }
        }, mode, actionPlayer);
    }
}
