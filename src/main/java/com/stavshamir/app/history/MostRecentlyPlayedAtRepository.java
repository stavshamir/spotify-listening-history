package com.stavshamir.app.history;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MostRecentlyPlayedAtRepository extends CrudRepository<MostRecentlyPlayedAt, String> {
    Optional<MostRecentlyPlayedAt> findByUserId(String userId);
}
