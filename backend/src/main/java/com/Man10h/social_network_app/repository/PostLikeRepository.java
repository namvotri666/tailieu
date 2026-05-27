package com.Man10h.social_network_app.repository;

import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.entity.PostLikeEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeEntity, String> {
    void deleteByUserEntityAndPostEntity(UserEntity userEntity, PostEntity postEntity);
    Optional<PostLikeEntity> findByUserEntityAndPostEntity(UserEntity userEntity, PostEntity postEntity);

    boolean existsByPostEntity_IdAndUserEntity(String postId, UserEntity userEntity);
    void deleteByPostEntity(PostEntity postEntity);
}
