package com.example.authservice.service

import com.example.authservice.database.UserRepo
import com.example.common.util.Role
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.Collections


@Service
class UserDetailsServiceImpl(
    private val userRepo: UserRepo
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        return username?.let { userRepo.findByName(it) }?.let {
            User(
                it.name,
                it.password,
                Collections.singletonList(SimpleGrantedAuthority(Role.USER.name))
            )
        } ?: throw UsernameNotFoundException("User not found with username: $username")
    }
}