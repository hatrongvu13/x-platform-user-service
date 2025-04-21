package com.xxx.user.service.utils.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.xxx.user.service.data.user.UserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final byte[] secretBytes;
    private final long tokenValidityInMilliseconds;
    private final long tokenValidityInMillisecondsForRememberMe;

    public TokenProvider(@Value("${security.authentication.jwt.secret}") String secret,
                         @Value("${security.authentication.jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds,
                         @Value("${security.authentication.jwt.token-validity-in-seconds-for-remember-me}") long tokenValidityInMillisecondsForRememberMe) {
        this.secret = secret;
        this.secretBytes = !ObjectUtils.isEmpty(secret) ? Base64.getDecoder().decode(secret) : secret.getBytes(StandardCharsets.UTF_8);
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 1000;
        this.tokenValidityInMillisecondsForRememberMe = tokenValidityInMillisecondsForRememberMe * 1000;
    }

    public String createToken(UserAuthentication authentication, boolean rememberMe) throws JOSEException {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe * 1000);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds * 1000);
        }
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .expirationTime(validity)
                .build();

        JWSSigner signer = new MACSigner(secretBytes);
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256), claimsSet
        );
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public Authentication getAuthentication(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        List<GrantedAuthority> authorities = Arrays.stream(
                        Optional.ofNullable(claims.getStringClaim(AUTHORITIES_KEY)).orElse("").split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(authToken);
            JWSVerifier verifier = new MACVerifier(this.secretBytes);
            return signedJWT.verify(verifier) &&
                    signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
