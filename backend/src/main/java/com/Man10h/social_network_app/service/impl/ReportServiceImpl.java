package com.Man10h.social_network_app.service.impl;

import com.Man10h.social_network_app.exception.exceptions.GlobalException;
import com.Man10h.social_network_app.exception.exceptions.NotFoundException;
import com.Man10h.social_network_app.exception.exceptions.UnauthorizedException;
import com.Man10h.social_network_app.exception.exceptions.UniqueConstraintException;
import com.Man10h.social_network_app.model.dto.ReportDTO;
import com.Man10h.social_network_app.model.entity.PostEntity;
import com.Man10h.social_network_app.model.entity.ReportEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.ReportResponse;
import com.Man10h.social_network_app.repository.PostRepository;
import com.Man10h.social_network_app.repository.ReportRepository;
import com.Man10h.social_network_app.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Lớp dịch vụ quản lý tính năng báo cáo (Tố cáo) bài viết vi phạm.
 * Cung cấp chức năng tạo báo cáo, xem danh sách báo cáo cho Admin và cho User.
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;

    public ReportResponse converted(ReportEntity reportEntity) {
        return ReportResponse.builder()
                .id(reportEntity.getId())
                .title(reportEntity.getTitle())
                .content(reportEntity.getContent())
                .postId(reportEntity.getPostEntity().getId())
                .userId(reportEntity.getUserEntity().getId())
                .build();
    }

    /**
     * Tạo một báo cáo vi phạm cho một bài viết.
     * @param postId ID của bài viết bị tố cáo.
     * @param userEntity Đối tượng người dùng thực hiện tố cáo.
     * @param reportDTO Dữ liệu tố cáo (tiêu đề, nội dung).
     */
    @Transactional
    public void createReport(String postId, UserEntity userEntity, ReportDTO reportDTO) {
        try{
            Optional<PostEntity> optionalPost = postRepository.findById(postId);
            if(optionalPost.isEmpty()) {
                throw new NotFoundException("Post not found");
            }
            PostEntity postEntity = optionalPost.get();

            ReportEntity reportEntity = ReportEntity.builder()
                    .title(reportDTO.getTitle())
                    .content(reportDTO.getContent())
                    .userEntity(userEntity)
                    .postEntity(postEntity)
                    .build();

            reportRepository.save(reportEntity);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintException(e.getMessage());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * Lấy danh sách toàn bộ các báo cáo (Dành cho Admin).
     * @param pageable Phân trang
     * @return Trang chứa danh sách ReportResponse
     */
    @Override
    public Page<ReportResponse> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable)
                .map(this::converted);
    }

    @Override
    public Page<ReportResponse> getUserReports(UserEntity userEntity, Pageable pageable) {
        return reportRepository.findByUserEntity(userEntity, pageable)
                .map(this::converted);
    }

    @Override
    public void deleteReport(Long reportId) {
        try{
            reportRepository.deleteById(reportId);
        }
        catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    @Override
    public ReportResponse findById(Long reportId) {
        return reportRepository.findById(reportId)
                .map(this::converted)
                .orElseThrow(() -> new NotFoundException("Report not found"));
    }

    @Override
    public Page<ReportResponse> findByUserEntity_Id(String userId, Pageable pageable) {
        return reportRepository.findByUserEntity_Id(userId, pageable)
                .map(this::converted);
    }

    @Override
    public Page<ReportResponse> findByPostEntity_Id(String postId, Pageable pageable) {
        return reportRepository.findByPostEntity_Id(postId, pageable)
                .map(this::converted);
    }

    /**
     * Xóa một báo cáo do chính người dùng tạo.
     * @param reportId ID của báo cáo.
     * @param userEntity Người dùng đang thao tác.
     */
    @Transactional
    public void deleteReportByUser(Long reportId, UserEntity userEntity) {
        Optional<ReportEntity> optional = reportRepository.findById(reportId);
        if(optional.isEmpty()) {
            throw new NotFoundException("Report not found");
        }
        ReportEntity reportEntity = optional.get();

        List<ReportEntity> reportEntityList = reportRepository.findByUserEntity(userEntity);
        if(!reportEntityList.contains(reportEntity)) {
            throw new UnauthorizedException("You are not allowed to delete this Report");
        }
        reportRepository.delete(reportEntity);
    }


}
