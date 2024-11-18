// package com.example.gamehandlerservice.security
//
// import jakarta.inject.Inject
// import org.springframework.messaging.Message
// import org.springframework.messaging.MessageChannel
// import org.springframework.messaging.simp.stomp.StompCommand
// import org.springframework.messaging.simp.stomp.StompHeaderAccessor
// import org.springframework.messaging.support.ChannelInterceptor
// import org.springframework.messaging.support.MessageHeaderAccessor
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
// import org.springframework.stereotype.Component
//
// @Component
// class AuthChannelInterceptorAdapter @Inject constructor(webSocketAuthenticatorService: WebSocketAuthenticatorService) :
//     ChannelInterceptor {
//     private val webSocketAuthenticatorService: WebSocketAuthenticatorService = webSocketAuthenticatorService
//
//     @Throws(AuthenticationException::class)
//     fun preSend(message: Message<*>, channel: MessageChannel?): Message<*> {
//         val accessor: StompHeaderAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
//
//         if (StompCommand.CONNECT == accessor.command) {
//             val username = accessor.getFirstNativeHeader(USERNAME_HEADER)
//             val password = accessor.getFirstNativeHeader(PASSWORD_HEADER)
//
//             val user: UsernamePasswordAuthenticationToken =
//                 webSocketAuthenticatorService.getAuthenticatedOrFail(username, password)
//
//             accessor.user = user
//         }
//         return message
//     }
//
//     companion object {
//         private const val USERNAME_HEADER = "login"
//         private const val PASSWORD_HEADER = "passcode"
//     }
// }
