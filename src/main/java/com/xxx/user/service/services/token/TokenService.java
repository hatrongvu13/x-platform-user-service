package com.xxx.user.service.services.token;

import com.nimbusds.jose.JOSEException;

import java.util.List;

public interface TokenService {
    String createToken(String username, String email) throws JOSEException;
    String createToken(String username, String email, List<String> roles) throws JOSEException;
}
