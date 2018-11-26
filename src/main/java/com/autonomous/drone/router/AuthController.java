package com.autonomous.drone.router;

import com.autonomous.drone.customException.NotFoundException;
import com.autonomous.drone.customException.UserExistsException;
import com.autonomous.drone.customException.InvalidUsernameOrPasswordException;
import com.autonomous.drone.intercepter.PermissionSecure;
import com.autonomous.drone.persistance.postgreSql.domain.User;
import com.autonomous.drone.service.LoginService;
import com.autonomous.drone.service.LogoutService;
import com.autonomous.drone.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/api/auth")
public class AuthController {
    @Autowired
    private RegisterService registerService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private LogoutService logoutService;

    @PostMapping(path = "/register", consumes = "application/json")
    public ResponseEntity<String>
    signUp(@RequestBody @Valid User user) throws UserExistsException {
        return registerService.createAccount(user);
    }

    @PostMapping(path = "/login", consumes = "application/json")
    public ResponseEntity<String>
    login(@RequestBody @Valid User user) throws InvalidUsernameOrPasswordException {
        return loginService.login(user);
    }

    @DeleteMapping(path = "/logout")
    public ResponseEntity<String>
    logout(@RequestHeader("Authorization") String token) throws NotFoundException {
        return logoutService.logout(token);
    }
}
