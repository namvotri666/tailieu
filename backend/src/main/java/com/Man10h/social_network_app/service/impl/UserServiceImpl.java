package com.Man10h.social_network_app.service.impl;

import com.Man10h.social_network_app.exception.exceptions.AccountNotEnabledException;
import com.Man10h.social_network_app.exception.exceptions.InvalidCredentialsException;
import com.Man10h.social_network_app.exception.exceptions.NotFoundException;
import com.Man10h.social_network_app.exception.exceptions.UserAlreadyExistsException;
import com.Man10h.social_network_app.model.dto.UserChangePasswordRequest;
import com.Man10h.social_network_app.model.dto.UserDTO;
import com.Man10h.social_network_app.model.dto.UserLoginDTO;
import com.Man10h.social_network_app.model.dto.UserRegisterDTO;
import com.Man10h.social_network_app.model.entity.ImageEntity;
import com.Man10h.social_network_app.model.entity.RoleEntity;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.model.response.ImageResponse;
import com.Man10h.social_network_app.model.response.UserResponse;
import com.Man10h.social_network_app.repository.ImageRepository;
import com.Man10h.social_network_app.repository.RoleRepository;
import com.Man10h.social_network_app.repository.UserRepository;
import com.Man10h.social_network_app.service.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.channels.AcceptPendingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ImageService imageService;
    private final CloudinaryService cloudinaryService;
    private final MailService mailService;
    private final ImageRepository imageRepository;


    @Transactional
    public Boolean register(UserRegisterDTO userRegisterDTO) {
        Optional<UserEntity> optionalUsername = userRepository.findByUsername(userRegisterDTO.getUsername());
        if(optionalUsername.isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        Optional<UserEntity> optionalEmail = userRepository.findByEmail(userRegisterDTO.getEmail());
        if(optionalEmail.isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        Optional<RoleEntity> optionalRole = roleRepository.findById(2L);
        if(optionalRole.isEmpty()) {
            throw new NotFoundException("Role not found");
        }
        RoleEntity role = optionalRole.get();

        UserEntity userEntity = UserEntity.builder()
                .username(userRegisterDTO.getUsername())
                .email(userRegisterDTO.getEmail())
                .password(passwordEncoder.encode(userRegisterDTO.getPassword()))
                .firstName(userRegisterDTO.getFirstName())
                .lastName(userRegisterDTO.getLastName())
                .gender(userRegisterDTO.getGender())
                .roleEntity(role)
                .enabled(true)
                .build();
        userRepository.save(userEntity);

        send(userRegisterDTO.getEmail(), userEntity.getUsername(), ", Welcome!");
        return true;
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) {
        Optional<UserEntity> optionalUsername = userRepository.findByUsername(userLoginDTO.getUsername());
        if(optionalUsername.isEmpty()){
            throw new NotFoundException("User not found");
        }
        UserEntity userEntity = optionalUsername.get();
        if(!passwordEncoder.matches(userLoginDTO.getPassword(), userEntity.getPassword())) {
            throw new InvalidCredentialsException("Username or password is incorrect");
        }
        if(!userEntity.isEnabled()){
            throw new AccountNotEnabledException("Account is not enabled");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword(), userEntity.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return tokenService.generateToken(userEntity);
    }

    @Override
    public String loginWithGoogle(String idToken) {
        GoogleIdToken.Payload payload = tokenService.verifyIdToken(idToken);

        String email = payload.getEmail();
        String name = payload.getSubject();

        Optional<UserEntity> optionalEmail = userRepository.findByEmail(email);
        UserEntity userEntity;
        if(optionalEmail.isEmpty()){
            Optional<RoleEntity> optionalRole = roleRepository.findById(2L);
            if(optionalRole.isEmpty()) {
                return null;
            }
            RoleEntity role = optionalRole.get();

            userEntity = UserEntity.builder()
                    .username(email)
                    .email(email)
                    .password(null)
                    .firstName(name)
                    .lastName(name)
                    .gender("F")
                    .roleEntity(role)
                    .build();

            userRepository.save(userEntity);
        }
        else {
            userEntity = optionalEmail.get();
        }
        return tokenService.generateToken(userEntity);
    }

    @Transactional
    public Boolean updateProfile(String userId, UserDTO userDTO, MultipartFile file) {
        Optional<UserEntity> optional = userRepository.findById(userId);
        if(optional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        UserEntity userEntity = optional.get();
        if(!userDTO.getFirstName().isEmpty()){
            userEntity.setFirstName(userDTO.getFirstName());
        }
        if(!userDTO.getLastName().isEmpty()){
            userEntity.setLastName(userDTO.getLastName());
        }
        if(!userDTO.getGender().isEmpty()){
            userEntity.setGender(userDTO.getGender());
        }
        if(file != null && !file.isEmpty()){
            if(!userEntity.getImageEntityList().isEmpty()){
                imageService.deleteByUserEntity(userEntity);
            }
            imageService.createImageWithUserEntity(file, userEntity);
        }
        userRepository.save(userEntity);
        return true;
    }

    @Override
    public UserResponse getProfile(String userId) {
        Optional<UserEntity> optional = userRepository.getProfile(userId);
        if(optional.isEmpty()){
            throw new NotFoundException("User not found");
        }
        UserEntity userEntity = optional.get();

        return UserResponse.builder()
                .id(userEntity.getId())
                .image(userEntity.getImageEntityList().isEmpty() ?
                        null : ImageResponse.builder()
                        .url(userEntity.getImageEntityList().getFirst().getUrl())
                        .id(userEntity.getImageEntityList().getFirst().getId())
                        .build()
                )
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .build();
    }

    @Transactional
    public void enableUser(String userId) {
        Optional<UserEntity> optional = userRepository.findById(userId);
        if(optional.isEmpty()){
            throw new NotFoundException("User not found");
        }
        UserEntity userEntity = optional.get();
        userEntity.setEnabled(!userEntity.isEnabled());
        userRepository.save(userEntity);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {

        Page<UserEntity> userEntityPage = userRepository.getAllUsers(pageable);
//        List<UserEntity> userEntityList = userEntityPage.getContent();
//        userEntityList.forEach(u -> u.getImageEntityList().size());

        return userEntityPage.map(userEntity ->
                UserResponse.builder()
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
                        .build()
        );
    }

    @Transactional
    public boolean forgotPassword(String email) {
        Optional<UserEntity> optionalEmail = userRepository.findByEmail(email);
        if(optionalEmail.isEmpty()){
            throw new NotFoundException("User not found");
        }
        UserEntity user = optionalEmail.get();
        if(!user.isEnabled()){
            throw new AccountNotEnabledException("Account is not enabled");
        }
        String password = generateCode();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        send(email, "New Password: ", password);
        return true;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findUsersByNameAndEnabled(String name, Boolean enabled, Pageable pageable) {

        return userRepository.findUsersByNameAndEnabled(name, enabled, pageable).map(userEntity ->
                UserResponse.builder()
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
                        .build()
        );
    }

    @Transactional
    public void changePassword(UserEntity userEntity, UserChangePasswordRequest request) {
        if(!passwordEncoder.matches(request.oldPassword(), userEntity.getPassword())){
            throw new InvalidCredentialsException("Old password does not match");
        }
        userEntity.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(userEntity);
    }

    public String generateCode(){
        return new Random().nextInt(100000) + "";
    }

    public void send(String to, String subject, String content) {
        String html = "<html>" + subject + content + "</html>";
        mailService.sendMail(to, subject, html);
    }
}
