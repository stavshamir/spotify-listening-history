package com.stavshamir.app.history;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ListeningHistoryRepositoryTest {

    private final String TESTER_USER_ID = "ID";

    @Autowired
    private ListeningHistoryRepository repository;

    @Test
    @Transactional
    public void findMostPlayed_default_parameters() {
        // Given a persisted list of tracks
        repository.saveAll(Lists.newArrayList(
                new ListeningHistory(TESTER_USER_ID, "1", new Timestamp(1000)),
                new ListeningHistory(TESTER_USER_ID, "1", new Timestamp(1500)),
                new ListeningHistory(TESTER_USER_ID, "2", new Timestamp(2000)),
                new ListeningHistory(TESTER_USER_ID, "3", new Timestamp(3000))
        ));

        // When querying most played tracks with default params
        List<Object[]> mostPlayed = repository.find50MostPlayed(
                TESTER_USER_ID,
                0, 2030,
                1, 13,
                0, 25
        );

        // Then all tracks are present in the result with the correct count
        List<IdWithCount> items = mostPlayed.stream()
                .map(o -> new IdWithCount((String) o[0], ((BigInteger) o[1]).intValue()))
                .collect(toList());

        assertThat(items)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        new IdWithCount("1", 2),
                        new IdWithCount("2", 1),
                        new IdWithCount("3", 1)
                );
    }

    private class IdWithCount {
        String id;
        int count;

        IdWithCount(String id, int count) {
            this.id = id;
            this.count = count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IdWithCount that = (IdWithCount) o;
            return count == that.count &&
                    Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, count);
        }
    }

}