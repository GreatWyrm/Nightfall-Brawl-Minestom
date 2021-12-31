package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.actionitems.ActionItemType;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;

public class ActionItemCommand extends Command {
    public ActionItemCommand() {
        super("actionitem");

        ArgumentWord modeArg = ArgumentType.Word("mode").from("give", "take");
        ArgumentEnum<ActionItemType> nameArg = ArgumentType.Enum("name", ActionItemType.class);
        //ArgumentEntity playerArg = ArgumentType.Entity("targets").onlyPlayers(true);
    }
}
