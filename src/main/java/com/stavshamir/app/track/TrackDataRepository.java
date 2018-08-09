package com.stavshamir.app.track;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackDataRepository extends JpaRepository<TrackData, String> {
    Optional<TrackData> findById(String id);
}
