package com.chatbot.services;
import com.chatbot.components.events.MailEvent;
import com.chatbot.components.exceptions.DataNotFoundException;
import com.chatbot.models.TokenEntity;
import com.chatbot.repositories.TokenRepository;
import com.chatbot.requests.SignInRequest;
import com.chatbot.requests.ForgotPasswordRequest;
import com.chatbot.requests.SignUpRequest;
import com.chatbot.responses.SignInResponse;
import com.chatbot.components.security.TokenProvider;
import com.chatbot.components.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.chatbot.components.exceptions.AppException;
import com.chatbot.models.UserEntity;
import com.chatbot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    AuthenticationManager authenticationManager;



    @Value("${app.fe.verify_url}")
    private String verifyUrl;

    @Value("${app.fe.forgot_password_url}")
    private String forgotPasswordUrl;
    @Override
    public void signUp(SignUpRequest request) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(request.getEmail());

        if(userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            if(user.getEmailVerified()){
                throw new AppException(HttpStatus.BAD_REQUEST,"Email address already in use.");
            }else{
                sendVerifyMail(user);
                return;
            }
        }
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);

        userRepository.save(user);

        sendVerifyMail(user);
    }

    @Override
    public void verify(Long userId, String token) {
        tokenProvider.validateToken(token);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with id : " + userId)
                );
        user.setEmailVerified(true);
        user.setIsActive(true);

        userRepository.save(user);
    }

    private void sendVerifyMail(UserEntity user) {
        String token = tokenProvider.createToken(user.getId(), 300000); // 5 minutes
        String urlPattern = verifyUrl + "?userId={0}&token={1}";
        String url = MessageFormat.format(urlPattern, user.getId(), token);
        applicationEventPublisher.publishEvent(new MailEvent(this, user, url, "verify"));
    }

    @Override
    public SignInResponse signIn(SignInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        if(!userPrincipal.getUser().getEmailVerified()){
            throw new AppException(HttpStatus.BAD_REQUEST,"Email not verified");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userPrincipal.getId())
                .fullName(userPrincipal.getUsername())
                .build();
    }

    @Override
    public String refresh(String token) {
        TokenEntity refreshToken = tokenRepository.findByName(token)
                .orElseThrow(() ->
                        new AppException(HttpStatus.BAD_REQUEST,"Refresh Token not found with token : " + token)
                );
        if(refreshToken.isRevoked()){
            throw new AppException(HttpStatus.BAD_REQUEST,"Token đã bị thu hồi");
        }
        if(refreshToken.isExpired()){
            throw new AppException(HttpStatus.BAD_REQUEST,"Token đã hết hạn");
        }
        if(refreshToken.getExpirationDate().isBefore(LocalDate.now())){
            refreshToken.setExpired(true);
            throw new AppException(HttpStatus.BAD_REQUEST,"Token đã hết hạn");
        }

        String accessToken = tokenProvider.createAccessToken(refreshToken.getUser().getId());

        return accessToken;
    }

    @Override
    public void sendMailForgotPassword(String email){
        UserEntity user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User", "Email", email));

        String token = tokenProvider.createToken(user.getId(), 600000); //10 minutes    `
        String urlPattern = forgotPasswordUrl + "?userId={0}&token={1}";
        String url = MessageFormat.format(urlPattern, user.getId(), token);
        applicationEventPublisher.publishEvent(new MailEvent(this, user, url, "forgot"));
    }

    @Override
    public void setPassword(Long userId, String token, ForgotPasswordRequest request) {
        tokenProvider.validateToken(token);
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(()
                        -> new DataNotFoundException("User", "id", userId));
        if(request.getPassword().equals(request.getConfirmedPassword())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        else{
            throw new AppException(HttpStatus.BAD_REQUEST, "Confirmed password is wrong");
        }
        userRepository.save(user);
    }

}
