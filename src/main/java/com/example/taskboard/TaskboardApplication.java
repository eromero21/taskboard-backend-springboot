package com.example.taskboard;

import com.example.taskboard.repository.BoardRepository;
import com.example.taskboard.repository.UserRepository;
import com.example.taskboard.service.BoardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskboardApplication.class, args);
	}

	@Bean
	@ConditionalOnProperty(name="app.seed-default-board", havingValue="true", matchIfMissing=true)
	CommandLineRunner seedDefaultBoard(BoardService boardService,
									  BoardRepository boardRepository,
									  UserRepository userRepository) {
		return args -> {
			if (boardRepository.count() == 0) {
				userRepository.findFirstByOrderByIdAsc()
						.ifPresent(user -> boardService.createBoard(user.getId(), "My first board!"));
			}
		};
	}
}
