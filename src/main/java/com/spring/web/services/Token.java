package com.spring.web.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings(value = "all")
public final class Token {

    private final  Algorithm ALGORITHM = Algorithm.HMAC512(System.getenv("SECRET"));

    public  String genToken(String name, String surname, String birthday,
            int age, int exp, int sub, String email, String favoriteColor) {
        try {
            return JWT.create()
                    .withClaim("name", name)
                    .withClaim("surname", surname)
                    .withClaim("birth", birthday)
                    .withClaim("age", age)
                    .withClaim("email", email)
                    .withClaim("color", favoriteColor)
                    .withClaim("exp", System.currentTimeMillis() / 1000 + exp)
                    .withClaim("sub", sub)
                    .sign(ALGORITHM);
        }
        catch(JWTCreationException jce) {
            jce.printStackTrace();
        }
        return null;
    }

    public DecodedJWT checkToken(String token) throws JWTVerificationException  {
        JWTVerifier verifier = JWT.require(ALGORITHM).build();
        return verifier.verify(token);
    }
}
