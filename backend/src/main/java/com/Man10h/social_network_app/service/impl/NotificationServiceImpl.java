package com.Man10h.social_network_app.service.impl;

import com.Man10h.social_network_app.exception.exceptions.GlobalException;
import com.Man10h.social_network_app.exception.exceptions.NotFoundException;
import com.Man10h.social_network_app.model.entity.FollowerEntity;
import com.Man10h.social_network_app.model.entity.NotificationEntity;
import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.NotificationResponse;
import com.Man10h.social_network_app.repository.FollowerRepository;
import com.Man10h.social_network_app.repository.NotificationRepository;
import com.Man10h.social_network_app.repository.PostRepository;
import com.Man10h.social_network_app.repository.UserRepository;
import com.Man10h.social_network_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void createAndSend(UserEntity sender, UserEntity receiver, String content, String targetId) {
        try{
            NotificationEntity notificationEntity = NotificationEntity.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content(content)
                    .createdAt(new Date())
                    .targetId(targetId)
                    .isRead(false)
                    .build();
            notificationRepository.save(notificationEntity);

            NotificationResponse notificationResponse = NotificationResponse.builder()
                    .id(notificationEntity.getId())
                    .targetId(targetId)
                    .content(content)
                    .createdAt(notificationEntity.getCreatedAt())
                    .sender(sender.getUsername())
                    .receiver(receiver.getUsername())
                    .isRead(notificationEntity.getIsRead())
                    .build();
            messagingTemplate.convertAndSendToUser(receiver.getUsername(), "/queue/notifications", notificationResponse);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void likePost(String postId, UserEntity sender) {
        Optional<PostEntity> optional = postRepository.findById(postId);
        if(optional.isEmpty()){
            throw new NotFoundException("Post not found");
        }
        PostEntity post = optional.get();
        if(!post.getUserEntity().equals(sender)){
            createAndSend(sender, post.getUserEntity(), "liked your post", postId);
        }
    }

    @Transactional
    public void commentPost(String postId, UserEntity sender) {
        Optional<PostEntity> optional = postRepository.findById(postId);
        if(optional.isEmpty()){
            throw new NotFoundException("Post not found");
        }
        PostEntity post = optional.get();
        if(!post.getUserEntity().equals(sender)){
            createAndSend(sender, post.getUserEntity(), "commented your post", postId);
        }
    }

    @Transactional
    public void followUser(String followerId, UserEntity currentUser) {
        Optional<UserEntity> optionalFollower = userRepository.findById(followerId);
        if(optionalFollower.isEmpty()){
            throw new NotFoundException("Follower not found");
        }
        createAndSend(currentUser, optionalFollower.get(), "followed you", followerId);
    }

    @Override
    public Page<NotificationResponse> getAllNotifications(UserEntity userEntity, Pageable pageable) {
        return notificationRepository.findByReceiver(userEntity, pageable)
                .map(notificationEntity -> {
                    return NotificationResponse.builder()
                            .id(notificationEntity.getId())
                            .targetId(notificationEntity.getTargetId())
                            .createdAt(notificationEntity.getCreatedAt())
                            .sender(notificationEntity.getSender().getUsername())
                            .receiver(notificationEntity.getReceiver().getUsername())
                            .content(notificationEntity.getContent())
                            .createdAt(notificationEntity.getCreatedAt())
                            .isRead(notificationEntity.getIsRead())
                            .build();
                });
    }

    @Transactional
    public void readNotification(String notificationId, UserEntity currentUser) {
        Optional<NotificationEntity> optional = notificationRepository.findById(notificationId);
        if(optional.isEmpty()){
            throw new NotFoundException("Notification not found");
        }
        NotificationEntity notification = optional.get();
        if(!notification.getReceiver().getId().equals(currentUser.getId())){
            throw new GlobalException("Not main users");
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }


}
