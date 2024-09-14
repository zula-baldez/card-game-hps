import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager


@Configuration
@EnableWebSocketSecurity
class SocketSecurityConfig {
    @Bean
    fun authorizationManager(messages: MessageMatcherDelegatingAuthorizationManager.Builder): AuthorizationManager<Message<*>?>? {
        messages.simpDestMatchers("/admin").hasRole("ADMIN")
             .anyMessage().authenticated()
        return messages.build()
    }

}
