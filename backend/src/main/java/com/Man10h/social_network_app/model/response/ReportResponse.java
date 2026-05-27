package com.Man10h.social_network_app.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private Long id;
    private String title;
    private String content;
    private String postId;
    private String userId;
}
