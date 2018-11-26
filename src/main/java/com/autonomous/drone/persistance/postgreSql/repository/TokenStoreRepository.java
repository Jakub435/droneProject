package com.autonomous.drone.persistance.postgreSql.repository;

import com.autonomous.drone.persistance.postgreSql.domain.TokenObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenStoreRepository extends JpaRepository<TokenObject, Long> {
    TokenObject findFirstByToken(String token);
    boolean existsByToken(String token);
    int deleteByToken(String token);
}
