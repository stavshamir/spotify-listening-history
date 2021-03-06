package com.stavshamir.app.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface ListeningHistoryRepository extends PagingAndSortingRepository<ListeningHistory, Long> {

    Page<ListeningHistory> findAllByUserIdAndPlayedAtGreaterThanEqualAndPlayedAtBeforeOrderByPlayedAtDesc(String userId, Timestamp after, Timestamp before, Pageable pageable);

    @Query(
            value = "SELECT uri, COUNT(uri) FROM listening_history" +
                    " WHERE user_id = :user_id" +
                    " AND EXTRACT(YEAR from played_at) >= :from_year AND EXTRACT(YEAR from played_at) < :to_year" +
                    " AND EXTRACT(MONTH from played_at) >= :from_month AND EXTRACT(MONTH from played_at) < :to_month" +
                    " AND EXTRACT(HOUR from played_at) >= :from_hour AND EXTRACT(HOUR from played_at) < :to_hour" +
                    " GROUP BY uri" +
                    " ORDER BY COUNT(uri) DESC" +
                    " LIMIT 50",
            nativeQuery = true
    )
    List<Object[]> find50MostPlayed(
            @Param("user_id") String userId,
            @Param("from_year") int fromYear, @Param("to_year") int toYear,
            @Param("from_month") int fromMonth, @Param("to_month") int toMonth,
            @Param("from_hour") int fromHour, @Param("to_hour") int toHour
    );

}
