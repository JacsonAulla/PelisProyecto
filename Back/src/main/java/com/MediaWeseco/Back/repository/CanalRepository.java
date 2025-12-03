package com.MediaWeseco.Back.repository;

import com.MediaWeseco.Back.models.Canal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CanalRepository extends JpaRepository<Canal, Long> {
    Optional<Canal> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = { "generos" })
    Page<Canal> findByEstaActivoTrue(Pageable pageable);
}