package com.example.taskboard.controller;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.ColumnType;
import com.example.taskboard.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class BoardController {
    BoardService boardService = new BoardService();

    /**
     * @api {get} /api/board Get the board (currently only 1)
     * @apiName GetBoard
     * @apiGroup Boards
     *
     * @apiSuccess (200 OK) {Object} Single board return
     * @apiError (404 Not Found) No board to return
     */
    @GetMapping("/board")
    public ResponseEntity<Board> getBoard() {
        Board board = boardService.getBoard();

        if (board == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    /**
     * @api {get} /api/cards Get list of current cards
     * @apiName GetCards
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object[]} List of cards returned
     * @apiError (204 No Content) No cards available
     */
    @GetMapping("/cards")
    public ResponseEntity<List<Card>> getCards() {
        List<Card> cards = boardService.getCards();

        if (cards == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    /**
     * @api {post} /api/cards Create new card
     * @apiName CreateCard
     * @apiGroup Cards
     *
     * @apiSuccess (201 Created) {Object} Newly created card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PostMapping("/cards")
    public ResponseEntity<Card> createCard(@RequestBody Map<String, String> cardInfo) {
        String newTitle = cardInfo.get("title");
        String newDescription = cardInfo.get("description");

        if (newTitle == null || newDescription == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Card newCard = boardService.createCard(newTitle, newDescription);
        return new ResponseEntity<>(newCard, HttpStatus.CREATED);
    }

    /**
     * @api {patch} /api/cards/{cardId}/edit Change existing card
     * @apiName EditCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly edited card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PatchMapping("/cards/{cardId}/edit")
    public ResponseEntity<Card> editCard(@PathVariable String cardId, @RequestBody Map<String, String> cardInfo) {
        if (!cardInfo.containsKey("title") || cardInfo.containsKey("description") || cardInfo.containsKey("columnId")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Card newCard = new Card();
        newCard.setTitle(cardInfo.get("title"));
        newCard.setDescription(cardInfo.get("description"));
        newCard.setColumnId(ColumnType.valueOf(cardInfo.get("columnId")));

        Card successCard = boardService.editCard(cardId, newCard);

        return new ResponseEntity<>(successCard, HttpStatus.OK);
    }

    /**
     * @api {patch} /api/cards/{cardId}/move Change column on existing card
     * @apiName MoveCard
     * @apiGroup Cards
     *
     * @apiSuccess (200 OK) {Object} Newly moved card returned
     * @apiError (400 Bad Request) Insufficient information
     */
    @PatchMapping("/cards/{cardId}/move")
     public ResponseEntity<Card> moveCard(@PathVariable String cardId, @RequestBody Map<String, String> cardInfo) {
        if (!cardInfo.containsKey("columnId")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Card successCard = boardService.moveCard(cardId, ColumnType.valueOf(cardInfo.get("columnId")));

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
}