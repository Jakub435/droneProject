package com.autonomous.drone.router

import com.autonomous.drone.customEnum.PermissionEnum
import com.autonomous.drone.customEnum.RoleEnum
import com.autonomous.drone.persistance.postgreSql.domain.Permission
import com.autonomous.drone.persistance.postgreSql.domain.Role
import com.autonomous.drone.persistance.postgreSql.domain.User
import com.autonomous.drone.persistance.postgreSql.repository.TokenStoreRepository
import com.autonomous.drone.persistance.postgreSql.repository.UserRepository
import com.autonomous.drone.service.TokenService
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AuthControllerTest extends Specification {

    @Autowired
    UserRepository userRepository

    @Autowired
    TokenStoreRepository tokenStoreRepository

    @Autowired
    TokenService tokenService

    private RESTClient restClient = new RESTClient("http://localhost:8080/api/auth/")
    private password = "password"
    private username = "testUsername"

    def setup(){
        restClient.handler.failure = restClient.handler.success
        restClient.post(
                path:"register/",
                requestContentType: JSON,
                body:[
                        username:username,
                        password:password
                ])
    }

    def "User should be register"() {
        when:
            def response = restClient.post(
                    path:"register/",
                    requestContentType: JSON,
                    body:[
                            username:username + "newUser",
                            password:password
                    ]
            )
        then: "status is 200 Created"
            assert response.status == 200

        and: "user is save in Db"
           assert userRepository.existsByUsername(username + "newUser")
    }

    def "User should not be register"(){
        when: "Try register with the same username"
            def response
            response = restClient.post(
                    path: "/register",
                    requestContentType: TEXT,
                    body: [
                            username: username,
                            password: password
                    ])

        then: "status is 401"
            assert response.status == 401
    }

    def "Should login with correct data"() {
        when: "send correct body"
            def response = restClient.post(
                    path:"login/",
                    requestContentType: JSON,
                    body:[
                            username:username,
                            password:password
                    ])

        then: "status is 200"
            assert response.status == 200
    }

    def "Should not login"(){
        when: "send incorrect body"
            def response = restClient.post(
                    path:"login/",
                    requestContentType: JSON,
                    body:[
                            username:username + "incorrectLogin",
                            password:password
                    ])
        then: "status is 401"
            assert response.status == 401
    }

    def "Logout with correct token"() {
        setup:
            User user = new User()
            Role role = new Role()
            Set<Role> roles = new HashSet<>()
            Set<Permission> permission = new HashSet<>()

            permission.add(new Permission(PermissionEnum.FIRST_PERMISSION.toString()))
            permission.add(new Permission(PermissionEnum.SECOND_PERMISSION.toString()))
            permission.add(new Permission(PermissionEnum.THIRD_PERMISSION.toString()))

            role.setName(RoleEnum.ADMIN.toString())
            role.setPermissions(permission)
            roles.add(role)

            user.id = 213123
            user.password = "pass"
            user.username = "adminTest"
            user.setRoles(roles)

            def token = tokenService.createToken(user)

        when:"Header with correct token"
            def response = restClient.delete(
                    path:"logout/",
                    headers: ["Authorization" : token]
                    )
        then: "status is 200"
            response.status == 200
        and: "token should be delete from db"
            assert tokenStoreRepository.existsByToken(token) == false
    }

    def "Logout with incorrect token"(){
        when:"Header with correct token"
            def response = restClient.delete(
                    path:"logout/",
                    headers: ["Authorization" : "exampletoken"]
            )
        then: "status is 401"
            response.status == 401
    }

}
