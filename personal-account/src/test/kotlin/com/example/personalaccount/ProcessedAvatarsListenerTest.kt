package com.example.personalaccount

import com.example.common.dto.Avatar
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.ProcessedAvatarsListener
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class ProcessedAvatarsListenerTest {

    private val accountsService: AccountService = mock(AccountService::class.java)
    private val processedAvatarsListener = ProcessedAvatarsListener(accountsService)

    @Test
    fun `test listen updates account avatar`() {
        val avatar = Avatar(accountId = 1L, "new_avatar_url")

        processedAvatarsListener.listen(avatar)

        verify(accountsService, times(1)).updateAccountAvatar(avatar)
    }
}