package com.xxx.user.service.services.user;

import com.htv.proto.user.JwtGrpc;
import com.htv.proto.user.UserGrpc;
import com.htv.proto.user.UserLoginGrpc;
import com.htv.proto.user.UserRegisterGrpc;
import com.nimbusds.jose.JOSEException;
import com.xxx.user.service.data.user.LoginRequest;
import com.xxx.user.service.data.user.User;

import java.util.List;

public interface UserService {
    Object createUser(User user) throws JOSEException;

    Object login(LoginRequest login) throws JOSEException;

    UserGrpc getUserInfoGrpc(String username, String email);

    List<UserGrpc> getUsersInfoGrpc(List<String> usernames, List<String> emails);

    JwtGrpc registerUser(UserRegisterGrpc request) throws JOSEException;

    JwtGrpc loginUser(UserLoginGrpc request) throws JOSEException;
}
