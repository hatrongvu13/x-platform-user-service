package com.xxx.user.service.grpc.services;

import com.xxx.user.grpc.*;
import com.xxx.user.service.database.entity.UserEntity;
import com.xxx.user.service.database.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {
    private final UserRepository userRepository;
    @Override
    public void findUser(UserGrpcInput request, StreamObserver<UserGrpcResponse> responseObserver) {
        long currentPage = 1;
        long pageSize = 10;
        paginationUser(request, responseObserver, currentPage, pageSize);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<UserGrpcInput> findUsers(StreamObserver<UserGrpcResponse> responseObserver) {
        return new StreamObserver<UserGrpcInput>() {
            private final List<String> usernames = new ArrayList<>();
            private final List<String> emails = new ArrayList<>();
            private final long currentPage = 1;
            private final long pageSize = 10;

            @Override
            public void onNext(UserGrpcInput userGrpcInput) {
                if (!userGrpcInput.getUsernameList().isEmpty()) {
                    usernames.addAll(userGrpcInput.getUsernameList());
                }
                if (!userGrpcInput.getEmailList().isEmpty()) {
                    emails.addAll(userGrpcInput.getEmailList());
                }
                paginationUser(userGrpcInput, responseObserver, currentPage, pageSize);
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onNext(UserGrpcResponse.newBuilder().setMessage("Error" + throwable.getMessage()).build());
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                Pageable pageable = PageRequest.of((int) currentPage, (int) pageSize, Sort.Direction.DESC);
                List<UserEntity> userEntities = userRepository
                        .findAllByUsernameInOrEmailIn(
                                usernames,
                                emails,
                                pageable)
                        .orElse(null);
                if (CollectionUtils.isEmpty(userEntities)) {
                    responseObserver.onCompleted();
                    return;
                }
                bindingUserGrpc(responseObserver, userEntities, pageable);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void updateUser(UserGrpc request, StreamObserver<UserGrpcResponse> responseObserver) {
        UserEntity user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (Objects.nonNull(user)) {
            responseObserver.onNext(UserGrpcResponse.newBuilder().setMessage("Success").build());
        } else {
            responseObserver.onNext(UserGrpcResponse.newBuilder().setMessage("Error").build());
        }
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void deleteUser(UserGrpcInput request, StreamObserver<UserGrpcResponse> responseObserver) {

    }

    @Override
    public void registerUser(UserGrpcRegister request, StreamObserver<UserGrpcResponse> responseObserver) {
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            responseObserver.onNext(UserGrpcResponse.newBuilder().setMessage("Error").build());
        } else {
            UserEntity user = new UserEntity();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            userRepository.save(user);
            responseObserver.onNext(UserGrpcResponse.newBuilder().setMessage("Success").build());
        }
        responseObserver.onCompleted();
    }

    private void paginationUser(UserGrpcInput userInput, StreamObserver<UserGrpcResponse> responseObserver, long currentPage, long pageSize) {
        if (userInput.getCurrentPage() > 0) {
            currentPage = userInput.getCurrentPage();
        }
        if (userInput.getPageSize() > 0) {
            pageSize = userInput.getPageSize();
        }
        Pageable pageable = PageRequest.of((int) currentPage, (int) pageSize, Sort.Direction.DESC);
        List<UserEntity> userEntities = userRepository
                .findAllByUsernameInOrEmailIn(
                        userInput.getUsernameList(),
                        userInput.getEmailList(),
                        pageable)
                .orElse(null);

        if (CollectionUtils.isEmpty(userEntities)) {
            responseObserver.onCompleted();
            return;
        }

        bindingUserGrpc(responseObserver, userEntities, pageable);
    }

    private void bindingUserGrpc(StreamObserver<UserGrpcResponse> responseObserver, List<UserEntity> userEntities, Pageable pageable) {
        List<UserGrpc> userList = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            userList.add(UserGrpc
                    .newBuilder()
                    .setEmail(userEntity.getEmail())
                    .setUsername(userEntity.getUsername())
                    .setFullName(userEntity.getFullName())
                    .build());
        }
        responseObserver.onNext(UserGrpcResponse
                .newBuilder()
                .setMessage("Success")
                .addAllData(userList)
                .setCurrentPage(pageable.getPageNumber())
                .setTotalRecord((long) pageable.getPageNumber() * pageable.getPageSize())
                .build());
    }
}
