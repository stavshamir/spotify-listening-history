package com.stavshamir.app.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.sql.Timestamp;
import java.util.List;

public interface ListeningHistoryRepository extends PagingAndSortingRepository<ListeningHistory, Long> {

    Page<ListeningHistory> findAllByUserIdAndPlayedAtGreaterThanEqualAndPlayedAtBeforeOrderByPlayedAtDesc(String userId, Timestamp after, Timestamp before, Pageable pageable);

    @Query(
            value = "SELECT uri, COUNT(uri)" +
                    " FROM listening_history" +
                    " WHERE user_id = :user_id AND played_at >= :after AND played_at < :before" +
                    " GROUP BY uri" +
                    " ORDER BY COUNT(uri) DESC" +
                    " LIMIT :limit",
            nativeQuery = true
    )
    List<Tuple> findMostPlayed(
            @Param("user_id") String userId,
            @Param("after") Timestamp after,
            @Param("before") Timestamp before,
            @Param("limit") int limit
    );

}
