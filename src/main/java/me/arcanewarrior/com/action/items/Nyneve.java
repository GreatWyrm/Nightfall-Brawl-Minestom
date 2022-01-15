package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.particles.ParticleGenerator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.particle.Particle;

public class Nyneve extends BaseActionItem {


    public Nyneve(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }
    @Override
    public void OnLeftClick() {
        ParticleGenerator.spawnParticlesForAll(player.getPlayer(), Particle.DUST, player.getPlayer().getPosition(), false,
                1f, 1f, 1f, 1f, 20, ParticleGenerator.createDustData(0.8f, 1f, 1f, 2f));
    }

    @Override
    public void OnRightClick() {
        // Dash towards facing direction
        player.dashTowardsFacing(40);
        player.getPlayer().playSound(Sound.sound(Key.key("entity.pillager.celebrate"), Sound.Source.AMBIENT, 0.3f, 1f), Sound.Emitter.self());
    }

    @Override
    public String getBaseItemName() {
        return "nyneve";
    }
}
