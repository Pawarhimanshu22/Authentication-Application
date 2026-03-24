package com.himanshu.auth_backend.repositories;

import com.himanshu.auth_backend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Users, UUID> {

}
