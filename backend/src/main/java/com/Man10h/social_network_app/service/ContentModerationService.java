package com.Man10h.social_network_app.service;

import com.Man10h.social_network_app.model.entity.PostEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContentModerationService {
    public void moderatePost(PostEntity postEntity, List<MultipartFile> images);
}
