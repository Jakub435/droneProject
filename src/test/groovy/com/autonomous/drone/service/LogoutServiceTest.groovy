package com.autonomous.drone.service

import com.autonomous.drone.customException.NotFoundException
import com.autonomous.drone.persistance.postgreSql.domain.User
import com.autonomous.drone.persistance.postgreSql.repository.TokenStoreRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class LogoutServiceTest extends Specification {

    @Autowired
    private LoginService loginService

    @Autowired
    private LogoutService logoutService

    @Autowired
    private RegisterService registerService

    @Autowired
    private TokenStoreRepository tokenStoreRepository

    def "logout with correct token"(){
        setup:
            User user = new User("usernametest", "password")
            registerService.createAccount(user)

            user = new User("usernametest", "password")
            def token = loginService.login(user).getBody()

        when: "logout with existing token"
            def response = logoutService.logout(token)

        then: "body is empty"
            response.body == ""
        and: "token should not exist in db"
            assert tokenStoreRepository.existsByToken(token) == false
    }

    def "logout with incorrect token"(){
        when: "logout with not existing token"
            logoutService.logout("sometoken")

        then: "should thrown NotFound exception"
            thrown(NotFoundException)
    }
}
