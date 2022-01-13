package me.arcanewarrior.com.particles;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.utils.binary.BinaryWriter;

import javax.annotation.Nullable;

public class ParticleGenerator {

    public static ParticlePacket createParticles(Particle particleType, Pos position, float offsetX, float offsetY, float offsetZ, float particleData, int count) {
        return createParticles(particleType, position, false, offsetX, offsetY, offsetZ, particleData, count, null);
    }

    public static ParticlePacket createParticles(Particle particleType, Pos position, float offsetX, float offsetY, float offsetZ, float particleData, int count, BinaryWriter extraData) {
        return createParticles(particleType, position, false, offsetX, offsetY, offsetZ, particleData, count, extraData);
    }

    public static ParticlePacket createParticles(Particle particleType, Pos position, boolean distance, float offsetX, float offsetY, float offsetZ, float particleData, int count, @Nullable BinaryWriter extraData) {
        // No easy way to check to ensure we won't send a bad packet, i.e. a particle type requires extra data, but we don't have it
        return ParticleCreator.createParticlePacket(
                particleType,
                distance,
                position.x(),
                position.y(),
                position.z(),
                offsetX,
                offsetY,
                offsetZ,
                particleData,
                count,
                extraData == null ? null : binaryWriter -> binaryWriter.setBuffer(extraData.getBuffer())
        );
    }

    public static void spawnParticlesForPlayer(Player player, Particle particleType, Pos position, boolean distance, float offsetX, float offsetY, float offsetZ, float particleData, int count, @Nullable BinaryWriter extraData) {
        var packet = createParticles(particleType, position, distance, offsetX, offsetY, offsetZ, particleData, count, extraData);
        player.sendPacket(packet);
    }

    public static void spawnParticlesForAll(Player player, Particle particleType, Pos position, boolean distance, float offsetX, float offsetY, float offsetZ, float particleData, int count, @Nullable BinaryWriter extraData) {
        var packet = createParticles(particleType, position, distance, offsetX, offsetY, offsetZ, particleData, count, extraData);
        player.sendPacketToViewersAndSelf(packet);
    }

    /**
     * Used for Particle.Dust
     * @param red Red Value, from 0-1
     * @param green Green Value, from 0-1
     * @param blue Blue Value, from 0-1
     * @param size Size Value, from 0.01-4
     * @return BinaryWriter containing the data
     */
    public static BinaryWriter createDustData(float red, float green, float blue, float size) {
        BinaryWriter writer = new BinaryWriter();
        writer.writeFloat(red);
        writer.writeFloat(green);
        writer.writeFloat(blue);
        writer.writeFloat(size);
        return writer;
    }

    /**
     * Used for Particle.Dust_Color_Transition
     * @param fromRed Start red value, from 0-1
     * @param fromGreen start green value, from 0-1
     * @param fromBlue start blue value, from 0-1
     * @param size Size Value, from 0.01-4
     * @param toRed End red value, from 0-1
     * @param toGreen End green Value, from 0-1
     * @param toBlue End blue value, from 0-1
     * @return BinaryWriter containing the data
     */
    public static BinaryWriter createDustTransitionData(float fromRed, float fromGreen, float fromBlue, float size, float toRed, float toGreen, float toBlue) {
        BinaryWriter writer = new BinaryWriter();
        writer.writeFloat(fromRed);
        writer.writeFloat(fromGreen);
        writer.writeFloat(fromBlue);
        writer.writeFloat(size);
        writer.writeFloat(toRed);
        writer.writeFloat(toGreen);
        writer.writeFloat(toBlue);
        return writer;
    }

    /**
     * Used for Particle.Block, Particle.Block_Marker, Particle.Falling_Dust
     * @param block - The block particle to use
     * @return A BinaryWriter containing the data
     */
    public static BinaryWriter createBlockData(Block block) {
        BinaryWriter writer = new BinaryWriter();
        writer.writeVarInt(block.id());
        return writer;
    }
}
