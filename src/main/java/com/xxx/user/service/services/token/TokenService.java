package com.xxx.user.service.services.token;

import java.util.List;

public interface TokenService {
    String createToken(String username, String email);
    String createToken(String username, String email, List<String> roles);
}
