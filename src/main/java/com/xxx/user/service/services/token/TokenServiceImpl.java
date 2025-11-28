package com.xxx.user.service.services.token;

import com.nimbusds.jose.JOSEException;
import com.xxx.user.service.data.user.UserAuthentication;
import com.xxx.user.service.utils.security.jwt.rsa.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenProvider tokenProvider;
    @Override
    public String createToken(String username, String email) throws JOSEException {
        return createToken(username, email, List.of());
    }

    @Override
    public String createToken(String username, String email, List<String> roles) throws JOSEException {
        Set<SimpleGrantedAuthority> authorities = createAuthorities(roles);
        return tokenProvider.createToken(new UserAuthentication(null, username, email, authorities), false);
    }

    /**
     * Parse Authority from Roles
     * @param roles
     * @return
     */
    private Set<SimpleGrantedAuthority> createAuthorities(List<String> roles) {
        if (CollectionUtils.isNotEmpty(roles)) {
            return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }
}
