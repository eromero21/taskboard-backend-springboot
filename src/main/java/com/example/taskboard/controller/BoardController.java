package com.example.taskboard.controller;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.ColumnType;
import com.example.taskboard.model.User;
import com.example.taskboard.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/boards")
@CrossOrigin(origins = "http://localhost:5173")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * @api {get} /boards Get the list of boards
     * @apiName GetBoards
     * @apiGroup Boards
     *
     * @apiSuccess (200 OK) {Object} List of boards returned
     */
    @GetMapping
    public ResponseEntity<List<BoardIdSummary>> getBoards(Authentication authentication) {
        User user = requireUser(authentication);

        return ResponseEntity.ok(
                boardService.getAllBoards(user.getId()).stream()
                        .map(b -> new BoardIdSummary(b.getId(), b.getName()))
                        .toList());
    }

    /**
     * @api {post} /boards Create a new board
     * @apiName CreateBoard
     * @apiGroup Boards
     *
     * @apiSuccess (201 CREATED) {Object} New Board returned
     */
    @PostMapping
    public ResponseEntity<Board> createBoard(Authentication authentication, @RequestBody CreateBoardRequest req) {
        User user = requireUser(authentication);
        Board saved = boardService.createBoard(user.getId(), req.name());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    /**
     * @api {get} /boards/{boardId} Get board by id
     * @apiName GetBoardById
     * @apiGroup Boards
     *
     * @apiSuccess (200 OK) {Object} Appropriate board returned
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getBoardById(Authentication authentication, @PathVariable Long boardId) {
        User user = requireUser(authentication);
        return ResponseEntity.ok(boardService.getBoard(user.getId(), boardId));
    }

    /**
     * @api {delete} /boards/{boardId} Delete board by id
     * @apiName DeleteBoard
     * @apiGroup Boards
     *
     * @apiSuccess (204 No Content) Board deleted
     * @apiError (404 Not Found) Board doesn't exist
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(Authentication authentication, @PathVariable Long boardId) {
        User user = requireUser(authentication);
        boardService.deleteBoard(user.getId(), boardId);

        return ResponseEntity.noContent().build();
    }

    /**
     * @api {post} /boards/{boardId}/cards Create new card
     * @apiName CreateCard
     * @apiGroup Cards
     *
     * @apiSuccess (201 Created) {Object} Newly created card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PostMapping("/{boardId}/cards")
    public ResponseEntity<Card> createCard(Authentication authentication,
                                           @PathVariable Long boardId,
                                           @RequestBody CreateCardRequest req) {
        User user = requireUser(authentication);
        Card newCard = boardService.createCard(user.getId(), boardId, req.title(), req.description());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newCard.getId())
                .toUri();

        return ResponseEntity.created(location).body(newCard);
    }

    /**
     * @api {patch} /boards/{boardId}/cards/{cardId}/edit Change existing card
     * @apiName EditCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly edited card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PatchMapping("/{boardId}/cards/{cardId}/edit")
    public ResponseEntity<Card> editCard(@PathVariable Long boardId,
                                         @PathVariable Long cardId,
                                         Authentication authentication,
                                         @RequestBody Map<String, String> cardInfo) {
        User user = requireUser(authentication);
        if (!cardInfo.containsKey("title") || !cardInfo.containsKey("description")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Card newCard = new Card();
        newCard.setTitle(cardInfo.get("title"));
        newCard.setDescription(cardInfo.get("description"));

        Card successCard = boardService.editCard(user.getId(), boardId, cardId, newCard);

        return ResponseEntity.ok(successCard);
    }

    /**
     * @api {patch} /boards/{boardId}/cards/{cardId}/move Change column on existing card
     * @apiName MoveCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly moved card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PatchMapping("/{boardId}/cards/{cardId}/move")
     public ResponseEntity<Card> moveCard(Authentication authentication,
                                          @PathVariable Long boardId,
                                          @PathVariable Long cardId,
                                          @RequestBody MoveCardRequest req) {
        User user = requireUser(authentication);

        Card successCard = boardService.moveCard(user.getId(), boardId, cardId, req.columnId());

        return new ResponseEntity<>(successCard, HttpStatus.OK);
    }

    /**
     * @api {delete} /boards/{boardId}/cards/{cardId}/delete Delete existing card
     * @apiName DeleteCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly deleted card returned
     * @apiError (400 Bad Request) Card doesn't exist
     */
    @DeleteMapping("/{boardId}/cards/{cardId}/delete")
    public ResponseEntity<Void> deleteCard(Authentication authentication,
                                           @PathVariable Long boardId,
                                           @PathVariable Long cardId) {
        User user = requireUser(authentication);
        boardService.deleteCard(user.getId(), boardId, cardId);

        return ResponseEntity.noContent().build();
    }

    private User requireUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return user;
    }

    public record CreateBoardRequest(String name) {}
    public record CreateCardRequest(String title, String description) {}
    public record MoveCardRequest(ColumnType columnId) {}
    public record BoardIdSummary(Long id, String name) {}
}
