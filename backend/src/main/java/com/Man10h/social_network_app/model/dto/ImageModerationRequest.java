package com.Man10h.social_network_app.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageModerationRequest {
    private ImageContent image;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageContent {
        private String content;
    }
}