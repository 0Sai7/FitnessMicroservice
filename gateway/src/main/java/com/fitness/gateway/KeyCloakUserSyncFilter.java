package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
        String userID = exchange.getRequest().getHeaders().getFirst("userID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        RegisterRequest request=getUserDetails(token);

        if (userID == null){
            userID=request.getKeyCloakId();
        }

        if(userID!=null && token!=null){
            String finalUserID=userID;
         return    userService.validateUserProfile(userID)
                 .flatMap(exist -> {
                     if(!exist){
                         if(request!=null){
                             return userService.registerUser(request)
                                     .then(Mono.empty());
                         }else{
                             return  Mono.empty();
                         }
                     }else{
                         log.info("user already exists skipping sync");
                         return  Mono.empty();
                     }
                 }).then(Mono.defer(()->{
                     ServerHttpRequest mutatedreq=exchange.getRequest().mutate()
                             .header("X-User-ID",finalUserID)
                             .build();
                     return chain.filter(exchange.mutate().request(mutatedreq).build());
                 }));
        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest request = new RegisterRequest();
            request.setEmail(claims.getStringClaim("email"));
            request.setPassword("password");
            request.setFirstName(claims.getStringClaim("given_name"));
            request.setLastName(claims.getStringClaim("family_name"));
            request.setKeyCloakId(claims.getStringClaim("sub"));
            return request;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


}
