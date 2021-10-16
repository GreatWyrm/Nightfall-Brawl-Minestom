package me.arcanewarrior.com;

import net.minestom.server.MinecraftServer;

public class StartServer {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.LOGGER.info("Server Base initialized, loading Game Core...");
        GameCore gameCore = new GameCore();
        MinecraftServer.LOGGER.info("Game Core loaded, starting server...");
        server.start("0.0.0.0", 25565);
    }


}
