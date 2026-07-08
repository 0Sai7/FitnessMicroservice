package com.fitness.userservice.repository;

import com.fitness.userservice.models.User;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

     public Boolean existsByEmail(String email);
     public Boolean existsByKeyCloakId(String keyCloakId);
}
