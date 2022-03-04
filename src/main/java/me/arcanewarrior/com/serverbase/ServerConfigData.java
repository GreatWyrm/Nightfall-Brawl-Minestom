package me.arcanewarrior.com.serverbase;

import net.kyori.adventure.text.Component;

public record ServerConfigData(int maxPlayers,
                               int serverPort,
                               Component serverMOTD,
                               boolean forceResourcePack,
                               String resourcePackURL,
                               String resourcePackHash) {
}
