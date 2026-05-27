package com.Man10h.social_network_app.service.impl;

import com.Man10h.social_network_app.exception.exceptions.GlobalException;
import com.Man10h.social_network_app.exception.exceptions.NotFoundException;
import com.Man10h.social_network_app.exception.exceptions.UnauthorizedException;
import com.Man10h.social_network_app.model.dto.PostDTO;
import com.Man10h.social_network_app.model.dto.PostUpdateDTO;
import com.Man10h.social_network_app.model.entity.*;
import com.Man10h.social_network_app.model.enums.ContentType;
import com.Man10h.social_network_app.model.response.*;
import com.Man10h.social_network_app.repository.*;
import com.Man10h.social_network_app.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageService imageService;
    private final ContentModerationService contentModerationService;
    private final ContentModerationRepository contentModerationRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final PostLikeRepository postLikeRepository;


    public PostResponse convertPostToPostResponse(PostEntity postEntity) {
        List<ImageResponse> imageResponseList = new ArrayList<>();
        for(ImageEntity imageEntity : postEntity.getImageEntityList()){
            ImageResponse imageResponse = ImageResponse.builder()
                    .url(imageEntity.getUrl())
                    .id(imageEntity.getId())
                    .build();
            imageResponseList.add(imageResponse);
        }
        UserEntity userEntity = postEntity.getUserEntity();
        UserResponse userResponse = UserResponse.builder()
                .id(userEntity.getId())
                .image(userEntity.getImageEntityList().isEmpty() ? null :
                        ImageResponse.builder()
                                .url(userEntity.getImageEntityList().getFirst().getUrl())
                                .id(userEntity.getImageEntityList().getFirst().getId())
                                .build()
                )
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .enabled(userEntity.isEnabled())
                .build();
        return PostResponse.builder()
                .images(imageResponseList)
                .title(postEntity.getTitle())
                .content(postEntity.getContent())
                .like(postEntity.getLikeCount())
                .userResponse(userResponse)
                .warning(postEntity.getWarning())
                .createDate(postEntity.getCreateDate())
                .id(postEntity.getId())
                .build();
    }

    @Transactional
    public PostResponse createPost(String userId, PostDTO postDTO, List<MultipartFile> images) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new NotFoundException("User not found");
        }
        UserEntity user = optionalUser.get();

        PostEntity post = PostEntity.builder()
                .title(postDTO.getTitle())
                .likeCount(0L)
                .createDate(new Date())
                .content(postDTO.getContent())
                .commentEntityList(new ArrayList<>())
                .userEntity(user)
                .warning(false)
                .createDate(new Date())
                .build();
        postRepository.save(post);
        //
        contentModerationService.moderatePost(post, images);
        List<ImageEntity> imageEntityList = new ArrayList<>();
        List<ImageResponse> imageResponseList = new ArrayList<>();
        if(images != null && !images.isEmpty()){
            for(MultipartFile file : images){
                ImageEntity imageEntity = imageService.createImageWithPostEntity(file, post);
                imageEntityList.add(imageEntity);

                ImageResponse imageResponse = ImageResponse.builder()
                        .url(imageEntity.getUrl())
                        .id(imageEntity.getId())
                        .build();
                imageResponseList.add(imageResponse);
            }
        }

        post.setImageEntityList(imageEntityList);
        postRepository.save(post);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .like(post.getLikeCount())
                .images(imageResponseList)
                .createDate(post.getCreateDate())
                .build();
    }

    @Override
    public PostResponse getPostDetailById(String id) {
        Optional<PostEntity> optionalPost = postRepository.getPostsWithImage(id);
        if(optionalPost.isEmpty()){
            throw new NotFoundException("Post not found");
        }
        PostEntity post = optionalPost.get();
        postRepository.getPostsWithComments(id);
        postRepository.getPostWithContentModeration(id);

        List<ImageResponse> imageResponseList = new ArrayList<>();
        for(ImageEntity imageEntity : post.getImageEntityList()){
            ImageResponse imageResponse = ImageResponse.builder()
                    .url(imageEntity.getUrl())
                    .id(imageEntity.getId())
                    .build();
            imageResponseList.add(imageResponse);
        }
        List<CommentResponse> commentResponseList = new ArrayList<>();
        for(CommentEntity commentEntity : post.getCommentEntityList()){
            CommentResponse commentResponse = CommentResponse.builder()
                    .id(commentEntity.getId())
                    .content(commentEntity.getContent())
                    .fullName(commentEntity.getFullName())
                    .url(commentEntity.getUrl())
                    .userId(commentEntity.getUserId())
                    .createDate(commentEntity.getCreateDate())
                    .build();
            commentResponseList.add(commentResponse);
        }

        List<ContentModerationResponse> contentModerationResponseList = new ArrayList<>();
        for(ContentModerationEntity contentModerationEntity: post.getContentModerationEntityList()){
            List<ContentModerationResultResponse> contentModerationResultResponseList = new ArrayList<>();
            for(ContentModerationResultEntity contentModerationResultEntity: contentModerationEntity.getContentModerationResultEntityList()){
                contentModerationResultResponseList.add(
                        ContentModerationResultResponse.builder()
                                .id(contentModerationResultEntity.getId())
                                .category(contentModerationResultEntity.getCategory())
                                .severity(contentModerationResultEntity.getSeverity())
                                .build()
                );
            }
            contentModerationResponseList.add(
                    ContentModerationResponse.builder()
                            .id(contentModerationEntity.getId())
                            .contentModerationResultResponseList(contentModerationResultResponseList)
                            .contentType(contentModerationEntity.getContentType().toString())
                            .createAt(contentModerationEntity.getCreateAt())
                            .provider(contentModerationEntity.getProvider())
                            .build()
            );
        }


        return PostResponse.builder()
                .images(imageResponseList)
                .title(post.getTitle())
                .content(post.getContent())
                .commentResponseList(commentResponseList)
                .like(post.getLikeCount())
                .id(post.getId())
                .moderationResponseList(contentModerationResponseList)
                .warning(post.getWarning())
                .createDate(post.getCreateDate())
                .build();
    }

    @Override
    public PostResponse getPostById(String id) {
        Optional<PostEntity> optionalPost = postRepository.getPostsWithImage(id);
        if(optionalPost.isEmpty()){
            throw new NotFoundException("Post not found");
        }
        PostEntity post = optionalPost.get();
        postRepository.getPostsWithComments(id);


        List<ImageResponse> imageResponseList = new ArrayList<>();
        for(ImageEntity imageEntity : post.getImageEntityList()){
            ImageResponse imageResponse = ImageResponse.builder()
                    .url(imageEntity.getUrl())
                    .id(imageEntity.getId())
                    .build();
            imageResponseList.add(imageResponse);
        }
        List<CommentResponse> commentResponseList = new ArrayList<>();
        for(CommentEntity commentEntity : post.getCommentEntityList()){
            CommentResponse commentResponse = CommentResponse.builder()
                    .id(commentEntity.getId())
                    .content(commentEntity.getContent())
                    .fullName(commentEntity.getFullName())
                    .url(commentEntity.getUrl())
                    .userId(commentEntity.getUserId())
                    .createDate(commentEntity.getCreateDate())
                    .build();
            commentResponseList.add(commentResponse);
        }

        return PostResponse.builder()
                .images(imageResponseList)
                .title(post.getTitle())
                .content(post.getContent())
                .commentResponseList(commentResponseList)
                .like(post.getLikeCount())
                .id(post.getId())
                .createDate(post.getCreateDate())
                .build();
    }

    @Override
    public Page<PostResponse> getFeedPosts(String userId, Pageable pageable) {
        return postRepository.getPosts(userId, pageable)
                .map(this::convertPostToPostResponse);
    }

    @Override
    public Page<PostResponse> getUserPosts(String userId, Pageable pageable) {
        return postRepository.findByUserEntity_Id(userId, pageable)
                .map(this::convertPostToPostResponse);
    }

    @Transactional
    public void deletePostById(String id) {
        Optional<PostEntity> optionalPost = postRepository.findById(id);
        if(optionalPost.isEmpty()){
            throw new NotFoundException("Post not found");
        }
        contentModerationRepository.deleteByPostEntity(optionalPost.get());
        imageService.deleteByPostEntity(optionalPost.get());
        commentRepository.deleteByPostEntity(optionalPost.get());
        reportRepository.deleteByPostEntity(optionalPost.get());
        postLikeRepository.deleteByPostEntity(optionalPost.get());

        postRepository.deleteById(id);
    }

    @Transactional
    public void updatePost(String id, PostDTO postDTO) {
        Optional<PostEntity> optionalPost = postRepository.findById(id);
        if(optionalPost.isEmpty()){
            throw new NotFoundException("Post not found");
        }
        PostEntity post = optionalPost.get();
        if(!postDTO.getContent().isEmpty()){
            post.setContent(postDTO.getContent());
        }
        if(!postDTO.getTitle().isEmpty()){
            post.setTitle(postDTO.getTitle());
        }
        postRepository.save(post);
        try{
            contentModerationRepository.deleteByContentTypeAndPostEntity(ContentType.TEXT, post);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
        contentModerationService.moderatePost(post, null);
    }

    @Override
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postRepository.getAllPosts(pageable)
                .map(this::convertPostToPostResponse);
    }

    @Override
    public Page<PostResponse> findPostByTitle(String title, Pageable pageable) {
        return postRepository.findByTitle(title, pageable)
                .map(this::convertPostToPostResponse);
    }

    @Transactional
    public void deletePost(String id, UserEntity userEntity) {
        Optional<PostEntity> optionalPost = postRepository.findById(id);
        if(optionalPost.isEmpty()){
            throw new NotFoundException("Post not found");
        }
        PostEntity postEntity = optionalPost.get();
        List<PostEntity> postEntityList = postRepository.findByUserEntity(userEntity);
        if(!postEntityList.contains(postEntity)){
            throw new UnauthorizedException("Account not authorized to delete this post");
        }
        contentModerationRepository.deleteByPostEntity(optionalPost.get());
        imageService.deleteByPostEntity(optionalPost.get());
        commentRepository.deleteByPostEntity(optionalPost.get());
        reportRepository.deleteByPostEntity(postEntity);
        postLikeRepository.deleteByPostEntity(postEntity);


        postRepository.deleteById(id);
    }

    @Override
    public Page<PostResponse> getWarningPosts(Pageable pageable) {
        return postRepository.findByWarning(true, pageable)
                .map(this::convertPostToPostResponse);
    }
}
