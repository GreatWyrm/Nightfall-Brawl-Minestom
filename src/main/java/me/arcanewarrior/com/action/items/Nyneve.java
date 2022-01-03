package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
public class Nyneve extends BaseActionItem {


    public Nyneve(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }
    @Override
    public void OnLeftClick() {
        Pos pos = player.getPlayer().getPosition();
        ParticlePacket packet = ParticleCreator.createParticlePacket(
                Particle.DUST, false, pos.x(), pos.y(), pos.z(), 1f, 1f, 1f, 1f, 20, binaryWriter -> {
                    binaryWriter.writeFloat(0.8f);
                    binaryWriter.writeFloat(1f);
                    binaryWriter.writeFloat(1f);
                    binaryWriter.writeFloat(2f);
                });

        player.getPlayer().sendPacketToViewersAndSelf(packet);
    }

    @Override
    public void OnRightClick() {
        // Dash towards facing direction
        player.dashTowardsFacing(500);
        player.getPlayer().playSound(Sound.sound(Key.key("entity.pillager.celebrate"), Sound.Source.AMBIENT, 0.3f, 1f), Sound.Emitter.self());
    }

    @Override
    public String getBaseItemName() {
        return "nyneve";
    }
}
