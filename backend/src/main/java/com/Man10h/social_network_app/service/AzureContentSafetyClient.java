package com.Man10h.social_network_app.service;

import com.Man10h.social_network_app.model.dto.ImageModerationRequest;
import com.Man10h.social_network_app.model.dto.TextModerationRequest;
import com.Man10h.social_network_app.model.response.ModerationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "azureContentSafety",
        url = "${azure.endpoint}"
)
public interface AzureContentSafetyClient {

    @PostMapping("/contentsafety/text:analyze?api-version=2023-10-01")
    ModerationResponse analyzeText(
            @RequestHeader("Ocp-Apim-Subscription-Key") String apiKey,
            @RequestBody TextModerationRequest request
    );

    @PostMapping("/contentsafety/image:analyze?api-version=2023-10-01")
    ModerationResponse analyzeImage(
            @RequestHeader("Ocp-Apim-Subscription-Key") String apiKey,
            @RequestBody ImageModerationRequest request
    );
}
