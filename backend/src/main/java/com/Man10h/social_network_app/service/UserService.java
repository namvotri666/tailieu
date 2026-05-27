package com.Man10h.social_network_app.service;

import com.Man10h.social_network_app.model.dto.UserChangePasswordRequest;
import com.Man10h.social_network_app.model.dto.UserDTO;
import com.Man10h.social_network_app.model.dto.UserLoginDTO;
import com.Man10h.social_network_app.model.dto.UserRegisterDTO;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    public Boolean register(UserRegisterDTO userRegisterDTO);
    public String login(UserLoginDTO userLoginDTO);
    public String loginWithGoogle(String idToken);
    public Boolean updateProfile(String userId, UserDTO userDTO, MultipartFile image);
    public UserResponse getProfile(String userId);
    public void enableUser(String userId);
    public Page<UserResponse> getAllUsers(Pageable pageable);
    public boolean forgotPassword(String email);
    public Page<UserResponse> findUsersByNameAndEnabled(String name, Boolean enabled,Pageable pageable);
    public void changePassword(UserEntity userEntity, UserChangePasswordRequest request);
}
