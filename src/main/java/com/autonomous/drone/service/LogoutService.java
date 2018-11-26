package com.autonomous.drone.service;

import com.autonomous.drone.customException.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    @Autowired
    private TokenService tokenService;

    public ResponseEntity<String> logout(String token) throws NotFoundException {
        if(tokenService.deleteToken(token) != 0)
            return ResponseEntity.ok("");
        throw new NotFoundException();
    }
}
