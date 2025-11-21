package com.xxx.user.service.grpc.services;

import com.htv.proto.user.*;
import com.xxx.user.service.annotation.PublicGrpc;
import com.xxx.user.service.services.user.UserService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {

    private final UserService userService;

    @Override
    public StreamObserver<UserGrpc> getUserInfo(StreamObserver<UserInfoResponseGrpc> responseObserver) {
        return new StreamObserver<>() {
            String username;
            String email;
            @Override
            public void onNext(UserGrpc userGrpc) {
                if (StringUtils.isBlank(userGrpc.getUsername()) && StringUtils.isBlank(userGrpc.getEmail())) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Username and Email is empty").asRuntimeException());
                    return;
                }
                username = userGrpc.getUsername();
                email = userGrpc.getEmail();
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                if (StringUtils.isBlank(username) && StringUtils.isBlank(email)) {
                    responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Username and Email is empty").asRuntimeException());
                    return;
                }
                UserGrpc userGrpc = userService.getUserInfoGrpc(username, email);

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
        return new StreamObserver<>() {

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
                if (CollectionUtils.isEmpty(usernames) || CollectionUtils.isEmpty(emails)) {
                    responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("User info username and email is empty").asRuntimeException());
                    return;
                }
                try {
                    List<UserGrpc> usersInfo = userService.getUsersInfoGrpc(usernames, emails);
                    responseObserver.onNext(UserInfoResponseGrpc.newBuilder()
                            .setMessage("User info grpc completed")
                            .setStatus("success")
                            .addAllData(usersInfo)
                            .build());
                } catch (Exception e) {
                    responseObserver.onError(e);
                }
                responseObserver.onCompleted();
            }
        };
    }

    @PublicGrpc
    @Override
    public void registerUser(UserRegisterGrpc request, StreamObserver<JwtGrpc> responseObserver) {
        if (StringUtils.isBlank(request.getUsername()) && StringUtils.isBlank(request.getEmail())) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("UserRegisterGrpc username and email is empty").asRuntimeException());
            return;
        }

        try {
            JwtGrpc jwtGrpc = userService.registerUser(request);
            responseObserver.onNext(jwtGrpc);
        } catch (Exception e) {
            responseObserver.onError(e);
            return;
        }
        responseObserver.onCompleted();
    }

    @PublicGrpc
    @Override
    public void loginUser(UserLoginGrpc request, StreamObserver<JwtGrpc> responseObserver) {
        if (StringUtils.isBlank(request.getUsername()) && StringUtils.isBlank(request.getEmail())) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("UserLoginGrpc username and email is required")
                    .asRuntimeException());
            return;
        }

        try {
            JwtGrpc jwtGrpc = userService.loginUser(request);
            responseObserver.onNext(jwtGrpc);
        } catch (Exception e) {
            responseObserver.onError(e);
            return;
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
