package com.Man10h.social_network_app.util;

import com.Man10h.social_network_app.exception.exceptions.GlobalException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Component
public class FileUtil {
    public static String convertToBase64(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new GlobalException("Convert file failed");
        }
    }
}
