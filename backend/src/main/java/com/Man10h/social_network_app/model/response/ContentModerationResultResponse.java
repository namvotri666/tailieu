package com.Man10h.social_network_app.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentModerationResultResponse {
    private String id;
    private String category;
    private int severity;
}
