package com.example.gameserviceintegration

import com.example.StompIntegrationTestBase
import com.example.common.client.PersonalAccountClient
import com.example.common.client.RoomServiceClient
import com.example.common.dto.personalaccout.AccountDto
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.gamehandlerservice.service.game.util.VirtualPlayers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.stomp.StompSession

class FineStrategyTest : StompIntegrationTestBase() {
    private var userSessions: MutableMap<Long, StompSession> = mutableMapOf()
    private var hostId = 6L

    //id 6 t1 t1
    //eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI2Iiwic2VydmljZSI6InVzZXItdG9rZW4iLCJzY29wZSI6IlVTRVIiLCJpc3MiOiJzZWxmIiwibmFtZSI6InQxIiwiZXhwIjoxNzM4Nzk1OTc4LCJpYXQiOjE3MzAxNTU5Nzh9.FoR2FdXcBzVIl6zpk5_pb_igZXdKDk-watTbVG42Lqji1zBUpGyvFRC3sDjjlVYipLs2PvfL1Kt4gJz-UuxMECI7K42TNSi96kd_S4GEEs_7BK-quCsRgPmvHU-LgQsGPRrghZb7uYxKMfETidE9tRht67aoeHMnJCPdH-1YZ7UcjuO6EiX7asnYdtwTiUs98HXfLtIyMEG-dL3VekDW3Hwo3o5hIeuIvcvU2lRnFharWIkEmP4MqnN4gSVSt53Ft_6gJ1z2JrlK_OrsFupA9VG3uXi6ffHis7zX2VCis9EHUNAkrYIPtS6MUUmyBTo2shGgt7BxqNUVYb8hF-SuUA
    //id 8 t2 t2
    // eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI4Iiwic2VydmljZSI6InVzZXItdG9rZW4iLCJzY29wZSI6IlVTRVIiLCJpc3MiOiJzZWxmIiwibmFtZSI6InQyIiwiZXhwIjoxNzM4Nzk2MTE2LCJpYXQiOjE3MzAxNTYxMTZ9.fqVbKKuCZ_KQVw521chLghk-GjEFGfaWbLQ8kAF7dxfP_syKHvnVJ3JVX1ctocsl7irN8AnPCSCZ1mo9Bywg7qrIXfBQ25fbVVW7V8QLHjrPSV8CAOw30vQLg_lXYLXOPYz5DAXBZD_nmMXxDIoqKpLPywgFE3OeQwBexZcPCXd1zAx_OSesO9KtDB8o4dka6YJhzS3am7PpZhTLWHsbr0qpPZQ37HiBwO5vQCEctsY54WuSpjKPncfn3Hz2Vbr6V-obSUlPpqSldPKLTrhKOXdSwdD8ssrxqgHnoHcEVJ5WUZOlq2WdT2XwjMvmLe9bnNG6TQahTZWFHB0kWoTcZQ
    // id 7 t3 t3
    // eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI3Iiwic2VydmljZSI6InVzZXItdG9rZW4iLCJzY29wZSI6IlVTRVIiLCJpc3MiOiJzZWxmIiwibmFtZSI6InQzIiwiZXhwIjoxNzM4Nzk2MTM0LCJpYXQiOjE3MzAxNTYxMzR9.O7IBMuk5JrvULgAqFyW-iFz9l_qcsAiKQrWwU1LErZVHIyNQi8QQNR-3AbC9Qw9GXkdQ7QelHzzE-yb9Jhk-sdblzjY_liXgqueKPx2wOGmPEwwK7a2a2PJkpIBG9smoHRE-JvNSBcEWKH4LLiGcAWEdsnHjg6Uk96GM-Xq4UU7kf872d5jhuLVsjU2VVvJ8NUf0YFKOGPIWdY47dC_49XRopx6HP4QqB9e4RsJs8IqT1aCure9ZlfsQjMjx8VhJchu7INagf-s4_hgHjfhEp6YkZVstzvCQv3IwdPs0ZDasg6E8jKllMWhukwv3gxPNZwwK7aK3_mA2vQdI3Xbf3w
    private var gameId = 1L
    private var roomId = 1L

    private val game: GameHandler
        get() = gameHandlerRegistry.getGame(gameId) ?: throw IllegalArgumentException("No game found")

    @Autowired
    private lateinit var gameHandlerRegistry: GameHandlerRegistry

    @Autowired
    private lateinit var personalAccountClient: PersonalAccountClient

    @Autowired
    private lateinit var roomServiceClient: RoomServiceClient


