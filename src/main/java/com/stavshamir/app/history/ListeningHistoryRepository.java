package com.stavshamir.app.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Timestamp;

public interface ListeningHistoryRepository extends PagingAndSortingRepository<ListeningHistory, Long> {
    Page<ListeningHistory> findAllByUserIdAndPlayedAtAfterOrderByPlayedAtDesc(String userId, Timestamp after, Pageable pageable);
}
