package com.Man10h.social_network_app.controller;

import com.Man10h.social_network_app.model.dto.UserLoginDTO;
import com.Man10h.social_network_app.model.dto.UserRegisterDTO;
import com.Man10h.social_network_app.model.entity.UserEntity;
import com.Man10h.social_network_app.service.TokenService;
import com.Man10h.social_network_app.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "API Home", description = "Without securing")
public class HomeController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/register")
    @Operation(summary = "Create new account", description = "Enter detail field to register new account")
    public ResponseEntity<?> register(@Validated @RequestBody UserRegisterDTO userRegisterDTO,
                                       BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.error(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return ResponseEntity.status(400).build();
        }
        else {
            userService.register(userRegisterDTO);
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Enter username/password to login")
    public ResponseEntity<?> login(@Validated @RequestBody UserLoginDTO userLoginDTO,
                                   BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.error(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return ResponseEntity.status(400).build();
        }
        else {
            String token = userService.login(userLoginDTO);
            if(token == null) {
                log.error("Token is null");
                return ResponseEntity.status(400).build();
            }
            return ResponseEntity.ok(token);
        }
    }

    @PostMapping("/google")
    @Operation(summary = "Google login", description = "Authorize google to login app")
    public ResponseEntity<?> loginWithGoogle(
            @RequestBody Map<String, Objects> request) {

        String idToken = request.get("idToken").toString();
        String token = userService.loginWithGoogle(idToken);
        if(token == null) {
            log.error("Token is empty");
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.ok(token);
    }

    @GetMapping("/forgot-password")
    @Operation(summary = "forgot password", description = "Receiver new password in gmail")
    public ResponseEntity<?> forgotPassword(@RequestParam(name = "email") String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok().build();
    }




}
