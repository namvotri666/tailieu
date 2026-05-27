package com.Man10h.social_network_app.service;

import com.Man10h.social_network_app.model.entity.ImageEntity;
import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    public void createImageWithUserEntity(MultipartFile image, UserEntity userEntity);
    public ImageEntity createImageWithPostEntity(MultipartFile image, PostEntity postEntity);
    public void deleteById(String id);
    public void deleteByUserEntity(UserEntity userEntity);
    public void deleteByPostEntity(PostEntity postEntity);
}

