package com.example.taskboard;

import com.example.taskboard.model.Board;
import com.example.taskboard.repository.BoardRepository;
import com.example.taskboard.service.BoardService;
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
	CommandLineRunner seedDefaultBoard(BoardService boardService, BoardRepository boardRepository) {
		return args -> {
			if (boardRepository.count() == 0) {
				boardService.createBoard("My first board!");
			}
		};
	}
}
