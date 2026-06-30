package com.authenticator.Follow.repository;

import com.authenticator.Follow.model.Block;
import com.authenticator.Follow.model.BlockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, BlockId> {
    boolean existsByBlockId_BlockerIdAndBlockId_BlockedId(String blockerId, String blockedId);
    List<Block> findByBlockId_BlockerId(String blockerId);
    List<Block> findByBlockId_BlockedId(String blockedId);
    Optional<Block> findByBlockId_BlockerIdAndBlockId_BlockedId(String blockerId, String blockedId);
    long countByBlockId_BlockerId(String blockerId);
}

