package com.autonomous.drone.persistance.postgreSql.repository;

import com.autonomous.drone.persistance.postgreSql.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);
    User findFirstByUsername(String username);
    int deleteUserByUsername(String userName);
}
