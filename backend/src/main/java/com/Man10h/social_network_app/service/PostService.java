package com.Man10h.social_network_app.service;

import com.Man10h.social_network_app.model.dto.PostDTO;
import com.Man10h.social_network_app.model.dto.PostUpdateDTO;
import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    public PostResponse createPost(String userId, PostDTO postDTO, List<MultipartFile> images);
    public PostResponse getPostDetailById(String id);
    public PostResponse getPostById(String id);
    public Page<PostResponse> getFeedPosts(String userId, Pageable pageable);
    public Page<PostResponse> getUserPosts(String userId, Pageable pageable);
    public void deletePostById(String id);
    public void updatePost(String id, PostDTO postDTO);
    public Page<PostResponse> getAllPosts(Pageable pageable);
    public Page<PostResponse> findPostByTitle(String title, Pageable pageable);
    public void deletePost(String id, UserEntity userEntity);
    public Page<PostResponse> getWarningPosts(Pageable pageable);
}
