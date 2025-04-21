package com.xxx.user.service.services.user;

import com.nimbusds.jose.JOSEException;
import com.xxx.user.service.data.jwt.Token;
import com.xxx.user.service.data.user.LoginRequest;
import com.xxx.user.service.data.user.User;
import com.xxx.user.service.database.entity.UserEntity;
import com.xxx.user.service.database.repository.UserRepository;
import com.xxx.user.service.services.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    @Transactional
    public Object createUser(User user) throws JOSEException {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
        return new Token(tokenService.createToken(userEntity.getUsername(), userEntity.getEmail()));
    }

    @Override
    public Object login(LoginRequest login) throws JOSEException {
        UserEntity user = userRepository.findByUsername(login.username()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or password is incorrect"));
        if (!passwordEncoder.matches(login.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }
        return tokenService.createToken(user.getUsername(), user.getEmail());
    }
}
