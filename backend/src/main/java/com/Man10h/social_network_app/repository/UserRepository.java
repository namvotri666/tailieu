package com.Man10h.social_network_app.repository;

import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    @Query(value = """
        SELECT DISTINCT u FROM UserEntity u
        LEFT JOIN FETCH u.imageEntityList
        WHERE u.id = :id
 """)
    Optional<UserEntity> getProfile(@Param("id") String userId);

    @Query(value = """
        SELECT DISTINCT u FROM UserEntity u
        JOIN u.roleEntity r
        WHERE r.id != 1
 """)
    Page<UserEntity> getAllUsers(Pageable pageable);


    @Query(value = """
        SELECT DISTINCT u FROM UserEntity u
        JOIN FETCH u.roleEntity r
        WHERE (:name IS NULL OR 
               u.username LIKE CONCAT('%', :name, '%') OR 
               u.firstName LIKE CONCAT('%', :name, '%') OR 
               u.lastName LIKE CONCAT('%', :name, '%'))
        AND (:enabled IS NULL OR u.enabled = :enabled) AND r.id != 1
""",
    countQuery = """
        SELECT COUNT(DISTINCT u) FROM UserEntity u
                JOIN u.roleEntity r
                WHERE (:name IS NULL OR\s
                       u.username LIKE CONCAT('%', :name, '%') OR\s
                       u.firstName LIKE CONCAT('%', :name, '%') OR\s
                       u.lastName LIKE CONCAT('%', :name, '%'))
                AND (:enabled IS NULL OR  u.enabled = :enabled) AND r.id != 1
""")
    Page<UserEntity> findUsersByNameAndEnabled(@Param("name") String name,
                                     @Param("enabled") boolean enabled,
                                     Pageable pageable);


}
