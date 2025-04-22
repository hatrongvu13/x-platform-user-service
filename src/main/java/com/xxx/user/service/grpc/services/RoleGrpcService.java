package com.xxx.user.service.grpc.services;

import com.xxx.user.grpc.RoleGrpc;
import com.xxx.user.grpc.RoleGrpcResponse;
import com.xxx.user.grpc.RoleGrpcServiceGrpc;
import com.xxx.user.service.database.entity.RoleEntity;
import com.xxx.user.service.database.repository.RoleRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@GrpcAdvice
@RequiredArgsConstructor
public class RoleGrpcService extends RoleGrpcServiceGrpc.RoleGrpcServiceImplBase {
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public void findRole(RoleGrpc request, StreamObserver<RoleGrpcResponse> responseObserver) {
        RoleEntity roleEntity = roleRepository.findById(request.getRoleId()).orElse(null);
        if (Objects.isNull(roleEntity)) {
            roleEntity = roleRepository.findByCode(request.getCode()).orElse(null);
        }
        if (Objects.isNull(roleEntity)) {
            responseObserver.onCompleted();
            return;
        }
        responseObserver.onNext(RoleGrpcResponse
                .newBuilder()
                        .addData(RoleGrpc
                                .newBuilder()
                                .setRoleId(roleEntity.getId())
                                .setCode(roleEntity.getCode())
                                .setValue(roleEntity.getValue())
                                .build())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public StreamObserver<RoleGrpc> findRoleStream(StreamObserver<RoleGrpcResponse> responseObserver) {
        return new StreamObserver<RoleGrpc>() {

            @Override
            public void onNext(RoleGrpc roleGrpc) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }

    @Override
    @Transactional
    public void createRole(RoleGrpc request, StreamObserver<RoleGrpcResponse> responseObserver) {
        RoleEntity roleEntity = new RoleEntity();
        buildRoleGrpc(request, responseObserver, roleEntity);
    }

    private void buildRoleGrpc(RoleGrpc request, StreamObserver<RoleGrpcResponse> responseObserver, RoleEntity roleEntity) {
        roleEntity.setCode(request.getCode());
        roleEntity.setValue(request.getValue());
        roleRepository.save(roleEntity);
        responseObserver.onNext(RoleGrpcResponse
                .newBuilder()
                        .addData(RoleGrpc
                                .newBuilder()
                                .setRoleId(roleEntity.getId())
                                .setCode(roleEntity.getCode())
                                .setValue(roleEntity.getValue())
                                .build())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateRole(RoleGrpc request, StreamObserver<RoleGrpcResponse> responseObserver) {
        RoleEntity roleEntity = roleRepository.findById(request.getRoleId()).orElse(null);
        if (Objects.isNull(roleEntity)) {
            responseObserver.onError(new RuntimeException("Error when update role"));
            responseObserver.onCompleted();
            return;
        }
        buildRoleGrpc(request, responseObserver, roleEntity);
    }

    @Override
    public void deleteRole(RoleGrpc request, StreamObserver<RoleGrpcResponse> responseObserver) {
        RoleEntity roleEntity = roleRepository.findById(request.getRoleId()).orElse(null);
        if (Objects.isNull(roleEntity)) {
            responseObserver.onError(new RuntimeException("Error when update role"));
            responseObserver.onCompleted();
            return;
        }
        roleRepository.delete(roleEntity);
        responseObserver.onNext(RoleGrpcResponse
                .newBuilder()
                .addData(RoleGrpc
                        .newBuilder()
                        .setRoleId(roleEntity.getId())
                        .setCode(roleEntity.getCode())
                        .setValue(roleEntity.getValue())
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
