package com.Man10h.social_network_app.model.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "content_moderation_result")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentModerationResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "content_moderation_id")
    private ContentModerationEntity contentModerationEntity;

    @Column(name = "category")
    private String category;

    @Column(name = "severity")
    private int severity;
}
