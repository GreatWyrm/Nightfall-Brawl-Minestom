package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.cooldown.UseCooldown;
import me.arcanewarrior.com.particles.ParticleGenerator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.particle.Particle;

public class Nyneve extends BaseActionItem {

    private final UseCooldown runedashCD = new UseCooldown(2*20, this::runedash);


    public Nyneve(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }

    @Override
    public void onPlayerInput(ActionInputType inputType) {
        switch (inputType) {
            case LEFT -> ParticleGenerator.spawnParticlesForAll(player.getPlayer(), Particle.DUST, player.getPlayer().getPosition(), false,
                    1f, 1f, 1f, 1f, 20, ParticleGenerator.createDustData(0.8f, 1f, 1f, 2f));
            case RIGHT -> runedashCD.tryUse();
        }
    }

    @Override
    public void update() {
        runedashCD.update();
    }

    private void runedash() {
        player.dashTowardsFacing(40);
        player.getPlayer().playSound(Sound.sound(Key.key("entity.pillager.celebrate"), Sound.Source.AMBIENT, 0.3f, 1f), Sound.Emitter.self());
    }

    @Override
    public String getBaseItemName() {
        return "nyneve";
    }

    @Override
    public float getCooldown() {
        return runedashCD.getCooldownPercentage();
    }
}
