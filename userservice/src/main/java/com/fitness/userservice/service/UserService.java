package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.models.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

   UserRepository userRepository;

   public UserResponse getUserProfile(String userId) {
      User user = userRepository.findById(userId).get();
      UserResponse userResponse = new UserResponse();
      userResponse.setId(user.getId());
      userResponse.setFirstName(user.getFirstName());
      userResponse.setLastName(user.getLastName());
      userResponse.setEmail(user.getEmail());
      userResponse.setPassword(user.getPassword());
      return userResponse;
   }


    public UserResponse register(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        User user = new User();

        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setKeyCloakId(registerRequest.getKeyCloakId());


        User savedUser = userRepository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(savedUser.getId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setKeyCloakId(savedUser.getKeyCloakId());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());

        return userResponse;

    }

    public  Boolean checkUserProfile(String userId) {

        if(userRepository.existsByKeyCloakId(userId)){
            return true;
        }else {
            return false;
        }
    }
}
