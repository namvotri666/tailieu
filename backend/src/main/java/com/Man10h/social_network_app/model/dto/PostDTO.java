package com.Man10h.social_network_app.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    @JsonProperty(value = "title", required = true)
    private String title;

    @JsonProperty(value = "content", required = true)
    private String content;
}
