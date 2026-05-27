package com.Man10h.social_network_app.controller;

import com.Man10h.social_network_app.model.dto.*;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.*;
import com.Man10h.social_network_app.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller xử lý các yêu cầu (Requests) từ phía người dùng (User).
 * Bao gồm các tính năng tương tác mạng xã hội như: Thích, Bình luận, Theo dõi, Báo cáo vi phạm.
 * Yêu cầu người dùng phải đăng nhập và cung cấp JWT Token hợp lệ.
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "API User", description = "Secured by JWT and required role USER")
public class UserController {

    private final UserService userService;
    private final FollowerService followerService;
    private final PostService postService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final ReportService reportService;
    private final NotificationService notificationService;

    @GetMapping("/profile")
    @Operation(summary = "Get user's profile", description = "Using Bearer Token")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserEntity userEntity) {
        return ResponseEntity.ok(userService.getProfile(userEntity.getId()));
    }

    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update user's profile", description = "Using Bearer Token, UserDTO for details and multipart file for image")
    public ResponseEntity<Boolean> updateProfile(@ModelAttribute UserDTO userDTO,
                                                 @Parameter(hidden = true) @AuthenticationPrincipal UserEntity userEntity,
                                                 @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(userService.updateProfile(userEntity.getId(), userDTO, image));
    }



    @GetMapping("/follower-posts")
    @Operation(summary = "Get a page of feed post", description = "Using Bearer Token, pageNumber, size(10)")
    public ResponseEntity<Page<PostResponse>> getFeedPosts(@AuthenticationPrincipal UserEntity userEntity,
                                                       @RequestParam(name = "page") int page,
                                                       @RequestParam(name = "size") int size){
        return ResponseEntity.ok(postService.getFeedPosts(userEntity.getId(), PageRequest.of(page, size)));
    }

    @GetMapping("/user-posts")
    @Operation(summary = "Get a page of user's post", description = "Using Bearer Token, pageNumber, size(10)")
    public ResponseEntity<Page<PostResponse>> getUserPosts(@AuthenticationPrincipal UserEntity userEntity,
                                                           @RequestParam(name = "page") int page,
                                                           @RequestParam(name = "size") int size){
        return ResponseEntity.ok(postService.getUserPosts(userEntity.getId(), PageRequest.of(page, size)));
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get details of the post", description = "By id post")
    public ResponseEntity<PostResponse> getPost(@PathVariable("id")String id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping(value = "/user-posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new post", description = "Using Bearer Token, postDTO for details, multipart file for images")
    public ResponseEntity<PostResponse> createPost(@AuthenticationPrincipal UserEntity userEntity,
                                                   @ModelAttribute PostDTO postDTO,
                                                   @RequestPart(value = "images", required = false) List<MultipartFile> images){
        return ResponseEntity.ok(postService.createPost(userEntity.getId(), postDTO, images));
    }

    @DeleteMapping("/user-posts/{id}")
    @Operation(summary = "Delete post", description = "By id")
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal UserEntity userEntity,
                                        @PathVariable("id") String id){
        postService.deletePost(id, userEntity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user-posts/{id}")
    @Operation(summary = "Update post", description = "Using id post and postDTO for details")
    public ResponseEntity<?> updatePost(@PathVariable("id")String id,
                                        @RequestBody PostDTO postDTO){
        postService.updatePost(id, postDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * API thực hiện Like (Thích) hoặc Unlike (Bỏ thích) một bài viết.
     * @param userEntity Đối tượng người dùng (Lấy tự động từ SecurityContext nhờ JWT)
     * @param postId ID của bài viết
     * @return Mã trạng thái 200 OK nếu thành công
     */
    @PostMapping("/posts/{id}/like")
    @Operation(summary = "Like post", description = "Using id post and Bearer Token to like/unlike post")
    public ResponseEntity<?> likePost(@AuthenticationPrincipal UserEntity userEntity,
                                      @PathVariable("id") String postId){
        postLikeService.likePost(postId, userEntity.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * API tạo bình luận mới cho bài viết.
     * @param postId ID của bài viết
     * @param commentDTO Dữ liệu bình luận người dùng nhập
     * @param userEntity Người dùng đang đăng nhập
     * @return CommentResponse chứa thông tin bình luận vừa tạo
     */
    @PostMapping("/posts/{id}/comments")
    @Operation(summary = "Create comment", description = "Using id post, Bearer Token and commentDTO")
    public ResponseEntity<CommentResponse> createComment(@PathVariable("id")String postId,
                                                         @RequestBody CommentDTO commentDTO,
                                                         @AuthenticationPrincipal UserEntity userEntity){
        return ResponseEntity.ok(commentService.createComment(postId, userEntity.getId(), commentDTO));
    }

    @DeleteMapping("/comments/{id}")
    @Operation(summary = "delete comment", description = "By id")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal UserEntity userEntity,
                                           @PathVariable("id") String id){
        commentService.deleteCommentByUser(id, userEntity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers")
    @Operation(summary = "Get user's follower", description = "Using Bearer Token")
    public ResponseEntity<List<FollowerResponse>> getFollowers(@AuthenticationPrincipal UserEntity userEntity) {
        return ResponseEntity.ok(followerService.getFollowers(userEntity.getId()));
    }

    /**
     * API Theo dõi (Follow) hoặc Bỏ theo dõi (Unfollow) tài khoản khác.
     * @param followerId ID của tài khoản muốn theo dõi
     * @param userEntity Người dùng đang đăng nhập thực hiện thao tác
     * @return Mã trạng thái 200 OK
     */
    @PostMapping("/followers/{followerId}")
    @Operation(summary = "Follow User", description = "Using id follower(user) and Bearer Token to follow/unfollow")
    public ResponseEntity<?> follow(@PathVariable("followerId") String followerId, @AuthenticationPrincipal UserEntity userEntity){
        followerService.follow(followerId, userEntity.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    @Operation(summary = "Find user by name", description = "Find user by entering name")
    public ResponseEntity<Page<UserResponse>> findUsers(@RequestParam(value = "name", required = false) String name,
                                                        @RequestParam(value = "page") int page,
                                                        @RequestParam(value = "size") int size){
        return ResponseEntity.ok(userService.findUsersByNameAndEnabled(name, true, PageRequest.of(page, size)));
    }

    @GetMapping("/reports")
    @Operation(summary = "Get all user's reports", description = "Get all user's reports by authenticating")
    public ResponseEntity<Page<ReportResponse>> findReportByUser(@AuthenticationPrincipal UserEntity userEntity,
                                                                 @RequestParam(value = "page") int page,
                                                                 @RequestParam(value = "size") int size){
        return ResponseEntity.ok(reportService.getUserReports(userEntity, PageRequest.of(page, size)));
    }

    /**
     * API gửi báo cáo (Tố cáo) một bài viết vi phạm tiêu chuẩn cộng đồng.
     * @param postId ID bài viết bị tố cáo
     * @param userEntity Người dùng gửi đơn tố cáo
     * @param reportDTO Dữ liệu tố cáo (tiêu đề, chi tiết)
     */
    @PostMapping("/posts/{id}/reports")
    @Operation(summary = "Create reports", description = "Create reports by entering details")
    public ResponseEntity<Void> createReport(@PathVariable("id") String postId,
                                             @AuthenticationPrincipal UserEntity userEntity,
                                             @RequestBody ReportDTO reportDTO){
        reportService.createReport(postId, userEntity, reportDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reports/{id}")
    @Operation(summary = "Delete report", description = "Delete report by id")
    public ResponseEntity<Void> deleteReport(@AuthenticationPrincipal UserEntity userEntity,
                                             @PathVariable("id") Long id){
        reportService.deleteReportByUser(id, userEntity);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/users/{id}")
    @Operation(summary = "Get stranger's profile")
    public ResponseEntity<UserResponse> getStrangerProfile(@PathVariable("id") String id){
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @GetMapping("/users/{id}/posts")
    @Operation(summary = "Get stranger's posts")
    public ResponseEntity<Page<PostResponse>> getStrangerPosts(@PathVariable("id") String id,
                                                         @RequestParam(name = "page") int page,
                                                         @RequestParam(name = "size") int size){
        return ResponseEntity.ok(postService.getUserPosts(id, PageRequest.of(page, size)));
    }

    @GetMapping("/posts/{id}/user-like")
    @Operation(summary = "Do user like this post")
    public ResponseEntity<Boolean> doUserLike(@PathVariable("id") String id,
                                              @AuthenticationPrincipal UserEntity userEntity){
        return ResponseEntity.ok(postLikeService.doUserLikedPost(id, userEntity));
    }

    @GetMapping("/notifications")
    @Operation(summary = "Get all user's notifications")
    public ResponseEntity<Page<NotificationResponse>> getAllNotifications(@AuthenticationPrincipal UserEntity userEntity,
                                                                    @RequestParam(name = "page") int page,
                                                                    @RequestParam(name = "size") int size){
        return ResponseEntity.ok(notificationService.getAllNotifications(userEntity, PageRequest.of(page, size)));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Users change password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserEntity userEntity,
                                            @RequestBody UserChangePasswordRequest request){
        userService.changePassword(userEntity, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notifications/{id}")
    public ResponseEntity<?> readNotification(@AuthenticationPrincipal UserEntity userEntity,
                                              @PathVariable String id){
        notificationService.readNotification(id, userEntity);
        return ResponseEntity.ok().build();
    }
}
