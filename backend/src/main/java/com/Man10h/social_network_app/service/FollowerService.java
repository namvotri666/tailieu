package com.Man10h.social_network_app.service;

import com.Man10h.social_network_app.model.response.FollowerResponse;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowerService {
    public List<FollowerResponse> getFollowers(String username);
    public void follow(String followerId, String userId);
}
