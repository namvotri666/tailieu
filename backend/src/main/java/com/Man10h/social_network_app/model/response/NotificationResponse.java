package com.Man10h.social_network_app.model.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private String id;
    private String sender;
    private String receiver;
    private String targetId;
    private String content;
    private Date createdAt;
    private Boolean isRead;
}
