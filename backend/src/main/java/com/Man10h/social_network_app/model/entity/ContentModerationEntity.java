package com.Man10h.social_network_app.model.entity;

import com.Man10h.social_network_app.model.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "content_moderation")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentModerationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

    @Column(name = "provider")
    private String provider;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    @OneToMany(mappedBy = "contentModerationEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContentModerationResultEntity> contentModerationResultEntityList;
}
