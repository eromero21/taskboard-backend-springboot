package com.example.taskboard.controller;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.User;
import com.example.taskboard.service.BoardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

    @Mock
    private BoardService boardService;

    @InjectMocks
    private BoardController boardController;

    @Test
    void getBoards_usesAuthenticatedUserId() {
        User user = new User("user@example.test", "hashedPassword");
        ReflectionTestUtils.setField(user, "id", 17L);

        Board board = new Board("Project Board");
        ReflectionTestUtils.setField(board, "id", 42L);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of());

        when(boardService.getAllBoards(17L)).thenReturn(List.of(board));

        ResponseEntity<List<BoardController.BoardIdSummary>> response = boardController.getBoards(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(42L, response.getBody().getFirst().id());
        assertEquals("Project Board", response.getBody().getFirst().name());
        verify(boardService).getAllBoards(17L);
    }

    @Test
    void createCard_usesAuthenticatedUserId() {
        User user = new User("user@example.test", "hashedPassword");
        ReflectionTestUtils.setField(user, "id", 17L);

        Board board = new Board("Project Board");
        ReflectionTestUtils.setField(board, "id", 42L);

        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", 99L);
        card.setBoard(board);
        card.setTitle("Task");
        card.setDescription("Do the work");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of());

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/boards/42/cards");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(boardService.createCard(17L, 42L, "Task", "Do the work")).thenReturn(card);

        ResponseEntity<Card> response = boardController.createCard(
                authentication,
                42L,
                new BoardController.CreateCardRequest("Task", "Do the work"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(99L, response.getBody().getId());
        verify(boardService).createCard(17L, 42L, "Task", "Do the work");
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void deleteBoard_usesAuthenticatedUserId() {
        User user = new User("user@example.test", "hashedPassword");
        ReflectionTestUtils.setField(user, "id", 17L);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of());

        doNothing().when(boardService).deleteBoard(17L, 42L);

        ResponseEntity<Void> response = boardController.deleteBoard(authentication, 42L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(boardService).deleteBoard(17L, 42L);
    }

    @Test
    void getBoards_rejectsMissingUserPrincipal() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("not-a-user", null, List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                boardController.getBoards(authentication));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
}
