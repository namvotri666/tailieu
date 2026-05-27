package com.Man10h.social_network_app.service;


import com.Man10h.social_network_app.model.dto.CommentDTO;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.CommentResponse;

public interface CommentService {
    public CommentResponse createComment(String postId, String userId, CommentDTO comment);
    public void deleteComment(String commentId);
    public void deleteCommentByUser(String commentId, UserEntity userEntity);
}
