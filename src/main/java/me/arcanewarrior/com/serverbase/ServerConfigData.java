package me.arcanewarrior.com.serverbase;

public record ServerConfigData(int maxPlayers,
                               int serverPort,
                               boolean forceResourcePack,
                               String resourcePackURL,
                               String resourcePackHash) {
}
