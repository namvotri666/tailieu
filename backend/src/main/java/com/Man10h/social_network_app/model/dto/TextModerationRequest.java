package com.Man10h.social_network_app.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextModerationRequest {
    private String text;
}