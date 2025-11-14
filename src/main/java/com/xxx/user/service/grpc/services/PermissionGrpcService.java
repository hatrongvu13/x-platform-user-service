package com.xxx.user.service.grpc.services;

import com.htv.proto.user.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PermissionGrpcService extends PermissionGrpcServiceGrpc.PermissionGrpcServiceImplBase {
    @Override
    public void initPermission(PermissionGrpc request, StreamObserver<PermissionResponseGrpc> responseObserver) {
        super.initPermission(request, responseObserver);
    }

    @Override
    public void initPermissions(PermissionInitGrpc request, StreamObserver<PermissionResponseGrpc> responseObserver) {
        super.initPermissions(request, responseObserver);
    }

    @Override
    public void updatePermission(PermissionGrpc request, StreamObserver<PermissionResponseGrpc> responseObserver) {
        super.updatePermission(request, responseObserver);
    }

    @Override
    public void deletePermission(PermissionGrpc request, StreamObserver<PermissionResponseGrpc> responseObserver) {
        super.deletePermission(request, responseObserver);
    }

    @Override
    public void permissionByRole(PermissionByRoleGrpc request, StreamObserver<PermissionResponseGrpc> responseObserver) {
        super.permissionByRole(request, responseObserver);
    }
}
