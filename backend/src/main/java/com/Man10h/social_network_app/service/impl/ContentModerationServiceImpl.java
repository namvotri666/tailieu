package com.Man10h.social_network_app.service.impl;

import com.Man10h.social_network_app.model.dto.ImageModerationRequest;
import com.Man10h.social_network_app.model.dto.TextModerationRequest;
import com.Man10h.social_network_app.model.entity.ContentModerationEntity;
import com.Man10h.social_network_app.model.entity.ContentModerationResultEntity;
import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.enums.ContentType;
import com.Man10h.social_network_app.model.response.ModerationResponse;
import com.Man10h.social_network_app.repository.ContentModerationRepository;
import com.Man10h.social_network_app.repository.ContentModerationResultRepository;
import com.Man10h.social_network_app.repository.PostRepository;
import com.Man10h.social_network_app.service.AzureContentSafetyClient;
import com.Man10h.social_network_app.service.ContentModerationService;
import com.Man10h.social_network_app.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentModerationServiceImpl implements ContentModerationService {
    private final PostRepository postRepository;
    @Value("${azure.api_key}")
    private String apiKey;

    private final AzureContentSafetyClient client;
    private final ContentModerationRepository contentModerationRepository;
    private final ContentModerationResultRepository contentModerationResultRepository;

    @Transactional
    @Async("asyncExecutor")
    public void moderatePost(PostEntity postEntity, List<MultipartFile> images) {
        ContentModerationEntity textModerationEntity = ContentModerationEntity.builder()
                .contentType(ContentType.TEXT)
                .createAt(LocalDateTime.now())
                .provider("AZURE")
                .postEntity(postEntity)
                .build();
        contentModerationRepository.save(textModerationEntity);

        ModerationResponse textRes = client.analyzeText(apiKey,
                new TextModerationRequest(postEntity.getContent()));

        boolean isTextGood = true;
        for(ModerationResponse.CategoryAnalysis categoryAnalysis: textRes.getCategoriesAnalysis()) {
            if(categoryAnalysis.getSeverity() > 0){
                ContentModerationResultEntity contentModerationResultEntity = ContentModerationResultEntity.builder()
                        .contentModerationEntity(textModerationEntity)
                        .category(categoryAnalysis.getCategory())
                        .severity(categoryAnalysis.getSeverity())
                        .build();
                contentModerationResultRepository.save(contentModerationResultEntity);
                postEntity.setWarning(true);
                isTextGood = false;
            }
        }
        if(isTextGood){
            contentModerationRepository.delete(textModerationEntity);
        }

        if(images != null && !images.isEmpty()) {

            ContentModerationEntity imageModerationEntity = ContentModerationEntity.builder()
                    .contentType(ContentType.IMAGE)
                    .createAt(LocalDateTime.now())
                    .provider("AZURE")
                    .postEntity(postEntity)
                    .build();
            contentModerationRepository.save(imageModerationEntity);


            for(MultipartFile image: images){
                String base64 = FileUtil.convertToBase64(image);
                boolean isImageGood = true;
                ImageModerationRequest req = new ImageModerationRequest();
                ImageModerationRequest.ImageContent img = new ImageModerationRequest.ImageContent();
                img.setContent(base64);
                req.setImage(img);

                ModerationResponse imgRes = client.analyzeImage(apiKey, req);

                for(ModerationResponse.CategoryAnalysis categoryAnalysis: imgRes.getCategoriesAnalysis()) {
                    if(categoryAnalysis.getSeverity() > 0){
                        isImageGood = false;
                        ContentModerationResultEntity contentModerationResultEntity = ContentModerationResultEntity.builder()
                                .contentModerationEntity(imageModerationEntity)
                                .category(categoryAnalysis.getCategory())
                                .severity(categoryAnalysis.getSeverity())
                                .build();
                        contentModerationResultRepository.save(contentModerationResultEntity);
                        postEntity.setWarning(true);
                    }

                }
                if(isImageGood){
                    contentModerationRepository.delete(imageModerationEntity);
                }
            }
        }



        postRepository.save(postEntity);


    }
}
