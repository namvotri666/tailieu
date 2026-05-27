package com.Man10h.social_network_app.repository;

import com.Man10h.social_network_app.model.entity.FollowerEntity;
import com.Man10h.social_network_app.model.response.FollowerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<FollowerEntity, String> {
    @Query(value = """
        SELECT new com.Man10h.social_network_app.model.response.FollowerResponse(u.id, CONCAT(u.firstName, " ", u.lastName), i.url) FROM FollowerEntity f
        JOIN UserEntity u ON f.followerId = u.id
        LEFT JOIN ImageEntity i ON f.followerId = i.userEntity.id 
        WHERE f.userEntity.id = :id
""")
    List<FollowerResponse> getFollowers(@Param("id") String userId);

    Optional<FollowerEntity> findByUserEntity_IdAndFollowerId(String userId, String followerId);

}
