package com.Man10h.social_network_app.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModerationResponse {
    private List<CategoryAnalysis> categoriesAnalysis;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryAnalysis {
        private String category;
        private int severity;
    }
}