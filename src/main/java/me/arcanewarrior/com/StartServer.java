package me.arcanewarrior.com;

import me.arcanewarrior.com.serverbase.ServerConfig;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;

public class StartServer {

    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();
        // Enable Mojang Authentication, so stuff like skins will work
        MojangAuth.init();
        // Read in Server Config
        MinecraftServer.LOGGER.info("Server Base initialized, reading server-config.data...");
        ServerConfig.loadServerConfig();
        ServerConfig.loadResourcePack();
        MinecraftServer.LOGGER.info("Config initialized, loading Game Core...");
        GameCore gameCore = new GameCore();
        MinecraftServer.LOGGER.info("Game Core loaded, starting server...");
        server.start("0.0.0.0", ServerConfig.serverConfigData.serverPort());
    }
}
