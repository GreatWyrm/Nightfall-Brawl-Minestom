package me.arcanewarrior.com;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.minestom.server.MinecraftServer;

import java.io.*;
import java.util.Map;

public class ServerConfig {
    private static final String CONFIG_PATH = "server-config.json";
    public static ServerConfigData serverConfigData = null;

    public static void loadServerConfig() {
        File file = new File(CONFIG_PATH);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        if(file.exists()) {
            try {
                serverConfigData = mapper.readValue(file, ServerConfigData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MinecraftServer.LOGGER.info("No " + CONFIG_PATH + " (config file) found! Using defaults...");
            try {
                serverConfigData = mapper.readValue(ServerConfig.class.getClassLoader().getResourceAsStream(CONFIG_PATH), ServerConfigData.class);
                mapper.writeValue(new FileWriter(CONFIG_PATH), serverConfigData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createDataFromConfiguration(Map<String, Object> configProperties) {
        serverConfigData = new ServerConfigData(
                getIntOrDefault(configProperties, "max-players", 50),
                getIntOrDefault(configProperties, "server-port", 25565)
        );
    }

    private static int getIntOrDefault(Map<String, Object> configProperties, String key, int defaultValue) {
        Object value = configProperties.get(key);
        return value instanceof Integer integer ? integer : defaultValue;
    }
}
