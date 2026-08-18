package com.fitness.gateway.client;

import com.fitness.gateway.dto.RegisterRequest;
import com.fitness.gateway.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("lb://USER-SERVICE").build();
    }

    public Mono<Boolean> validateUserProfile(String userId) {
        return this.webClient.get()
                .uri("/api/users/validate/{userId}", userId)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        return this.webClient.post()
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class);
    }
}
