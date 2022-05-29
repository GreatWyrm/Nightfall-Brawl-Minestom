package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.brawl.BrawlPlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

public class SkinManager implements Manager {

    private final HashMap<String, PlayerSkin> skinList = new HashMap<>();

    private final Path skinPath = Path.of("skins.yml");

    public static SkinManager getManager() { return GameCore.getGameCore().getManager(SkinManager.class);
    }

    @Override
    public void initialize() {
        URL file = this.getClass().getClassLoader().getResource(skinPath.toString());
        if(file != null) {
            final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .url(file)
                    .indent(2)
                    .nodeStyle(NodeStyle.BLOCK)
                    .build();
            try {
                CommentedConfigurationNode root = loader.load();
                for(var node : root.childrenMap().entrySet()) {
                    loadSkin(node.getKey().toString(), node.getValue());
                }
            } catch (ConfigurateException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop() {

    }
    public Set<String> getAllSkinNames() {
        return skinList.keySet();
    }

    public PlayerSkin getSkin(String skinName) {
        return skinList.get(skinName);
    }
    public void setSkin(BrawlPlayer player, String skinName) {
        setSkin(player.getPlayer(), skinName);
    }
    public void setSkin(Player player, String skinName) {
        player.setSkin(skinList.get(skinName));
    }
    public void removeSkin(BrawlPlayer player) {
        removeSkin(player.getPlayer());
    }
    public void removeSkin(Player player) {
        player.setSkin(null);
    }
    private void loadSkin(String skinName, CommentedConfigurationNode node) {
        if(node.hasChild("skin") && node.hasChild("sign")) {
            skinList.put(skinName, new PlayerSkin(
                    node.node("skin").getString(),
                    node.node("sign").getString()
            ));
        }
    }
}
