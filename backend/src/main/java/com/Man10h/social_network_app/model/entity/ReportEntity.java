package com.Man10h.social_network_app.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Lớp thực thể (Entity) đại diện cho bảng 'report' trong cơ sở dữ liệu.
 * Chứa thông tin về các báo cáo (tố cáo) bài viết vi phạm từ người dùng.
 * Ràng buộc 'uk_report_post_user' đảm bảo mỗi người dùng chỉ có thể tố cáo 
 * một bài viết tối đa một lần để chống spam.
 */
@Entity
@Table(name = "report",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_report_post_user", columnNames = {"post_id", "user_id"})
    }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
