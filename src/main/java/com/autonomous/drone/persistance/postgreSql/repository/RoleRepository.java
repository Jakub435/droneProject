package com.autonomous.drone.persistance.postgreSql.repository;

import com.autonomous.drone.persistance.postgreSql.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
