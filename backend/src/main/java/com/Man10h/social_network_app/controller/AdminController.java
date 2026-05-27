package com.Man10h.social_network_app.controller;

import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.PostResponse;
import com.Man10h.social_network_app.model.response.ReportResponse;
import com.Man10h.social_network_app.model.response.UserResponse;
import com.Man10h.social_network_app.service.PostService;
import com.Man10h.social_network_app.service.ReportService;
import com.Man10h.social_network_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller dành cho quản trị viên (Admin).
 * Cung cấp các API quản lý người dùng, bài viết và xử lý các báo cáo vi phạm.
 * Yêu cầu quyền (Role) ADMIN mới được phép truy cập.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "API ADMIN", description = "Secured by JWT and required role ADMIN")
public class AdminController {

    private final PostService postService;
    private final UserService userService;
    private final ReportService reportService;


    @GetMapping("/posts/{id}")
    @Operation(summary = "Get details of the post", description = "By id post")
    public ResponseEntity<PostResponse> getPost(@PathVariable("id")String id){
        return ResponseEntity.ok(postService.getPostDetailById(id));
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete post", description = "By id")
    public ResponseEntity<PostResponse> deletePost(@PathVariable("id") String id){
        postService.deletePostById(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/users/{id}/enabled")
    @Operation(summary = "Enable/ UnEnabled user", description = "Using user's id")
    public ResponseEntity<?> enabledUser(@PathVariable("id") String id){
        userService.enableUser(id);
        return ResponseEntity.ok().build();
    }


    /**
     * Lấy danh sách toàn bộ báo cáo vi phạm trên hệ thống.
     * @param page Số trang hiện tại
     * @param size Số lượng báo cáo trên một trang
     * @return Trang chứa danh sách các báo cáo
     */
    @GetMapping("/reports")
    @Operation(summary = "Get all reports")
    public ResponseEntity<Page<ReportResponse>> getAllReports( @RequestParam(value = "page") int page,
                                                               @RequestParam(value = "size") int size){
        return ResponseEntity.ok(reportService.getAllReports(PageRequest.of(page, size)));
    }

    @GetMapping("/reports/{id}")
    @Operation(summary = "Find report by id")
    public ResponseEntity<ReportResponse> findReportById(@PathVariable("id") Long id){
        return ResponseEntity.ok(reportService.findById(id));
    }

    @GetMapping("/posts/{id}/reports")
    @Operation(summary = "Get post's reports")
    public ResponseEntity<Page<ReportResponse>> findReportByPost(@PathVariable("id") String id,
                                                                 @RequestParam(value = "page") int page,
                                                                 @RequestParam(value = "size") int size){
        return ResponseEntity.ok(reportService.findByPostEntity_Id(id, PageRequest.of(page, size)));
    }

    @GetMapping("/users/{id}/reports")
    @Operation(summary = "Get user's reports")
    public ResponseEntity<Page<ReportResponse>> findReportByUser(@PathVariable("id") String id,
                                                                 @RequestParam(value = "page") int page,
                                                                 @RequestParam(value = "size") int size){
        return ResponseEntity.ok(reportService.findByUserEntity_Id(id, PageRequest.of(page, size)));
    }

    /**
     * Xóa một báo cáo vi phạm khỏi hệ thống sau khi đã xử lý xong.
     * @param id ID của báo cáo cần xóa
     */
    @DeleteMapping("/reports/{id}")
    @Operation(summary = "Delete report by id")
    public ResponseEntity<Void> deleteReport(@PathVariable("id") Long id){
        reportService.deleteReport(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/posts")
    @Operation(summary = "Get/Search posts")
    public ResponseEntity<Page<PostResponse>> getPosts(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        if (title != null && !title.isEmpty()) {
            return ResponseEntity.ok(postService.findPostByTitle(title, PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(postService.getAllPosts(PageRequest.of(page, size)));
    }

    @GetMapping("/users")
    @Operation(summary = "Get/Search users")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(userService.findUsersByNameAndEnabled(name, null, PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page, size)));
    }

    @GetMapping("/warning-posts")
    public ResponseEntity<Page<PostResponse>> getWarningPosts( @RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok(postService.getWarningPosts(PageRequest.of(page, size)));
    }
}
