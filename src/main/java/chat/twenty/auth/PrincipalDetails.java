package chat.twenty.auth;

import chat.twenty.domain.User;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Getter
@RequiredArgsConstructor
/**
 * SpringSecurity 일반 유저와 OAuth2 유저 정보를 모두 담을수 있는 클래스.
 * Authentication - PrincipalDetails - Member
 */
public class PrincipalDetails implements UserDetails {

    private final User user;
    
    // OAuth2User.getAttributes() 로 받은 정보
    private final Map<String, Object> attributes;
    private final Long id;

    public PrincipalDetails(User user) {
        this.user = user;
        this.attributes = null;
        this.id = user.getId();
    }

    // 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collection;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getEmail() { return "Email";}

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

}
