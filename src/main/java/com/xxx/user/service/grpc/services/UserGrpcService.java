package com.xxx.user.service.grpc.services;

import com.htv.proto.user.*;
import com.nimbusds.jose.JOSEException;
import com.xxx.user.service.database.entity.RoleEntity;
import com.xxx.user.service.database.entity.UserEntity;
import com.xxx.user.service.database.repository.UserRepository;
import com.xxx.user.service.services.role.RoleService;
import com.xxx.user.service.services.token.TokenService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    public StreamObserver<UserGrpc> getUserInfo(StreamObserver<UserInfoResponseGrpc> responseObserver) {
        return new StreamObserver<UserGrpc>() {
            UserEntity userEntity = null;

            @Override
            public void onNext(UserGrpc userGrpc) {
                if (StringUtils.isBlank(userGrpc.getUsername()) && StringUtils.isBlank(userGrpc.getEmail())) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Username and Email is empty").asRuntimeException());
                    return;
                }

                userEntity = userRepository.findByUsernameOrEmail(userGrpc.getUsername(), userGrpc.getEmail()).orElse(null);
                if (Objects.isNull(userEntity)) {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("User not found")
                            .asRuntimeException());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                if (Objects.isNull(userEntity)) {
                    return;
                }
                UserGrpc userGrpc = UserGrpc.newBuilder()
                        .setId(userEntity.getId())
                        .setUsername(userEntity.getUsername())
                        .setEmail(userEntity.getEmail())
                        .setFullName(userEntity.getFullName())
                        .build();
                UserInfoResponseGrpc userInfoResponseGrpc = UserInfoResponseGrpc.newBuilder()
                        .addData(userGrpc)
                        .build();
                responseObserver.onNext(userInfoResponseGrpc);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<UserInfoGrpc> getUsersInfo(StreamObserver<UserInfoResponseGrpc> responseObserver) {
        return new StreamObserver<UserInfoGrpc>() {

            final List<String> usernames = new ArrayList<>();
            final List<String> emails = new ArrayList<>();

            @Override
            public void onNext(UserInfoGrpc userInfoResponseGrpc) {
                if (userInfoResponseGrpc.getEmailList().isEmpty()
                        && userInfoResponseGrpc.getUsernameList().isEmpty()) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("User info username and email is empty")
                            .asRuntimeException());
                    return;
                }

                usernames.addAll(userInfoResponseGrpc.getUsernameList());
                emails.addAll(userInfoResponseGrpc.getEmailList());

            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                List<UserEntity> userEntities = userRepository.findAllByUsernameInOrEmailIn(usernames, emails).orElse(new ArrayList<>());
                if (CollectionUtils.isNotEmpty(userEntities)) {
                    responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
                    responseObserver.onCompleted();
                }
                List<UserGrpc> userGrpcs = userEntities.stream().map(userEntity ->
                        UserGrpc.newBuilder()
                                .setId(userEntity.getId())
                                .setFullName(userEntity.getFullName())
                                .setUsername(userEntity.getUsername())
                                .setEmail(userEntity.getEmail())
                                .build()).toList();
                responseObserver.onNext(UserInfoResponseGrpc.newBuilder()
                        .setMessage("User info grpc completed")
                        .setStatus("success")
                        .addAllData(userGrpcs)
                        .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void registerUser(UserRegisterGrpc request, StreamObserver<JwtGrpc> responseObserver) {
        if (StringUtils.isBlank(request.getUsername()) && StringUtils.isBlank(request.getEmail())) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("UserRegisterGrpc username and email is empty").asRuntimeException());
            return;
        }

        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("UserRegisterGrpc username and email already exist").asRuntimeException());
            return;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setEmail(request.getEmail());
        userEntity.setFullName(request.getFullName());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRoles(Set.of((roleService.findByRoleName("USER"))));
        userRepository.save(userEntity);

        try {
            responseObserver.onNext(JwtGrpc.newBuilder().setToken(tokenService.createToken(request.getUsername(), request.getEmail(), Collections.singletonList("USER"))).build());
        } catch (JOSEException e) {
            responseObserver.onError(e);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void loginUser(UserLoginGrpc request, StreamObserver<JwtGrpc> responseObserver) {
        if (StringUtils.isBlank(request.getUsername()) && StringUtils.isBlank(request.getEmail())) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("UserLoginGrpc username and email is required")
                    .asRuntimeException());
            return;
        }
        UserEntity userEntity = userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()).orElse(null);
        if (Objects.isNull(userEntity)) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
            return;
        }

        if (passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            try {
                responseObserver.onNext(JwtGrpc.newBuilder()
                        .setMessage("User login success")
                        .setToken(tokenService.createToken(userEntity.getUsername(),
                                userEntity.getEmail(),
                                userEntity.getRoles()
                                        .stream().map(RoleEntity::getCode)
                                        .toList()))
                        .build());
            } catch (JOSEException e) {
                responseObserver.onError(e);
            }
        }

        responseObserver.onCompleted();
    }

    @Override
    public void resetPassword(UserResetPasswordGrpc request, StreamObserver<JwtGrpc> responseObserver) {
        super.resetPassword(request, responseObserver);
    }

    @Override
    public void updateUser(UserGrpc request, StreamObserver<UserInfoResponseGrpc> responseObserver) {
        super.updateUser(request, responseObserver);
    }

    @Override
    public void deleteUser(UserGrpc request, StreamObserver<UserInfoResponseGrpc> responseObserver) {
        super.deleteUser(request, responseObserver);
    }
}
