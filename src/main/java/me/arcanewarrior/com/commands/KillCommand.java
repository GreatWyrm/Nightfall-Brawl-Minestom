package me.arcanewarrior.com.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.LivingEntity;

public class KillCommand extends Command {
    public KillCommand() {
        super("kill");

        ArgumentEntity entityArg = ArgumentType.Entity("target");

        addSyntax((sender, context) -> {
           for(var entity : context.get(entityArg).find(sender)) {
               if(entity instanceof LivingEntity living) {
                   living.kill();
                   sender.sendMessage("Killed " + living.getEntityType());
               }

           }
        }, entityArg);
    }
}
