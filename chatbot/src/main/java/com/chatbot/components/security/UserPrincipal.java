package com.chatbot.components.security;

import com.chatbot.models.UserEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
@Data
@Getter
@Setter
@NoArgsConstructor
public class UserPrincipal implements UserDetails {
    private UserEntity user;

    public UserPrincipal(UserEntity user) {
        this.user = user;
    }

    public static UserPrincipal create(UserEntity user) {
        return new UserPrincipal(user);
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return an empty list since we are not managing roles
        return Collections.emptyList();
    }
}
