package com.example.taskboard.repository;

import com.example.taskboard.model.ColumnEntity;
import com.example.taskboard.model.ColumnType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository extends JpaRepository<ColumnEntity, Long> {
    List<ColumnEntity> findByBoardId(Long boardId);

    Optional<ColumnEntity> findByBoardIdAndType(Long boardId, ColumnType type);
}
