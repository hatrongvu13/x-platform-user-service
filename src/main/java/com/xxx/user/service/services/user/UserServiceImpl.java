package com.xxx.user.service.services.user;

import com.htv.proto.user.JwtGrpc;
import com.htv.proto.user.UserGrpc;
import com.htv.proto.user.UserLoginGrpc;
import com.htv.proto.user.UserRegisterGrpc;
import com.nimbusds.jose.JOSEException;
import com.xxx.user.service.data.jwt.Token;
import com.xxx.user.service.data.user.LoginRequest;
import com.xxx.user.service.data.user.User;
import com.xxx.user.service.database.entity.RoleEntity;
import com.xxx.user.service.database.entity.UserEntity;
import com.xxx.user.service.database.repository.RoleRepository;
import com.xxx.user.service.database.repository.UserRepository;
import com.xxx.user.service.services.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Object createUser(User user) throws JOSEException {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
        return new Token(tokenService.createToken(userEntity.getUsername(), userEntity.getEmail()));
    }

    /**
     *
     * @param login Dto LoginRequest
     * @return token
     * @throws JOSEException JwtException
     */
    @Override
    public Object login(LoginRequest login) throws JOSEException {
        UserEntity user = userRepository.findByUsername(login.username()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or password is incorrect"));
        if (!passwordEncoder.matches(login.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }
        return tokenService.createToken(user.getUsername(), user.getEmail());
    }

    /// GetUserInfoGrpc return null if not found
    ///
    /// @param username as String
    /// @param email as String
    /// @return UserGrpc
    @Override
    public UserGrpc getUserInfoGrpc(String username, String email) {
        UserEntity user = userRepository.findByUsernameOrEmail(username, email).orElse(null);
        if (user == null) {
            return UserGrpc.newBuilder().build();
        }
        return UserGrpc.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setFullName(user.getFullName())
                .build();
    }

    @Override
    public List<UserGrpc> getUsersInfoGrpc(List<String> usernames, List<String> emails) {
        List<UserEntity> userEntities = userRepository.findAllByUsernameInOrEmailIn(usernames, emails).orElse(new ArrayList<>());
        if (userEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userEntities.stream().map(item -> UserGrpc.newBuilder()
                .setId(item.getId())
                .setUsername(item.getUsername())
                .setEmail(item.getEmail())
                .setFullName(item.getFullName())
                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JwtGrpc registerUser(UserRegisterGrpc request) throws JOSEException {
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or email already in use");
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(request, userEntity);
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        RoleEntity roleEntity = roleRepository.findByCode("USER").orElse(null);
        if (roleEntity == null) {
            roleEntity = new RoleEntity();
            roleEntity.setCode("USER");
            roleEntity.setValue("USER");
        }
        userEntity.setRoles(Set.of(roleEntity));
        userRepository.save(userEntity);
        String token = tokenService.createToken(userEntity.getUsername(), userEntity.getEmail(), userEntity.getRoles().stream().map(RoleEntity::getCode).toList());

        return JwtGrpc.newBuilder().setMessage("Success").setToken(token).build();
    }

    @Override
    public JwtGrpc loginUser(UserLoginGrpc request) {

        return null;
    }
}
