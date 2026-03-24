package com.himanshu.auth_backend.repositories;

import com.himanshu.auth_backend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Users, UUID> {

}
