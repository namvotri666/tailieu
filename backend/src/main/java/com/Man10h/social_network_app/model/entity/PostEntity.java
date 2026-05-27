package com.Man10h.social_network_app.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "post")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "warning")
    public Boolean warning;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @BatchSize(size = 10)
    private UserEntity userEntity;

    @OneToMany(mappedBy = "postEntity", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @BatchSize(size = 10)//max post fetch load: 10
    private List<ImageEntity> imageEntityList;

    @OneToMany(mappedBy = "postEntity", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @BatchSize(size = 10)//max post fetch load: 10
    private List<CommentEntity> commentEntityList;

    @OneToMany(mappedBy = "postEntity", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<ReportEntity> reportEntityList;

    @OneToMany(mappedBy = "postEntity", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<ContentModerationEntity> contentModerationEntityList;

    @OneToMany(mappedBy = "postEntity", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<PostLikeEntity> postLikeEntityList;
}
