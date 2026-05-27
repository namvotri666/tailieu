package com.Man10h.social_network_app.model.response;

import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentModerationResponse {
    private String id;
    private String contentType;
    private String provider;
    private LocalDateTime createAt;
    private List<ContentModerationResultResponse> contentModerationResultResponseList;
}
