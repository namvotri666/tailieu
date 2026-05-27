package com.Man10h.social_network_app.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    public Map<String, Object> upload(MultipartFile file);
}