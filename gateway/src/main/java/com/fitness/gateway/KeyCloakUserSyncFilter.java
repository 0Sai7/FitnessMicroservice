package com.fitness.gateway;

import com.fitness.gateway.client.UserServiceClient;
import com.fitness.gateway.dto.RegisterRequest;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements GlobalFilter {

    private final UserServiceClient userServiceClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        try {
            RegisterRequest request = getUserDetailsFromToken(token);
            String keycloakId = request.getKeyCloakId();

            return userServiceClient.validateUserProfile(keycloakId)
                    .flatMap(exists -> {
                        if (!exists) {
                            log.info("User with KeyCloak ID {} does not exist. Registering...", keycloakId);
                            return userServiceClient.registerUser(request).then(Mono.just(keycloakId));
                        } else {
                            log.info("User with KeyCloak ID {} already exists. Skipping sync.", keycloakId);
                            return Mono.just(keycloakId);
                        }
                    })
                    .flatMap(userId -> {
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-ID", userId)
                                .build();
                        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                        return chain.filter(mutatedExchange);
                    });

        } catch (ParseException e) {
            log.error("Error parsing JWT token", e);
            return chain.filter(exchange); // Or return an error response
        }
    }

    private RegisterRequest getUserDetailsFromToken(String token) throws ParseException {
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
        SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        RegisterRequest request = new RegisterRequest();
        request.setEmail(claims.getStringClaim("email"));
        request.setPassword("password"); // A dummy password, as KeyCloak handles authentication
        request.setFirstName(claims.getStringClaim("given_name"));
        request.setLastName(claims.getStringClaim("family_name"));
        request.setKeyCloakId(claims.getSubject()); // 'sub' is the standard claim for subject/user ID
        return request;
    }
}
