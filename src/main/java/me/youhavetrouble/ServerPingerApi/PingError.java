package me.youhavetrouble.ServerPingerApi;

import org.springframework.http.ResponseEntity;

public enum PingError {

    COULD_NOT_PARSE_PORT(400,"{ error: \"Could not parse the port number\" }"),
    SERVER_OFFLINE(502, "{ error: \"Server is offline\" }"),
    TIMED_OUT(504, "{ error: \"Status request timed out\" }");

    private final ResponseEntity<String> response;

    PingError(int httpCode, String error) {
        this.response = ResponseEntity.status(httpCode).body(error);
    }

    public ResponseEntity<String> getResponse() {
        return response;
    }
}
