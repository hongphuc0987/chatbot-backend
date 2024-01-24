package com.chatbot.controllers;

import com.chatbot.components.apis.CoreApiResponse;
import com.chatbot.components.configurations.AppProperties;
import com.chatbot.requests.ForgotPasswordRequest;
import com.chatbot.requests.SignInRequest;
import com.chatbot.requests.SignUpRequest;
import com.chatbot.responses.SignInResponse;
import com.chatbot.services.IUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("${app.api.version.v1}/user")
public class UserController {
    @Autowired
    private  AppProperties appProperties;

    @Autowired
    private  IUserService userService;

    @GetMapping("/verify")
    public CoreApiResponse<?> verify(
            @RequestParam Long userId,
            @RequestParam String token
    ) {
        userService.verify(userId,token);
        return CoreApiResponse.success("User verified successfully");
    }

    @PostMapping("/signup")
    public CoreApiResponse<?> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return CoreApiResponse.success("User registered successfully");
    }

    @PostMapping("/signin")
    public CoreApiResponse<SignInResponse> signin(@Valid @RequestBody SignInRequest request, HttpServletResponse response) {
        SignInResponse signIn = userService.signIn(request);
        Cookie cookie = new Cookie("refreshToken", signIn.getRefreshToken());

        // expires in 15 minutes
        cookie.setMaxAge(appProperties.getAuth().getRefreshTokenExpirationMsec());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return CoreApiResponse.success(signIn);
    }
    @GetMapping("/forgotpassword")
    public CoreApiResponse<?> forgotPassword(
            @RequestBody String email
    ){
        userService.sendMailForgotPassword(email);
        return CoreApiResponse.success("Check your mail");
    }

    @PutMapping("/setpassword")
    public CoreApiResponse<?> setPassword(
            @RequestParam("userId") Long userId,
            @RequestParam("token") String token,
            @RequestBody ForgotPasswordRequest request
    ){
        userService.setPassword(userId, token, request);
        return CoreApiResponse.success("Successfully!");
    }

}
