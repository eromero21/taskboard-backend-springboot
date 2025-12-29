package com.example.taskboard;

import com.example.taskboard.model.Board;
import com.example.taskboard.repository.BoardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskboardApplication.class, args);
	}

	@Bean
	CommandLineRunner seedDefaultBoard(BoardRepository boardRepo) {
		return args -> {
			if (boardRepo.count() == 0) {
				Board defaultBoard = new Board("My First Board");
				boardRepo.save(defaultBoard);
			}
		};
	}
}
