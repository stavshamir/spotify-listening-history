package com.stavshamir.app.history

import com.stavshamir.app.authorization.AuthTokensService
import com.stavshamir.app.spotify.SpotifyClient
import com.stavshamir.app.track.TrackDataService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Timestamp
import java.util.*

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [ListeningHistoryServiceImpl::class])
@MockBeans(value = [
    MockBean(ListeningHistoryRepository::class),
    MockBean(TrackDataService::class)
])
class ListeningHistoryServiceImplTest {

    private val MOCK_USER_ID = "mock_user_id"

    @Autowired
    private lateinit var listeningHistoryService: ListeningHistoryService

    @MockBean
    private lateinit var authTokensService: AuthTokensService

    @MockBean
    private lateinit var spotifyClient: SpotifyClient

    @MockBean
    private lateinit var mostRecentlyPlayedAtRepository: MostRecentlyPlayedAtRepository


    @Test
    fun persistListeningHistoryForUser_existingUser() {
        val accessToken = "access-token"
        `when`(authTokensService.getAccessToken(MOCK_USER_ID))
                .thenReturn(accessToken)

        val mostRecentPlayedAt = Timestamp(1000)
        `when`(mostRecentlyPlayedAtRepository.findByUserId(MOCK_USER_ID))
                .thenReturn(Optional.of(MostRecentlyPlayedAt(MOCK_USER_ID, mostRecentPlayedAt)))

        `when`(spotifyClient.getListeningHistory(accessToken, mostRecentPlayedAt))
                .thenReturn(emptyArray())

        listeningHistoryService.persistListeningHistoryForUser(MOCK_USER_ID)

        verify(spotifyClient).getListeningHistory(accessToken, mostRecentPlayedAt)
    }

    @Test
    fun persistListeningHistoryForUser_newUser() {
        val accessToken = "access-token"
        `when`(authTokensService.getAccessToken(MOCK_USER_ID))
                .thenReturn(accessToken)

        `when`(mostRecentlyPlayedAtRepository.findByUserId(MOCK_USER_ID))
                .thenReturn(Optional.empty())

        `when`(spotifyClient.getListeningHistory(eq(accessToken), any()))
                .thenReturn(emptyArray())

        listeningHistoryService.persistListeningHistoryForUser(MOCK_USER_ID)

        verify(spotifyClient).getListeningHistory(accessToken, Timestamp(0))
    }

}