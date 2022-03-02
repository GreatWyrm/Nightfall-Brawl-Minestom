package me.arcanewarrior.com.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentPotionEffect;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Entity;
import net.minestom.server.potion.Potion;

import java.util.Locale;

public class EffectCommand extends Command {
    public EffectCommand() {
        super("effect");

        ArgumentWord modeArg = ArgumentType.Word("mode").from("give", "remove");
        ArgumentPotionEffect effectArg = ArgumentType.Potion("potion");
        ArgumentEntity entityArg = ArgumentType.Entity("targets");
        ArgumentInteger durationArg = ArgumentType.Integer("duration");
        ArgumentInteger levelArg = ArgumentType.Integer("level");
        durationArg.setDefaultValue(30);
        levelArg.setDefaultValue(0);

        addSyntax((sender, context) -> {
            for(Entity entity : context.get(entityArg).find(sender)) {
                switch (context.get(modeArg).toLowerCase(Locale.ROOT)) {
                    case "give" -> entity.addEffect(new Potion(context.get(effectArg), context.get(levelArg).byteValue(), context.get(durationArg)));
                    case "remove" -> entity.removeEffect(context.get(effectArg));
                }
            }
        }, modeArg, effectArg, entityArg, durationArg, levelArg);
    }
}
