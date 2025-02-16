package com.qorvia.communicationservice;

import com.corundumstudio.socketio.SocketIOServer;
import com.qorvia.communicationservice.socket.ChatEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class CommunicationServiceApplication implements CommandLineRunner {

	private final SocketIOServer socketIOServer;
	private final ChatEventHandler chatEventHandler;

	public static void main(String[] args) {
		SpringApplication.run(CommunicationServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		chatEventHandler.registerListeners(socketIOServer);
		socketIOServer.start();
	}
}
