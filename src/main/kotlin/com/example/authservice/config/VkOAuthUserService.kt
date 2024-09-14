package com.example.authservice.config

import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.convert.converter.Converter
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

@Component
class VkOAuthUserService : DefaultOAuth2UserService() {
    private lateinit var restOperations: RestOperations
    private val requestEntityConverter: Converter<OAuth2UserRequest, RequestEntity<*>> =
        OAuth2UserRequestEntityConverter()

    init {
        val restTemplate = RestTemplate()
        restTemplate.errorHandler = OAuth2ErrorResponseErrorHandler()
        this.restOperations = restTemplate
    }

    @Throws(OAuth2AuthenticationException::class, IllegalArgumentException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User: OAuth2User
        if (userRequest.clientRegistration.registrationId != "vk") {
            oAuth2User = super.loadUser(userRequest)
            return oAuth2User
        }
        Assert.notNull(userRequest, "userRequest cannot be null")
        if (!StringUtils.hasText(userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri)) {
            val oauth2Error = OAuth2Error(
                MISSING_USER_INFO_URI_ERROR_CODE,
                "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: " +
                    userRequest.clientRegistration.registrationId,
                null
            )
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        val userNameAttributeName = userRequest.clientRegistration.providerDetails
            .userInfoEndpoint.userNameAttributeName
        if (!StringUtils.hasText(userNameAttributeName)) {
            val oauth2Error = OAuth2Error(
                MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
                "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: " +
                    userRequest.clientRegistration.registrationId,
                null
            )
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        val request: RequestEntity<*>? = this.requestEntityConverter.convert(userRequest)
        val response: ResponseEntity<Map<String?, Any?>?>?
        try {
            response = request?.let { this.restOperations.exchange(it, PARAMETERIZED_RESPONSE_TYPE) }
        } catch (ex: OAuth2AuthorizationException) {
            var oauth2Error: OAuth2Error = ex.error
            val errorDetails = StringBuilder()
            errorDetails.append("Error details: [")
            errorDetails.append("UserInfo Uri: ").append(
                userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri
            )
            errorDetails.append(", Error Code: ").append(oauth2Error.errorCode)
            if (oauth2Error.description != null) {
                errorDetails.append(", Error Description: ").append(oauth2Error.description)
            }
            errorDetails.append("]")
            oauth2Error = OAuth2Error(
                INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                "An error occurred while attempting to retrieve the UserInfo Resource: $errorDetails", null
            )
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex)
        } catch (ex: RestClientException) {
            val oauth2Error = OAuth2Error(
                INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                "An error occurred while attempting to retrieve the UserInfo Resource: " + ex.message, null
            )
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex)
        }

        val valueList = response?.body?.get("response") as ArrayList<*>?
        val userAttributes = valueList?.get(0) as MutableMap<String, Any>? //TODO че с этим делать
        val authorities: MutableSet<GrantedAuthority> = LinkedHashSet()
        authorities.add(OAuth2UserAuthority(userAttributes))
        val token: OAuth2AccessToken = userRequest.accessToken
        for (authority in token.scopes) {
            authorities.add(SimpleGrantedAuthority("SCOPE_$authority"))
        }
        userAttributes?.set("name",
            userAttributes["first_name"].toString() + " " + userAttributes["last_name"].toString()
        )
        return DefaultOAuth2User(authorities, userAttributes, "name")
    }

    companion object {
        private const val MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri"
        private const val MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute"
        private const val INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response"
        private val PARAMETERIZED_RESPONSE_TYPE: ParameterizedTypeReference<Map<String?, Any?>?> =
            object : ParameterizedTypeReference<Map<String?, Any?>?>() {}
    }
}

