package com.example.taskboard.repository;

import com.example.taskboard.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByOwnerId(Long ownerId);

    @Query("""
SELECT distinct b from Board b
left join fetch b.columns c
left join fetch c.cards
where b.id = :boardId
and b.owner.id = :ownerId
""")
    Optional<Board> findByIdWithColumnsAndCards(@Param("boardId") Long boardId,
                                                @Param("ownerId") Long ownerId);

    Optional<Board> findByIdAndOwnerId(Long boardId, Long ownerId);
}
