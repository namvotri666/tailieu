package com.Man10h.social_network_app.service.impl;

import com.Man10h.social_network_app.exception.exceptions.GlobalException;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.repository.UserRepository;
import com.Man10h.social_network_app.service.TokenService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserRepository userRepository;
    private final SecretKey secretKey;

    @Override
    public String generateToken(UserEntity userEntity) {
        JWTClaimsSet claim =  new JWTClaimsSet.Builder()
                .subject(userEntity.getUsername())
                .claim("id", userEntity.getId())
                .expirationTime(new Date(new Date().getTime() + 1000 * 60 * 60))
                .claim("roles", Collections.singletonList(userEntity.getRoleEntity().getName()))
                .build();
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, claim);
        try{
            JWSSigner signer = new MACSigner(secretKey);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    @Override
    public String getUsername(String token) {
        if(!validateToken(token)){
            return null;
        }
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    @Override
    public boolean validateToken(String token) {
        try{
            JWSVerifier verifier = new MACVerifier(secretKey);
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.verify(verifier) && new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    @Override
    public GoogleIdToken.Payload verifyIdToken(String idToken) {
        GoogleIdTokenVerifier verifier =
                new GoogleIdTokenVerifier.Builder(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance()
                )
                        .setAudience(List.of("YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"))
                        .build();

        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (idToken == null) throw new RuntimeException("Invalid token");
            return token.getPayload();
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }



}
