package com.Man10h.social_network_app.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowerResponse {
    private String id;
    private String fullName;
    private String profilePictureUrl;
}
