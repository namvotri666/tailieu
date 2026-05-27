package com.Man10h.social_network_app.service;

import com.Man10h.social_network_app.model.dto.ReportDTO;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.ReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    public void createReport(String postId, UserEntity userEntity, ReportDTO reportDTO);
    public Page<ReportResponse> getAllReports(Pageable pageable);
    public Page<ReportResponse> getUserReports(UserEntity userEntity, Pageable pageable);
    public void deleteReport(Long reportId);
    public ReportResponse findById(Long reportId);
    public Page<ReportResponse> findByUserEntity_Id(String userId, Pageable pageable);
    public Page<ReportResponse> findByPostEntity_Id(String postId, Pageable pageable);
    public void deleteReportByUser(Long reportId, UserEntity userEntity);
}
