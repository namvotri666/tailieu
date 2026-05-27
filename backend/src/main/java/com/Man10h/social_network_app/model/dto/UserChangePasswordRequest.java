package com.Man10h.social_network_app.model.dto;

public record UserChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
