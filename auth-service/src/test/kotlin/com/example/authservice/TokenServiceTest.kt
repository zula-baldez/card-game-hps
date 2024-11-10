import com.example.authservice.database.RoleEntity
import com.example.authservice.database.UserEntity
import com.example.authservice.jwt.TokenService
import com.example.common.util.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Instant
import java.time.temporal.ChronoUnit

class TokenServiceTest {

    private lateinit var jwtEncoder: JwtEncoder
    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setup() {
        jwtEncoder = mock(JwtEncoder::class.java)
        tokenService = TokenService(jwtEncoder)
    }

    @Test
    fun `generateAccessToken should generate token with correct claims`() {
        val userEntity = UserEntity(
            id = 123,
            name = "John Doe",
            password = "lol",
            roles = mutableSetOf(RoleEntity(id = 1, roleName = Role.USER), RoleEntity(id = 2, roleName = Role.ADMIN))
        )
        val serviceName = "TestService"
        val expectedTokenValue = "encodedJwtToken"

        val mockJwt = mock(org.springframework.security.oauth2.jwt.Jwt::class.java)
        `when`(mockJwt.tokenValue).thenReturn(expectedTokenValue)
        `when`(jwtEncoder.encode(any())).thenReturn(mockJwt)

        val actualToken = tokenService.generateAccessToken(userEntity, serviceName)

        assertEquals(expectedTokenValue, actualToken, "The returned token should match the expected token")

        val captor = ArgumentCaptor.forClass(JwtEncoderParameters::class.java)
        verify(jwtEncoder).encode(captor.capture())
        val capturedParameters = captor.value

        val claims = capturedParameters.claims.claims
        assertEquals("self", claims["iss"], "Issuer should be 'self'")
        assertEquals(userEntity.id.toString(), claims["sub"], "Subject should be user ID")
        assertEquals("John Doe", claims["name"], "Name claim should match user's name")
        assertEquals(serviceName, claims["service"], "Service claim should match provided service name")

        val expectedScope = userEntity.roles.joinToString(" ") { it.roleName.toString()  }
        assertEquals(expectedScope, claims["scope"], "Scope should match user's roles")

        val issuedAt = claims["iat"] as Instant
        val expiresAt = claims["exp"] as Instant
        val expectedExpirationTime = issuedAt.plus(100, ChronoUnit.MINUTES)
        assertEquals(expectedExpirationTime, expiresAt, "Expiration should be 100 minutes after issued time")
    }
}
