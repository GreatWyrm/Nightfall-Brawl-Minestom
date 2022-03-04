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
        MinecraftServer.LOGGER.info("Server Base initialized, reading server-config.yml data...");
        // TODO: Make this better
        // Have a server-config.yml file
        ServerConfig.init();
        MinecraftServer.LOGGER.info("Config initialized, loading Game Core...");
        GameCore gameCore = new GameCore();
        MinecraftServer.LOGGER.info("Game Core loaded, starting server...");
        server.start("0.0.0.0", ServerConfig.getServerPort());
    }
}
