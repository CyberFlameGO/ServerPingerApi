package me.youhavetrouble.ServerPingerApi.endpoints;

import me.nurio.minecraft.pinger.MinecraftServerPinger;
import me.nurio.minecraft.pinger.beans.MinecraftServerStatus;
import me.nurio.minecraft.pinger.beans.Player;
import me.nurio.minecraft.pinger.beans.Players;
import me.youhavetrouble.ServerPingerApi.PingError;
import me.youhavetrouble.ServerPingerApi.util.MinecraftQuery;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class MinecraftServerPing {

    public static ResponseEntity<String> handleRequest(String ip) {

        MinecraftServerStatus server = MinecraftServerPinger.ping(ip);
        if (server.isOffline())
            return PingError.SERVER_OFFLINE.getResponse();

        HashMap<String, Object> info = new HashMap<>();

        HashMap<String, Object> version = new HashMap<>();
        version.put("name", server.getVersion().getName());
        version.put("protocol", server.getVersion().getProtocol());
        info.put("version", version);

        HashMap<String, Object> players = new HashMap<>();
        players.put("max", server.getPlayers().getMax());
        players.put("online", server.getPlayers().getOnline());
        players.put("sample", parsePlayerData(server.getPlayers()));
        info.put("players", players);

        info.put("motd", server.getMotd());
        info.put("favicon", server.getFavicon());

        return ResponseEntity.ok(new JSONObject(info).toString());
    }

    public static ResponseEntity<String> handleQueryRequest(String ip) {

        String domain;
        int port;

        String[] address = ip.split(":");
        if (address.length == 1) {
            port = 25565;
            domain = ip;
        } else {
            if (address[1].matches("-?\\d+")) {
                port = Integer.parseInt(address[1]);
                domain = address[0];
            } else return PingError.COULD_NOT_PARSE_PORT.getResponse();
        }

        MinecraftQuery query = new MinecraftQuery(domain, port, port);

        try {
            query.sendQuery();
            HashMap<String, Object> info = new HashMap<>();

            HashMap<String, Object> host = new HashMap<>();
            host.put("ip", query.getValues().get("hostip"));
            host.put("port", query.getValues().get("hostport"));
            info.put("host", host);

            HashMap<String, Object> server = new HashMap<>();
            server.put("version", query.getValues().get("version"));
            server.put("world_name", query.getValues().get("map"));
            server.put("gametype", query.getValues().get("gametype"));
            server.put("game_id", query.getValues().get("game_id"));
            String pluginsField = query.getValues().get("plugins");
            if (pluginsField == null)
                pluginsField = "Unknown";
            server.put("mod_name", parseServerModName(pluginsField));

            server.put("plugins", parseServerMods(pluginsField));

            info.put("server", server);
            return ResponseEntity.ok(new JSONObject(info).toString());

        } catch (IOException e) {
            return PingError.SERVER_OFFLINE.getResponse();
        }


    }

    private static LinkedHashSet<HashMap<String, Object>> parsePlayerData(Players players) {
        LinkedHashSet<HashMap<String, Object>> playerSet = new LinkedHashSet<>();
        for (Player player : players.getSample()) {
            HashMap<String, Object> playerData = new HashMap<>();
            playerData.put("name", player.getName());
            playerData.put("uuid", player.getId());
            playerSet.add(playerData);
        }
        return playerSet;
    }

    private static String parseServerModName(String pluginsField) {
        if (!pluginsField.contains(":"))
            return pluginsField;
        String[] pluginsSplit = pluginsField.split(":");
        return pluginsSplit[0];
    }

    private static String[] parseServerMods(String pluginsField) {
        if (!pluginsField.contains(":"))
            return new String[0];
        String[] pluginsSplit = pluginsField.split(": ");

        if (pluginsSplit.length <= 1)
            return new String[0];

        return pluginsSplit[1].split("; ");
    }

}
