package com.fitness.userservice.controller;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @GetMapping("/fetch/{userId}")
    public ResponseEntity<UserResponse> userProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));

    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {

        return ResponseEntity.ok( userService.register(registerRequest));
    }

    @GetMapping("/validate/{userId}")
    public ResponseEntity<Boolean> validateUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.checkUserProfile(userId));

    }




}
