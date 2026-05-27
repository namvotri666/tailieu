package com.Man10h.social_network_app.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Lớp thực thể (Entity) đại diện cho bảng 'post_like' trong cơ sở dữ liệu.
 * Lưu trữ thông tin lượt thích của người dùng đối với bài viết.
 * Bảng này sử dụng ràng buộc duy nhất (UniqueConstraint) 'uk_like_post_user' 
 * trên cặp cột (post_id, user_id) để đảm bảo mỗi người dùng chỉ có thể thích một bài viết tối đa một lần.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_like_post_user",
                        columnNames = {"post_id", "user_id"}
                )
        }
)
public class PostLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    @Column(name = "create_at")
    private Date createAt;
}
