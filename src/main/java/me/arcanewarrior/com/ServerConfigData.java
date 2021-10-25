package me.arcanewarrior.com;

// Wish this could be a record, but SnakeYAML does not play nice with records
public class ServerConfigData {

    private int maxPlayers;
    private int serverPort;

    public ServerConfigData() {
        this(50, 25565);
    }

    public ServerConfigData(int maxPlayers, int serverPort) {
        this.maxPlayers = maxPlayers;
        this.serverPort = serverPort;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
