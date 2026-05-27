package com.Man10h.social_network_app.repository;

import com.Man10h.social_network_app.model.entity.ImageEntity;
import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, String> {
    void deleteByUserEntity(UserEntity userEntity);
    void deleteByPostEntity(PostEntity postEntity);

    // ImageRepository
    @Query("""
    SELECT i FROM ImageEntity i
    WHERE i.userEntity.id IN :userIds
    AND i.id = (
        SELECT MIN(i2.id) FROM ImageEntity i2 
        WHERE i2.userEntity.id = i.userEntity.id
    )
""")
    List<ImageEntity> findFirstImagesByUserIds(@Param("userIds") List<String> userIds);


}
