package me.arcanewarrior.com;

import net.minestom.server.MinecraftServer;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class ServerConfig {
    private static final String CONFIG_PATH = "server-config.yml";
    public static ServerConfigData serverConfigData = null;

    public static void loadServerConfig() {
        File file = new File(CONFIG_PATH);
        Yaml yaml = new Yaml();
        if(file.exists()) {
            try {
                serverConfigData = yaml.loadAs(new FileInputStream(CONFIG_PATH), ServerConfigData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MinecraftServer.LOGGER.info("No " + CONFIG_PATH + " (config file) found! Using defaults...");
            Map<String, Object> configSettings = yaml.load(ServerConfig.class.getClassLoader().getResourceAsStream(CONFIG_PATH));
            createDataFromConfiguration(configSettings);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(CONFIG_PATH));
                bw.write(yaml.dumpAsMap(serverConfigData));
                bw.close();
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
