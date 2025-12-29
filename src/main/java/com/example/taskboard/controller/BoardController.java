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
    private final BoardRepository boardRepository;

    public BoardController(BoardService boardService, BoardRepository boardRepository) {
        this.boardService = boardService;
        this.boardRepository = boardRepository;
    }

    @GetMapping("/board")
    public Board defaultBoard() {
        Long defaultBoardId = 1L;
        return boardService.getBoard(defaultBoardId);
    }

    /**
     * @api {get} /api/boards Get the list of boards
     * @apiName GetBoards
     * @apiGroup Boards
     *
     * @apiSuccess (200 OK) {Object} List of boards returned
     */
    @GetMapping("/boards")
    public ResponseEntity<List<Board>> getBoards() {
        List<Board> boards = boardRepository.findAll();
        return ResponseEntity.ok(boards);
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
        Board saved = boardRepository.save(new Board(req.name));

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
        Optional<Board> board = boardRepository.findById(boardId);

        return board.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * @api {post} /api/cards Create new card
     * @apiName CreateCard
     * @apiGroup Cards
     *
     * @apiSuccess (201 Created) {Object} Newly created card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PostMapping("/boards/{boardId}/cards")
    public ResponseEntity<Card> createCard(@PathVariable Long boardId, @RequestBody CreateCardRequest req) {
        if (req.title == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Card newCard = boardService.createCard(boardId, req.title, req.description);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newCard.getId())
                .toUri();

        return ResponseEntity.created(location).body(newCard);
    }

    /**
     * @api {patch} /api/cards/{cardId}/edit Change existing card
     * @apiName EditCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly edited card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PatchMapping("boards/{boardId}/cards/{cardId}/edit")
    public ResponseEntity<Card> editCard(@PathVariable Long boardId,
                                         @PathVariable String cardId,
                                         @RequestBody Map<String, String> cardInfo) {
        if (!cardInfo.containsKey("title") || !cardInfo.containsKey("description")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Card newCard = new Card();
        newCard.setTitle(cardInfo.get("title"));
        newCard.setDescription(cardInfo.get("description"));

        Card successCard = boardService.editCard(cardId, newCard);

        return ResponseEntity.ok(successCard);
    }

    /**
     * @api {patch} /api/cards/{cardId}/move Change column on existing card
     * @apiName MoveCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly moved card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PatchMapping("/boards/{boardId}/cards/{cardId}/move")
     public ResponseEntity<Card> moveCard(@PathVariable Long boardId,
                                          @PathVariable String cardId,
                                          @RequestParam ColumnType colType) {

        Card successCard = boardService.moveCard(boardId, cardId, colType);

        return new ResponseEntity<>(successCard, HttpStatus.OK);
    }

    /**
     * @api {delete} /api/cards/{cardId}/delete Delete existing card
     * @apiName DeleteCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly deleted card returned
     * @apiError (400 Bad Request) Card doesn't exist
     */
    @DeleteMapping("/cards/{cardId}/delete")
    public ResponseEntity<Card> deleteCard(@PathVariable String cardId) {
        if (!boardService.hasCard(cardId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Card successCard = boardService.deleteCard(cardId);

        return new ResponseEntity<>(successCard, HttpStatus.OK);
    }

    public record CreateBoardRequest(String name) {}
    public record CreateCardRequest(String title, String description) {}
}