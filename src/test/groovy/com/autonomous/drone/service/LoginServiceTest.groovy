package com.autonomous.drone.service

import com.autonomous.drone.customException.InvalidUsernameOrPasswordException
import com.autonomous.drone.persistance.postgreSql.domain.User
import com.autonomous.drone.persistance.postgreSql.repository.TokenStoreRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import spock.lang.Specification

@SpringBootTest
class LoginServiceTest extends Specification {

    @Autowired
    private LoginService loginService

    @Autowired
    private RegisterService registerService

    @Autowired
    private TokenStoreRepository tokenStoreRepository

    def "Should login"() {
        setup:
            User user = new User("username","somepass")
            registerService.createAccount(user)

        when: "login with existing user"
            user = new User("username","somepass")
            ResponseEntity<String> tokenResponse = loginService.login(user)
            def token = tokenResponse.getBody()

        then: "token is save in Db"
            assert tokenStoreRepository.existsByToken(token)
    }

    def "Should not Login "(){
        when: "try login with incorrect username/password"
            User user = new User("username", "pass")
            def token = loginService.login(user).getBody()

        then: "Should thrown exception"
            thrown(InvalidUsernameOrPasswordException)
        and: "token should be null"
            assert token == null
    }
}
