package com.autonomous.drone.service

import com.autonomous.drone.customException.UserExistsException
import com.autonomous.drone.persistance.postgreSql.domain.User
import com.autonomous.drone.persistance.postgreSql.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class RegisterServiceTest extends Specification {
    @Autowired
    private RegisterService registerService

    @Autowired
    private UserRepository userRepository

    def "CreateAccount"() {
        setup:
            User user = new User("createUserName", "pass")

        when: "create new user with correct data and user not exist"
            registerService.createAccount(user)
        then: "user should be save id Db"
            userRepository.existsByUsername("createUserName")

        when: "user exists"
            registerService.createAccount(user)
        then: "should throw UserExistException"
            thrown(UserExistsException)
    }
}