    @BeforeEach
    fun initData() {
        val gameHandler = gameHandlerRegistry.createGame("game", 1)

        var session = getClientStompSession(
            1,
            6,
            "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI2Iiwic2VydmljZSI6InVzZXItdG9rZW4iLCJzY29wZSI6IlVTRVIiLCJpc3MiOiJzZWxmIiwibmFtZSI6InQxIiwiZXhwIjoxNzM4Nzk1OTc4LCJpYXQiOjE3MzAxNTU5Nzh9.FoR2FdXcBzVIl6zpk5_pb_igZXdKDk-watTbVG42Lqji1zBUpGyvFRC3sDjjlVYipLs2PvfL1Kt4gJz-UuxMECI7K42TNSi96kd_S4GEEs_7BK-quCsRgPmvHU-LgQsGPRrghZb7uYxKMfETidE9tRht67aoeHMnJCPdH-1YZ7UcjuO6EiX7asnYdtwTiUs98HXfLtIyMEG-dL3VekDW3Hwo3o5hIeuIvcvU2lRnFharWIkEmP4MqnN4gSVSt53Ft_6gJ1z2JrlK_OrsFupA9VG3uXi6ffHis7zX2VCis9EHUNAkrYIPtS6MUUmyBTo2shGgt7BxqNUVYb8hF-SuUA"
        )
        userSessions[hostId] = session

        session = getClientStompSession(
            roomId,
            8,
            "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI4Iiwic2VydmljZSI6InVzZXItdG9rZW4iLCJzY29wZSI6IlVTRVIiLCJpc3MiOiJzZWxmIiwibmFtZSI6InQyIiwiZXhwIjoxNzM4Nzk2MTE2LCJpYXQiOjE3MzAxNTYxMTZ9.fqVbKKuCZ_KQVw521chLghk-GjEFGfaWbLQ8kAF7dxfP_syKHvnVJ3JVX1ctocsl7irN8AnPCSCZ1mo9Bywg7qrIXfBQ25fbVVW7V8QLHjrPSV8CAOw30vQLg_lXYLXOPYz5DAXBZD_nmMXxDIoqKpLPywgFE3OeQwBexZcPCXd1zAx_OSesO9KtDB8o4dka6YJhzS3am7PpZhTLWHsbr0qpPZQ37HiBwO5vQCEctsY54WuSpjKPncfn3Hz2Vbr6V-obSUlPpqSldPKLTrhKOXdSwdD8ssrxqgHnoHcEVJ5WUZOlq2WdT2XwjMvmLe9bnNG6TQahTZWFHB0kWoTcZQ"
        )
        userSessions[8] = session

        session = getClientStompSession(
            roomId,
            7,
            "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI3Iiwic2VydmljZSI6InVzZXItdG9rZW4iLCJzY29wZSI6IlVTRVIiLCJpc3MiOiJzZWxmIiwibmFtZSI6InQzIiwiZXhwIjoxNzM4Nzk2MTM0LCJpYXQiOjE3MzAxNTYxMzR9.O7IBMuk5JrvULgAqFyW-iFz9l_qcsAiKQrWwU1LErZVHIyNQi8QQNR-3AbC9Qw9GXkdQ7QelHzzE-yb9Jhk-sdblzjY_liXgqueKPx2wOGmPEwwK7a2a2PJkpIBG9smoHRE-JvNSBcEWKH4LLiGcAWEdsnHjg6Uk96GM-Xq4UU7kf872d5jhuLVsjU2VVvJ8NUf0YFKOGPIWdY47dC_49XRopx6HP4QqB9e4RsJs8IqT1aCure9ZlfsQjMjx8VhJchu7INagf-s4_hgHjfhEp6YkZVstzvCQv3IwdPs0ZDasg6E8jKllMWhukwv3gxPNZwwK7aK3_mA2vQdI3Xbf3w"
        )
        userSessions[7] = session


        game.stateMachine.stage = Stage.DISTRIBUTION
        game.gameData.playersTurnQueue = CyclicQueue(userSessions.keys.map {
            AccountDto(
                it,
                "name$it",
                0,
                roomId
            )
        })
    }

    private fun getTurningPlayer(): Long? {
        return game.turningPlayer()?.id
    }

    private fun getNotTurningPlayer(): Long {
        val turningPlayer = getTurningPlayer()
        return if (turningPlayer == hostId) hostId + 1
        else hostId
    }

    @AfterEach
    fun clear() {
        userSessions.clear()
    }

    @Test
    fun testCannotDropOnYourself() {
        userSessions[hostId]?.send("/app/move-card", MoveCardRequest(hostId, hostId, Card()))
        val response = getMessage(hostId)
        assertNull(response)
    }

    @Test
    fun noDropOnEnemyWhenNotYourTurn() {
        val notTurningPlayer = getNotTurningPlayer()
        val turningPlayer = getTurningPlayer()!!
        userSessions[notTurningPlayer]?.send(
            "/app/move-card",
            MoveCardRequest(notTurningPlayer, turningPlayer, Card())
        )
        val response = getMessage(notTurningPlayer)
        assertNull(response)
    }

    @Test
    fun dropOnEnemyWithFines() {
        val notTurningPlayer = getNotTurningPlayer()
        val turningPlayer = getTurningPlayer()!!
        var finedAccount = personalAccountClient.getAccountById(notTurningPlayer)
        finedAccount.fines = 1
        userSessions[turningPlayer]?.send(
            "/app/move-card",
            MoveCardRequest(turningPlayer, notTurningPlayer, Card())
        )
        val response = getMessage(turningPlayer)
        assertEquals(Card(), response?.card)
        assertEquals(notTurningPlayer, response?.idTo)
        assertEquals(turningPlayer, response?.idFrom)
        finedAccount =
            personalAccountClient.getAccountById(notTurningPlayer)
        assertEquals(0, finedAccount.fines)
    }

    @Test
    fun dropOnEnemyWithNoFines() {
        val notTurningPlayer = getNotTurningPlayer()
        val turningPlayer = getTurningPlayer()!!
        val finedAccount = personalAccountClient.getAccountById(notTurningPlayer)
        finedAccount.fines = 0
        userSessions[turningPlayer]?.send(
            "/app/move-card",
            MoveCardRequest(turningPlayer, notTurningPlayer, Card())
        )
        val response = getMessage(turningPlayer)
        assertNull(response)
    }

    @Test
    fun fineOnDropTable() {
        val turningPlayer = getTurningPlayer()!!
        var finedAccount = personalAccountClient.getAccountById(turningPlayer)
        finedAccount.fines = 0
        userSessions[turningPlayer]?.send(
            "/app/move-card",
            MoveCardRequest(turningPlayer, VirtualPlayers.TABLE.id, Card())
        )
        val response = getMessage(turningPlayer)
        assertNull(response)

        finedAccount = personalAccountClient.getAccountById(turningPlayer)
        verify(personalAccountClient.addFine(anyLong()))
    }

}