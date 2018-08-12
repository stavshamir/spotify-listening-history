package com.stavshamir.app.history;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ListeningHistoryRepository extends CrudRepository<ListeningHistory, Long> {
    List<ListeningHistory> findAllByUserId(String userId);
}
