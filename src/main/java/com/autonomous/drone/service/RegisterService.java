package com.autonomous.drone.service;

import com.autonomous.drone.customException.UserExistsException;
import com.autonomous.drone.persistance.postgreSql.domain.User;
import com.autonomous.drone.persistance.postgreSql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<String> createAccount(User user) throws UserExistsException {
        if( userRepository.existsByUsername(user.getUsername()) )
            throw new UserExistsException();

        String encryptedPassword = bCryptPasswordEncoder.encode( user.getPassword() );

        user.setPassword(encryptedPassword);
        userRepository.save(user);

        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
