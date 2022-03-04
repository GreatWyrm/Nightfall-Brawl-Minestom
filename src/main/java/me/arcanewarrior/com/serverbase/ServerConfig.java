package me.arcanewarrior.com.serverbase;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.resourcepack.ResourcePack;
import net.minestom.server.utils.identity.NamedAndIdentified;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ServerConfig {
    private static final Path CONFIG_PATH = Path.of("server-config.yml");
    private static ServerConfigData serverConfigData = null;
    public static ResourcePack currentResourcePack = null;
    private static int maxPlayers = 0;

    public static void init() {
        // Load config
        loadServerConfig();
        // Set Response Data
        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class,
                serverListPingEvent -> serverListPingEvent.setResponseData(getServerResponseData()));
        // Set Resource Pack
        loadResourcePack();
    }

    public static int getServerPort() {
        return serverConfigData.serverPort();
    }

    public static void loadServerConfig() {
        if(!Files.exists(CONFIG_PATH)) {
            MinecraftServer.LOGGER.info("No " + CONFIG_PATH + " (config file) found! Creating file with defaults...");
            try {
                Files.copy(Objects.requireNonNull(ServerConfig.class.getClassLoader().getResourceAsStream(CONFIG_PATH.toString())), CONFIG_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(CONFIG_PATH)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
        try {
            CommentedConfigurationNode root = loader.load();
            serverConfigData = new ServerConfigData(
                    root.node("maxPlayers").getInt(20),
                    root.node("serverPort").getInt(25565),
                    LegacyComponentSerializer.legacyAmpersand().deserialize(root.node("motd").getString("")),
                    root.node("forceResourcePack").getBoolean(false),
                    root.node("resourcePackURL").getString(""),
                    root.node("resourcePackHash").getString("")
            );
            // Save stuff in node
            root.node("maxPlayers").set(serverConfigData.maxPlayers());
            root.node("serverPort").set(serverConfigData.serverPort());
            root.node("motd").set(LegacyComponentSerializer.legacyAmpersand().serialize(serverConfigData.serverMOTD()));
            root.node("forceResourcePack").set(serverConfigData.forceResourcePack());
            root.node("resourcePackURL").set(serverConfigData.resourcePackURL());
            root.node("resourcePackHash").set(serverConfigData.resourcePackHash());

            // Write back in case anything was missed
            loader.save(root);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
        maxPlayers = serverConfigData.maxPlayers();
    }

    private static void loadResourcePack() {
        if(serverConfigData == null) {
            MinecraftServer.LOGGER.warn("Tried to load the resource pack before the Server Config Data was loaded!");
            return;
        }
        if(serverConfigData.resourcePackURL() == null || serverConfigData.resourcePackURL().isEmpty()) {
            return;
        }
        if(serverConfigData.forceResourcePack()) {
            currentResourcePack = ResourcePack.forced(serverConfigData.resourcePackURL(), serverConfigData.resourcePackHash());
        } else {
            currentResourcePack = ResourcePack.optional(serverConfigData.resourcePackURL(), serverConfigData.resourcePackHash());
        }
        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, playerLoginEvent -> playerLoginEvent.getPlayer().setResourcePack(currentResourcePack));
    }

    private static ResponseData getServerResponseData() {
        var data = new ResponseData();
        data.setMaxPlayer(maxPlayers);
        for(var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            data.addEntry(NamedAndIdentified.of(player.getUsername(), player.getUuid()));
        }
        data.setDescription(serverConfigData.serverMOTD());
        return data;
    }
}
