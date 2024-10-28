// package com.example.authservice
//
// import com.example.authservice.database.UserEntity
// import com.example.authservice.database.UserRepository
// import com.example.authservice.service.UserDetailsServiceImpl
// import org.junit.jupiter.api.Assertions.assertEquals
// import org.junit.jupiter.api.Assertions.assertTrue
// import org.junit.jupiter.api.Test
// import org.junit.jupiter.api.assertThrows
// import org.mockito.Mockito.mock
// import org.mockito.Mockito.`when`
// import org.springframework.security.core.userdetails.UsernameNotFoundException
//
// class UserDetailsServiceImplTest {
//
//     private val userRepo: UserRepository = mock(UserRepository::class.java)
//     private val userDetailsService = UserDetailsServiceImpl(userRepo)
//
//     @Test
//     fun `should load user by username successfully`() {
//         val username = "testUser"
//         val password = "testPass"
//         val userEntity = UserEntity(name=username, password=password)
//
//         `when`(userRepo.findByName(username)).thenReturn(userEntity)
//
//         val userDetails = userDetailsService.loadUserByUsername(username)
//
//         assertEquals(username, userDetails.username)
//         assertEquals(password, userDetails.password)
//         assertTrue(userDetails.authorities.isNotEmpty())
//     }
//
//     @Test
//     fun `should throw UsernameNotFoundException when user not found`() {
//         val username = "unknownUser"
//
//         `when`(userRepo.findByName(username)).thenReturn(null)
//
//         val exception = assertThrows<UsernameNotFoundException> {
//             userDetailsService.loadUserByUsername(username)
//         }
//         assertEquals("User not found with username: $username", exception.message)
//     }
// }