package com.autonomous.drone.service

import com.auth0.jwt.exceptions.JWTDecodeException
import com.autonomous.drone.persistance.postgreSql.domain.User
import com.autonomous.drone.persistance.postgreSql.repository.TokenStoreRepository
import com.autonomous.drone.persistance.postgreSql.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class TokenServiceTest extends Specification {

    @Autowired
    private TokenService tokenService

    @Autowired
    private TokenStoreRepository tokenStoreRepository

    private User user

    @Autowired
    private UserRepository userRepository

    def setup(){
        user = new User("userTokenName", "pass")
        user = userRepository.save(user)
    }
    def "CreateToken"() {
        when: "create token with correct data"
            def token = tokenService.createToken(user)
        then: "token should be save in Db"
            assert tokenStoreRepository.existsByToken(token)
    }

    def "Validate"() {
        setup:
            def expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJyb2xlIjpbIlVTRVIiXSwicGVybWlzc2lvbiI6WyJGSVJTVF9QRVJNSVNTSU9OIl0sImV4cCI6MTU0MzE0ODM2NSwidXNlcklkIjoxfQ.ZvZ4t2TPAQvj58-137IO_FuXkZthgRydwkQW4w9GMjOETxy-Vbe7XVWiFqAZi_vmkrse63pCl1Tw6WAEPgqpjA"
            String correctToken = tokenService.createToken(user)

        when: "validate correct token"
            def isValidate = tokenService.validate(correctToken)
        then: "should be true"
            assert isValidate == true

        when: "validate expired token"
            isValidate = tokenService.validate(expiredToken)
        then: "should be false"
            assert isValidate == false

        when: "validate not existing token"
            isValidate = tokenService.validate("asdasdasdasdasd")
        then: "should be false"
            assert isValidate == false


    }

    def "DeleteToken"() {
        setup:
            def correctToken = tokenService.createToken(user)

        when: "delete existing token"
            def deleted = tokenService.deleteToken(correctToken)
        then: "return should be 1"
            assert deleted == 1

        when: "delete not existing token"
            deleted = tokenService.deleteToken("sometoken")
        then: "deleted should be 0"
            assert deleted == 0
    }

    def "GetDecodedJwtFromToken"() {
        setup:
            def correctToken = tokenService.createToken(user)

        when: "decode correct token"
            def decodedToken = tokenService.getDecodedJwtFromToken(correctToken)
        then: "claim exp should be greater than time now"
            assert decodedToken.getClaim("exp").asDate() > new Date()

        when: "decode incorrect token"
            tokenService.getDecodedJwtFromToken("incorrecttoken")
        then: "should thrown exception"
            thrown(JWTDecodeException)
    }
}
