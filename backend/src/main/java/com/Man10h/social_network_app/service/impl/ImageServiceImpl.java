package com.Man10h.social_network_app.service.impl;

import com.Man10h.social_network_app.exception.exceptions.NotFoundException;
import com.Man10h.social_network_app.model.entity.ImageEntity;
import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.repository.ImageRepository;
import com.Man10h.social_network_app.service.CloudinaryService;
import com.Man10h.social_network_app.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public void createImageWithUserEntity(MultipartFile image, UserEntity userEntity) {
        Map<String, Object> result = cloudinaryService.upload(image);
        ImageEntity imageEntity = ImageEntity.builder()
                .userEntity(userEntity)
                .url(result.get("url").toString())
                .build();
        imageRepository.save(imageEntity);
    }

    @Transactional
    public ImageEntity createImageWithPostEntity(MultipartFile image, PostEntity postEntity) {
        Map<String, Object> result = cloudinaryService.upload(image);
        ImageEntity imageEntity = ImageEntity.builder()
                .postEntity(postEntity)
                .url(result.get("url").toString())
                .build();
        imageRepository.save(imageEntity);
        return imageEntity;
    }

    @Transactional
    public void deleteById(String id) {
        Optional<ImageEntity> optional = imageRepository.findById(id);
        if(optional.isEmpty()){
            throw new NotFoundException("Not found image");
        }
        imageRepository.deleteById(id);
    }

    @Transactional
    public void deleteByUserEntity(UserEntity userEntity) {
        try{
            imageRepository.deleteByUserEntity(userEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteByPostEntity(PostEntity postEntity) {
        try{
            imageRepository.deleteByPostEntity(postEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
