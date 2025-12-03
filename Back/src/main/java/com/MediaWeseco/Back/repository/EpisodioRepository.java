package com.MediaWeseco.Back.repository;

import com.MediaWeseco.Back.models.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodioRepository extends JpaRepository<Episodio, Long> {
}