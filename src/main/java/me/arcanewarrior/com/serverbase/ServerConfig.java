package me.arcanewarrior.com.serverbase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.resourcepack.ResourcePack;
import net.minestom.server.utils.identity.NamedAndIdentified;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServerConfig {
    private static final String CONFIG_PATH = "server-config.json";
    public static ServerConfigData serverConfigData = null;
    public static ResourcePack currentResourcePack = null;
    private static int maxPlayers = 0;

    public static void loadServerConfig() {
        File file = new File(CONFIG_PATH);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        if(file.exists()) {
            try {
                serverConfigData = mapper.readValue(file, ServerConfigData.class);
                // Write config after loading, as we may be missing values in the config because of updating
                writeServerConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MinecraftServer.LOGGER.info("No " + CONFIG_PATH + " (config file) found! Using defaults...");
            try {
                serverConfigData = mapper.readValue(ServerConfig.class.getClassLoader().getResourceAsStream(CONFIG_PATH), ServerConfigData.class);
                writeServerConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        maxPlayers = serverConfigData.maxPlayers();
    }

    public static void loadResourcePack() {
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

    public static void writeServerConfig() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(new FileWriter(CONFIG_PATH), serverConfigData);
        } catch (IOException e) {
            MinecraftServer.LOGGER.info("Failed to write out server config file!");
            e.printStackTrace();
        }
    }
    public static ResponseData getServerResponseData() {
        var data = new ResponseData();
        data.setMaxPlayer(maxPlayers);
        for(var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            data.addEntry(NamedAndIdentified.of(player.getUsername(), player.getUuid()));
        }
        return data;
    }
}
