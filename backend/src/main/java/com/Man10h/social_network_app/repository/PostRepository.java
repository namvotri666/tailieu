package com.Man10h.social_network_app.repository;

import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, String> {

    @Query(value = """
    SELECT DISTINCT p FROM PostEntity p
    LEFT JOIN FETCH p.imageEntityList
    WHERE p.id = :id 
""")
    Optional<PostEntity> getPostsWithImage(@Param("id") String id);

    @Query(value = """
    SELECT DISTINCT p FROM PostEntity p
    LEFT JOIN FETCH p.commentEntityList
    WHERE p.id = :id 
""")
    Optional<PostEntity> getPostsWithComments(@Param("id") String id);

    @Query(
            value = """
    SELECT p FROM PostEntity p
    LEFT JOIN FollowerEntity f 
    ON f.followerId = p.userEntity.id AND f.userEntity.id = :id
    ORDER BY 
      CASE 
        WHEN f.followerId IS NOT NULL THEN 0 ELSE 1
      END,
      p.createDate DESC
  """,
            countQuery = """
    SELECT COUNT(p.id) FROM PostEntity p
  """
    )
    Page<PostEntity> getPosts(@Param("id") String userId, Pageable pageable);


    Page<PostEntity> findByUserEntity_Id(String userId, Pageable pageable);


    @Query(value = """
    SELECT DISTINCT p FROM PostEntity p
""")
    Page<PostEntity> getAllPosts(Pageable pageable);

    @Query(value = """
    SELECT DISTINCT p FROM PostEntity p
    WHERE p.title LIKE CONCAT('%', :title, '%')
""")
    Page<PostEntity> findByTitle(@Param("title") String title, Pageable pageable);

    List<PostEntity> findByUserEntity(UserEntity userEntity);

    @Query("""
    SELECT DISTINCT p FROM PostEntity p
    LEFT JOIN FETCH p.contentModerationEntityList cm
    LEFT JOIN FETCH cm.contentModerationResultEntityList 
    WHERE p.id = :id
""")
    Optional<PostEntity> getPostWithContentModeration(@Param("id") String id);

    Page<PostEntity> findByWarning(Boolean warning, Pageable pageable);
}
