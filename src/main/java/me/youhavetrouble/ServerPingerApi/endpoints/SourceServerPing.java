package me.youhavetrouble.ServerPingerApi.endpoints;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;
import me.youhavetrouble.ServerPingerApi.PingError;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

public class SourceServerPing {

    public static ResponseEntity<String> handleRequest(String ip) {
        try {
            SourceServer server = new SourceServer(ip);
            server.updateServerInfo();
            server.updatePlayers();
            HashMap<String, Object> info = new HashMap<>();
            info.put("serverInfo", server.getServerInfo());
            info.put("players", parsePlayerData(server.getPlayers()));
            return ResponseEntity.ok(new JSONObject(info).toString());
        } catch (NumberFormatException e) {
            return PingError.COULD_NOT_PARSE_PORT.getResponse();
        } catch (SteamCondenserException e) {
            return PingError.SERVER_OFFLINE.getResponse();
        } catch (TimeoutException te) {
            return PingError.TIMED_OUT.getResponse();
        }
    }

    public static ResponseEntity<String> handleInfoRequest(String ip) {
        try {
            SourceServer server = new SourceServer(ip);
            server.updateServerInfo();
            HashMap<String, Object> info = new HashMap<>();
            info.put("serverInfo", server.getServerInfo());
            return ResponseEntity.ok(new JSONObject(info).toString());
        } catch (NumberFormatException e) {
            return PingError.COULD_NOT_PARSE_PORT.getResponse();
        } catch (SteamCondenserException e) {
            return PingError.SERVER_OFFLINE.getResponse();
        } catch (TimeoutException te) {
            return PingError.TIMED_OUT.getResponse();
        }
    }

    public static ResponseEntity<String> handlePlayerRequest(String ip) {
        try {
            SourceServer server = new SourceServer(ip);
            server.updatePlayers();
            HashMap<String, Object> info = new HashMap<>();
            info.put("players", parsePlayerData(server.getPlayers()));
            return ResponseEntity.ok(new JSONObject(info).toString());
        } catch (NumberFormatException e) {
            return PingError.COULD_NOT_PARSE_PORT.getResponse();
        } catch (SteamCondenserException e) {
            return PingError.SERVER_OFFLINE.getResponse();
        } catch (TimeoutException te) {
            return PingError.TIMED_OUT.getResponse();
        }
    }

    private static HashSet<HashMap<String, Object>> parsePlayerData(HashMap<String, SteamPlayer> players) {
        HashSet<HashMap<String, Object>> playerSet = new HashSet<>();
        for (SteamPlayer player : players.values()) {
            HashMap<String, Object> playerData = new HashMap<>();
            playerData.put("name", player.getName());
            playerData.put("connectTime", player.getConnectTime());
            playerData.put("score", player.getScore());
            playerSet.add(playerData);
        }
        return playerSet;
    }


}
