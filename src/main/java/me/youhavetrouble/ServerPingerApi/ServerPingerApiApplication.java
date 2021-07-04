package me.youhavetrouble.ServerPingerApi;

import me.youhavetrouble.ServerPingerApi.endpoints.MinecraftServerPing;
import me.youhavetrouble.ServerPingerApi.endpoints.SourceServerPing;
import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;

@SpringBootApplication
@RestController
public class ServerPingerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerPingerApiApplication.class, args);
	}

	@Bean(name = "threadPoolTaskExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadNamePrefix("AsyncThread::");
		executor.initialize();
		return executor;
	}

	@Async("threadPoolTaskExecutor")
	@GetMapping(value = "/source/v1/{ip}", produces = "application/json")
	public ResponseEntity<String> sourceServer(@PathVariable String ip) throws JSONException {
		return SourceServerPing.handleRequest(ip);
	}

	@Async("threadPoolTaskExecutor")
	@GetMapping(value = "/source/v1/players/{ip}", produces = "application/json")
	public ResponseEntity<String> sourceServerPlayers(@PathVariable String ip) throws JSONException {
		return SourceServerPing.handlePlayerRequest(ip);
	}

	@Async("threadPoolTaskExecutor")
	@GetMapping(value = "/source/v1/info/{ip}", produces = "application/json")
	public ResponseEntity<String> sourceServerInfo(@PathVariable String ip) throws JSONException {
		return SourceServerPing.handleInfoRequest(ip);
	}

	@Async("threadPoolTaskExecutor")
	@GetMapping(value = "/minecraft/v1/{ip}", produces = "application/json")
	public ResponseEntity<String> minecraftServer(@PathVariable String ip) throws JSONException {
		return MinecraftServerPing.handleRequest(ip);
	}

	@Async("threadPoolTaskExecutor")
	@GetMapping(value = "/minecraft/v1/query/{ip}", produces = "application/json")
	public ResponseEntity<String> minecraftServerQuery(@PathVariable String ip) throws JSONException {
		return MinecraftServerPing.handleQueryRequest(ip);
	}

}
