package com.Man10h.social_network_app.repository;

import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.entity.ReportEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    @Override
    Page<ReportEntity> findAll(Pageable pageable);

    Page<ReportEntity> findByUserEntity(UserEntity userEntity, Pageable pageable);

    void deleteById(Long reportId);

    Optional<ReportEntity> findById(Long reportId);

    Page<ReportEntity> findByUserEntity_Id(String userId, Pageable pageable);
    Page<ReportEntity> findByPostEntity_Id(String postId, Pageable pageable);

    List<ReportEntity> findByUserEntity(UserEntity userEntity);

    void deleteByPostEntity(PostEntity postEntity);
}
