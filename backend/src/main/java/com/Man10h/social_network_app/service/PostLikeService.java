package com.Man10h.social_network_app.service;

import com.Man10h.social_network_app.model.entity.UserEntity;

public interface PostLikeService {
    public void likePost(String postId, String username);
    public Boolean doUserLikedPost(String postId, UserEntity userEntity);
}
