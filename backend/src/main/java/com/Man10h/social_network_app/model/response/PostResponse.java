package com.Man10h.social_network_app.model.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private Date createDate;
    private List<ImageResponse> images;
    private List<CommentResponse> commentResponseList;
    private List<ContentModerationResponse> moderationResponseList;
    private List<ReportResponse> reportResponseList;
    private UserResponse userResponse;
    private Long like;
    private Boolean warning;
}
