package me.arcanewarrior.com.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;

import java.time.temporal.ChronoUnit;

public class SummonCommand extends Command {
    public SummonCommand() {
        super("summon");

        var entityType= ArgumentType.EntityType("type");

        addSyntax((sender, context) -> {
            if(sender instanceof Player player) {
                EntityCreature entity = new EntityCreature(context.get(entityType));
                if(player.getInstance() == null) {
                    player.sendMessage("You cannot summon creatures while in a null instance!");
                } else {
                    entity.setInstance(player.getInstance(), player.getPosition());
                    entity.addAIGroup(
                            new EntityAIGroupBuilder()
                                    .addGoalSelector(new MeleeAttackGoal(entity, 2, 2, ChronoUnit.SECONDS))
                                    .addTargetSelector(new ClosestEntityTarget(entity, 15, Player.class))
                                    .build()
                    );
                }
            } else {
                sender.sendMessage("Console cannot use this command!");
            }
        }, entityType);
    }
}
