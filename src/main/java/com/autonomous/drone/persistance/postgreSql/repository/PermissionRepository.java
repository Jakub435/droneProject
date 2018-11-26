package com.autonomous.drone.persistance.postgreSql.repository;

import com.autonomous.drone.persistance.postgreSql.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
}
