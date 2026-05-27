package com.Man10h.social_network_app.repository;

import com.Man10h.social_network_app.model.entity.ContentModerationEntity;
import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentModerationRepository extends JpaRepository<ContentModerationEntity, String> {
    void deleteByContentTypeAndPostEntity(ContentType contentType, PostEntity postEntity);
    void deleteByPostEntity(PostEntity postEntity);
}
