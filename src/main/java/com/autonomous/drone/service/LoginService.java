package com.autonomous.drone.service;

import com.autonomous.drone.customException.InvalidUsernameOrPasswordException;
import com.autonomous.drone.persistance.postgreSql.domain.User;
import com.autonomous.drone.persistance.postgreSql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public ResponseEntity<String> login(User login)
            throws InvalidUsernameOrPasswordException{
        String username = login.getUsername();
        String loginPassword = login.getPassword();

        User user = getUserByName(username);
        String password = user.getPassword();

        if (!bCryptPasswordEncoder.matches(loginPassword,password))
            throw new InvalidUsernameOrPasswordException();

        String token = tokenService.createToken(user);

        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    private User getUserByName(String username) throws InvalidUsernameOrPasswordException{
        if (!userRepository.existsByUsername(username))
            throw new InvalidUsernameOrPasswordException();
        return userRepository.findFirstByUsername(username);
    }

}
