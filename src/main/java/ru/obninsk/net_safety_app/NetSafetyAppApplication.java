package ru.obninsk.net_safety_app;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.obninsk.net_safety_app.entity.Role;
import ru.obninsk.net_safety_app.entity.TokenMode;
import ru.obninsk.net_safety_app.entity.TokenType;
import ru.obninsk.net_safety_app.entity.User;
import ru.obninsk.net_safety_app.repository.TokenRepository;
import ru.obninsk.net_safety_app.repository.UserRepository;
import ru.obninsk.net_safety_app.service.TokenService;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class NetSafetyAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetSafetyAppApplication.class, args);
	}

}
