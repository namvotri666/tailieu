package com.Man10h.social_network_app.service.impl;

import com.Man10h.social_network_app.exception.exceptions.NotFoundException;
import com.Man10h.social_network_app.model.entity.FollowerEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.FollowerResponse;
import com.Man10h.social_network_app.repository.FollowerRepository;
import com.Man10h.social_network_app.repository.UserRepository;
import com.Man10h.social_network_app.service.FollowerService;
import com.Man10h.social_network_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Lớp dịch vụ quản lý tính năng theo dõi (Follow) người dùng khác.
 */
@Service
@RequiredArgsConstructor
public class FollowerServiceImpl implements FollowerService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final NotificationService notificationService;

    /**
     * Lấy danh sách những người đang theo dõi người dùng hiện tại.
     * @param userId ID của người dùng.
     * @return Danh sách FollowerResponse
     */
    @Override
    public List<FollowerResponse> getFollowers(String userId) {
        return followerRepository.getFollowers(userId);
    }

    /**
     * Chức năng theo dõi (Follow) hoặc Hủy theo dõi (Unfollow).
     * @param followerId ID của người sẽ được theo dõi.
     * @param userId ID của người dùng thực hiện thao tác theo dõi.
     */
    @Transactional
    public void follow(String followerId, String userId) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        Optional<UserEntity> optionalFollower = userRepository.findById(followerId);

        if(optionalUserEntity.isEmpty() || optionalFollower.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        Optional<FollowerEntity> optionalFollowerEntity = followerRepository.findByUserEntity_IdAndFollowerId(userId, followerId);
        if(optionalFollowerEntity.isEmpty()){
            UserEntity userEntity = optionalUserEntity.get();
            FollowerEntity followerEntity = FollowerEntity.builder()
                    .followerId(followerId)
                    .userEntity(userEntity)
                    .build();
            followerRepository.save(followerEntity);

            notificationService.followUser(followerId, userEntity);
        }
        else {
            followerRepository.delete(optionalFollowerEntity.get());
        }

    }
}
