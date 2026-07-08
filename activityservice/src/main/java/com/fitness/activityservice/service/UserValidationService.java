package com.fitness.activityservice.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final WebClient userServiceWebClient;

    public boolean validateUserProfile(String userId) {
        try {
            return userServiceWebClient.get().uri("api/users/validate/" + userId).retrieve().bodyToMono(Boolean.class).block();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }


}
