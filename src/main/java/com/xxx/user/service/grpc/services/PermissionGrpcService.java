package com.xxx.user.service.grpc.services;

import com.xxx.user.grpc.PermissionGrpc;
import com.xxx.user.grpc.PermissionGrpcResponse;
import com.xxx.user.grpc.PermissionGrpcServiceGrpc;
import com.xxx.user.service.database.entity.PermissionEntity;
import com.xxx.user.service.database.repository.PermissionRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@RequiredArgsConstructor
public class PermissionGrpcService extends PermissionGrpcServiceGrpc.PermissionGrpcServiceImplBase {
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public void findPermission(PermissionGrpc request, StreamObserver<PermissionGrpcResponse> responseObserver) {
        PermissionEntity permissionEntity = permissionRepository.findById(request.getPermissionId()).orElse(null);
        if (permissionEntity == null) {
            permissionEntity = permissionRepository.findByCode(request.getCode()).orElse(null);
            if (permissionEntity == null) {
                responseObserver.onError(new RuntimeException("Error: Permission Not Found"));
                responseObserver.onCompleted();
                return;
            }
        }
        responseObserver.onNext(PermissionGrpcResponse
                .newBuilder()
                        .addData(PermissionGrpc
                                .newBuilder()
                                .setPermissionId(permissionEntity.getId())
                                .setCode(permissionEntity.getCode())
                                .setValue(permissionEntity.getValue())
                                .build())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public StreamObserver<PermissionGrpc> findPermissionStream(StreamObserver<PermissionGrpcResponse> responseObserver) {
        return new StreamObserver<PermissionGrpc>() {

            @Override
            public void onNext(PermissionGrpc permissionGrpc) {

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
    public void createPermission(PermissionGrpc request, StreamObserver<PermissionGrpcResponse> responseObserver) {
        PermissionEntity permissionEntity = new PermissionEntity();
        buildPermissionGrpc(request, responseObserver, permissionEntity);
    }

    @Override
    @Transactional
    public void updatePermission(PermissionGrpc request, StreamObserver<PermissionGrpcResponse> responseObserver) {
        PermissionEntity permissionEntity = permissionRepository.findById(request.getPermissionId()).orElse(null);
        if (permissionEntity == null) {
            responseObserver.onError(new RuntimeException("Error: Permission Not Found"));
            responseObserver.onCompleted();
            return;
        }
        buildPermissionGrpc(request, responseObserver, permissionEntity);
    }

    private void buildPermissionGrpc(PermissionGrpc request, StreamObserver<PermissionGrpcResponse> responseObserver, PermissionEntity permissionEntity) {
        permissionEntity.setCode(request.getCode());
        permissionEntity.setValue(request.getValue());
        permissionRepository.save(permissionEntity);
        responseObserver.onNext(PermissionGrpcResponse
                .newBuilder()
                .addData(PermissionGrpc
                        .newBuilder()
                        .setPermissionId(permissionEntity.getId())
                        .setCode(permissionEntity.getCode())
                        .setValue(permissionEntity.getValue())
                        .build())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void deletePermission(PermissionGrpc request, StreamObserver<PermissionGrpcResponse> responseObserver) {
        PermissionEntity permissionEntity = permissionRepository.findById(request.getPermissionId()).orElse(null);
        if (permissionEntity == null) {
            responseObserver.onError(new RuntimeException("Error: Permission Not Found"));
            responseObserver.onCompleted();
            return;
        }
        permissionRepository.delete(permissionEntity);
        responseObserver.onNext(PermissionGrpcResponse
                .newBuilder()
                .addData(PermissionGrpc
                        .newBuilder()
                        .setPermissionId(permissionEntity.getId())
                        .setCode(permissionEntity.getCode())
                        .setValue(permissionEntity.getValue())
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
