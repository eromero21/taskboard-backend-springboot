package com.example.taskboard.controller;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.ColumnType;
import com.example.taskboard.repository.BoardRepository;
import com.example.taskboard.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * @api {get} /api/boards Get the list of boards
     * @apiName GetBoards
     * @apiGroup Boards
     *
     * @apiSuccess (200 OK) {Object} List of boards returned
     */
    @GetMapping("/boards")
    public ResponseEntity<List<BoardIdSummary>> getBoards() {
        return ResponseEntity.ok(
                boardService.getAllBoards().stream()
                        .map(b -> new BoardIdSummary(b.getId(), b.getName()))
                        .toList());
    }

    /**
     * @api {post} /api/boards Create a new board
     * @apiName CreateBoard
     * @apiGroup Boards
     *
     * @apiSuccess (201 CREATED) {Object} New Board returned
     */
    @PostMapping("/boards")
    public ResponseEntity<Board> createBoard(@RequestBody CreateBoardRequest req) {
        Board saved = boardService.createBoard(req.name());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    /**
     * @api {get} /api/boards/{boardId} Get board by id
     * @apiName GetBoardById
     * @apiGroup Boards
     *
     * @apiSuccess (200 OK) {Object} Appropriate board returned
     */
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    /**
     * @api {post} /api/boards/{boardId}/cards Create new card
     * @apiName CreateCard
     * @apiGroup Cards
     *
     * @apiSuccess (201 Created) {Object} Newly created card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PostMapping("/boards/{boardId}/cards")
    public ResponseEntity<Card> createCard(@PathVariable Long boardId, @RequestBody CreateCardRequest req) {
        Card newCard = boardService.createCard(boardId, req.title(), req.description());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newCard.getId())
                .toUri();

        return ResponseEntity.created(location).body(newCard);
    }

    /**
     * @api {patch} /api/boards/{boardId}/cards/{cardId}/edit Change existing card
     * @apiName EditCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly edited card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PatchMapping("/boards/{boardId}/cards/{cardId}/edit")
    public ResponseEntity<Card> editCard(@PathVariable Long boardId,
                                         @PathVariable Long cardId,
                                         @RequestBody Map<String, String> cardInfo) {
        if (!cardInfo.containsKey("title") || !cardInfo.containsKey("description")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Card newCard = new Card();
        newCard.setTitle(cardInfo.get("title"));
        newCard.setDescription(cardInfo.get("description"));

        Card successCard = boardService.editCard(boardId, cardId, newCard);

        return ResponseEntity.ok(successCard);
    }

    /**
     * @api {patch} /api/boards/{boardId}/cards/{cardId}/move Change column on existing card
     * @apiName MoveCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly moved card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PatchMapping("/boards/{boardId}/cards/{cardId}/move")
     public ResponseEntity<Card> moveCard(@PathVariable Long boardId,
                                          @PathVariable Long cardId,
                                          @RequestBody MoveCardRequest req) {

        Card successCard = boardService.moveCard(boardId, cardId, req.columnId());

        return new ResponseEntity<>(successCard, HttpStatus.OK);
    }

    /**
     * @api {delete} /api/boards/{boardId}/cards/{cardId}/delete Delete existing card
     * @apiName DeleteCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly deleted card returned
     * @apiError (400 Bad Request) Card doesn't exist
     */
    @DeleteMapping("/boards/{boardId}/cards/{cardId}/delete")
    public ResponseEntity<Void> deleteCard(@PathVariable Long boardId, @PathVariable Long cardId) {
        boardService.deleteCard(boardId, cardId);

        return ResponseEntity.noContent().build();
    }

    public record CreateBoardRequest(String name) {}
    public record CreateCardRequest(String title, String description) {}
    public record MoveCardRequest(ColumnType columnId) {}
    public record BoardIdSummary(Long id, String name) {}
}