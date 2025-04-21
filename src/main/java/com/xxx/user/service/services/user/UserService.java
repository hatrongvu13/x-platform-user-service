package com.xxx.user.service.services.user;

import com.nimbusds.jose.JOSEException;
import com.xxx.user.service.data.user.LoginRequest;
import com.xxx.user.service.data.user.User;

public interface UserService {
    Object createUser(User user) throws JOSEException;

    Object login(LoginRequest login) throws JOSEException;
}
