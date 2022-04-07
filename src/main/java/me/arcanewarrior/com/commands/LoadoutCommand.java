package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.LoadoutManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LoadoutCommand extends Command {
    public LoadoutCommand() {
        super("loadout");

        setDefaultExecutor((sender, context) -> {
            if(sender instanceof Player player) {
                LoadoutManager.getManager().displayLoadoutMenu(player);
            } else {
                sender.sendMessage("Console cannot use this command!");
            }
        });
    }
}
