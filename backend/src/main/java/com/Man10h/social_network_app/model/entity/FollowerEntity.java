package com.Man10h.social_network_app.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Lớp thực thể (Entity) đại diện cho bảng 'follower' trong cơ sở dữ liệu.
 * Quản lý mối quan hệ theo dõi giữa các người dùng (Ai đang theo dõi ai).
 * Bảng sử dụng ràng buộc duy nhất 'uk_user_follower' trên cặp (user_id, follower_id)
 * để đảm bảo một người không thể theo dõi một người khác nhiều lần.
 */
@Entity
@Table(
        name = "follower",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_follower",
                        columnNames = {"user_id", "follower_id"}
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "follower_id")
    private String followerId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
