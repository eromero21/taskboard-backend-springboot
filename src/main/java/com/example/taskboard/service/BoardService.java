package com.example.taskboard.service;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.ColumnEntity;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.ColumnType;
import com.example.taskboard.model.User;
import com.example.taskboard.repository.BoardRepository;
import com.example.taskboard.repository.CardRepository;
import com.example.taskboard.repository.ColumnRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class BoardService {
    private final String[] columnNames = {"Backlog", "Todo", "In Progress", "Completed"};
    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final UserService userService;

    public BoardService(CardRepository cardRepository,
                        BoardRepository boardRepository,
                        ColumnRepository columnRepository,
                        UserService userService) {
        this.cardRepository = cardRepository;
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.userService = userService;
    }

    public Board createBoard(Long ownerId, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Board name cannot be empty");
        }

        User owner = userService.getUserById(ownerId);
        Board board = new Board();
        board.setName(name);
        board.setOwner(owner);

        for (String columnName : columnNames) {
            ColumnType type = ColumnType.valueOf(columnName.toUpperCase().replace(" ", "_"));
            ColumnEntity column = new ColumnEntity(columnName, type);
            board.addColumn(column);
        }

        return boardRepository.save(board);
    }

    public Board getBoard(Long ownerId, Long boardId) {
        return boardRepository.findByIdWithColumnsAndCards(boardId, ownerId).orElseThrow(() ->
                new IllegalArgumentException("Board not found"));
    }

    public List<Board> getAllBoards(Long ownerId) {
        return boardRepository.findAllByOwnerId(ownerId);
    }

    public List<Card> getCards(Long ownerId) {
        return cardRepository.findByBoardOwnerId(ownerId);
    }

    public Card createCard(Long ownerId, Long boardId, String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        Board board = boardRepository.findByIdAndOwnerId(boardId, ownerId).orElseThrow(() ->
                new IllegalArgumentException("Board not found"));

        ColumnEntity backlog = columnRepository.findByBoardIdAndBoardOwnerIdAndType(boardId, ownerId, ColumnType.BACKLOG).orElseThrow(
                () -> new IllegalArgumentException("Backlog column not found"));

        Card newCard = new Card(board, backlog, title, description);

        backlog.getCards().add(newCard);

        return cardRepository.save(newCard);
    }

    public Card moveCard(Long ownerId, Long boardId, Long cardId, ColumnType columnId) {
        if (columnId == null) {
            throw new IllegalArgumentException("Invalid column type.");
        }

        Card theCard = cardRepository.findByIdAndBoardIdAndBoardOwnerId(cardId, boardId, ownerId).orElseThrow(() ->
                new IllegalArgumentException("Card ID doesn't exist.."));

        ColumnEntity targetColumn = columnRepository.findByBoardIdAndBoardOwnerIdAndType(boardId, ownerId, columnId).orElseThrow(
                () -> new IllegalArgumentException("Column not belong to this board.")
        );

        theCard.setColumn(targetColumn);
        return cardRepository.save(theCard);
    }

    public void deleteCard(Long ownerId, Long boardId, Long cardId) {
        Card theCard = cardRepository.findByIdAndBoardIdAndBoardOwnerId(cardId, boardId, ownerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        cardRepository.delete(theCard);
    }

    public Card editCard(Long ownerId, Long boardId, Long cardId, Card newCard) {
        Card theCard = cardRepository.findByIdAndBoardIdAndBoardOwnerId(cardId, boardId, ownerId).orElseThrow(() ->
                new IllegalArgumentException("Card ID doesn't exist.."));

        theCard.setTitle(newCard.getTitle());
        theCard.setDescription(newCard.getDescription());

        return cardRepository.save(theCard);
    }

    public boolean hasCard(Long ownerId, Long boardId, Long cardId) {
        return cardRepository.findByIdAndBoardIdAndBoardOwnerId(cardId, boardId, ownerId).isPresent();
    }
}
