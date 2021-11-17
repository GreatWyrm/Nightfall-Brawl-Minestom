package me.arcanewarrior.com.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.EntityCreature;

public class SummonCommand extends Command {
    public SummonCommand() {
        super("summon");

        var entityType= ArgumentType.EntityType("type");

        addSyntax((sender, context) -> {
            if(sender.isPlayer()) {
                var player = sender.asPlayer();
                EntityCreature entity = new EntityCreature(context.get(entityType));
                entity.setInstance(player.getInstance(), player.getPosition());
            } else {
                sender.sendMessage("Console cannot use this command!");
            }
        }, entityType);
    }
}
