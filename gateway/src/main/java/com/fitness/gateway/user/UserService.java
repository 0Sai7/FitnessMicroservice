package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUserProfile(String userId) {
        log.info("calling userService");

            return userServiceWebClient.get().uri("api/users/validate/" + userId).retrieve().bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class,e -> {
                        if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                            return Mono.error(new RuntimeException("User not found"+userId));
                        } else if (e.getStatusCode()==HttpStatus.BAD_REQUEST) {
                            return Mono.error(new RuntimeException("Bad Request"));
                            
                        }
                        return Mono.error(new RuntimeException("Internal Server Error"));
                    });



    }


    public Mono<UserResponse> registerUser(RegisterRequest request) {
        log.info("calling userRegistration for {}", request.getEmail());
        return userServiceWebClient.post().uri("/api/users/register" )
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class,e -> {
                    if (e.getStatusCode()==HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("Bad Request : "+e.getMessage()));

                    }
                    return Mono.error(new RuntimeException("Internal Server Error"+e.getMessage()));
                });

    }
}
