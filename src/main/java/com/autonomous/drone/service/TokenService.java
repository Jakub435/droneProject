package com.autonomous.drone.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.autonomous.drone.persistance.postgreSql.domain.TokenObject;
import com.autonomous.drone.persistance.postgreSql.domain.User;
import com.autonomous.drone.persistance.postgreSql.repository.TokenStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

@Service
public class TokenService {

    @Autowired
    private TokenStoreRepository tokenStoreRepository;

    public static final String SECRET = "PwHfOV4n2rCj1M7KoPSf3s";

    public String createToken(User user){
        try {
            String[] userRoleNames = user.createRoleNameArray();
            String[] userPermissionNames = user.createPermissionNameArray();

            Algorithm algorithm = Algorithm.HMAC512(SECRET);

            String token = JWT.create()
                    .withExpiresAt( getExpirationDate() )
                    .withClaim("userId", user.getId() )
                    .withArrayClaim("role", userRoleNames)
                    .withArrayClaim("permission", userPermissionNames)
                    .sign(algorithm);

            saveTokenInDb(token, user.getId());

            return token;
        } catch (UnsupportedEncodingException | JWTCreationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean validate(String token) throws UnsupportedEncodingException {
        if (!tokenStoreRepository.existsByToken(token)){ return false; }

        DecodedJWT decodedJWT = getDecodedJwtFromToken(token);
        Date expirationTime = decodedJWT.getClaim("exp").asDate();
        Date now = new Date();
        if( expirationTime.before(now)) { return false; }

        return true;
    }

    @Transactional
    public int deleteToken(String token){
        return tokenStoreRepository.deleteByToken(token);
    }

    public DecodedJWT getDecodedJwtFromToken(String token) throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC512(SECRET);
        JWTVerifier verifier = JWT.require(algorithm).build();

        return verifier.verify(token);
    }

    private void saveTokenInDb(String token, long userId){
        TokenObject tokenStore = new TokenObject(userId, token);
        tokenStoreRepository.save(tokenStore);
    }

    private Date getExpirationDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);

        return calendar.getTime();
    }
}
