package me.youhavetrouble.ServerPingerApi;

import org.springframework.http.ResponseEntity;

public enum PingError {

    COULD_NOT_PARSE_PORT(200,"{ error: \"Could not parse the port number\" }"),
    SERVER_OFFLINE(200, "{ error: \"Server is offline\" }"),
    TIMED_OUT(200, "{ error: \"Status request timed out\" }");

    private final ResponseEntity<String> response;

    PingError(int httpCode, String error) {
        this.response = ResponseEntity.status(httpCode).body(error);
    }

    public ResponseEntity<String> getResponse() {
        return response;
    }
}
