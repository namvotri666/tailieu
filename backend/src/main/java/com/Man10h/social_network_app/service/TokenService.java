package com.Man10h.social_network_app.service;

import com.Man10h.social_network_app.model.entity.UserEntity;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public interface TokenService {
    public String generateToken(UserEntity userEntity);

    public String getUsername(String token);

    public boolean validateToken(String token);

    public GoogleIdToken.Payload verifyIdToken(String idToken);
}
