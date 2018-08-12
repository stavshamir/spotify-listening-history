package com.stavshamir.app.history;

import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface ListeningHistoryRepository extends CrudRepository<ListeningHistory, Long> {
    List<ListeningHistory> findAllByUserIdAndPlayedAtAfterOrderByPlayedAtDesc(String userId, Timestamp after);
}
