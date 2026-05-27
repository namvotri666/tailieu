package com.Man10h.social_network_app.repository;

import com.Man10h.social_network_app.model.entity.CommentEntity;
import com.Man10h.social_network_app.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, String> {
    void deleteById(String commentId);
    List<CommentEntity> findByUserId(String userId);
    void deleteByPostEntity(PostEntity postEntity);
}
