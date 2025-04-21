package com.xxx.user.service.grpc.services;

import com.xxx.user.grpc.PermissionGrpc;
import com.xxx.user.grpc.PermissionGrpcResponse;
import com.xxx.user.grpc.PermissionGrpcServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PermissionGrpcService extends PermissionGrpcServiceGrpc.PermissionGrpcServiceImplBase {
    @Override
    public void findPermission(PermissionGrpc request, StreamObserver<PermissionGrpcResponse> responseObserver) {
        super.findPermission(request, responseObserver);
    }
}
