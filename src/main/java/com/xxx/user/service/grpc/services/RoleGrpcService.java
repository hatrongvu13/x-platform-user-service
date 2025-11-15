package com.xxx.user.service.grpc.services;

import com.htv.proto.user.*;
import com.xxx.user.service.services.role.RoleService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RoleGrpcService extends RoleGrpcServiceGrpc.RoleGrpcServiceImplBase {

    private final RoleService roleService;

    @Override
    public void initRole(RoleGrpc request, StreamObserver<RoleResponseGrpc> responseObserver) {
        super.initRole(request, responseObserver);
    }

    @Override
    public void initRoles(InitRoleGrpc request, StreamObserver<RoleResponseGrpc> responseObserver) {
        super.initRoles(request, responseObserver);
    }

    @Override
    public void updateRole(RoleGrpc request, StreamObserver<RoleResponseGrpc> responseObserver) {
        super.updateRole(request, responseObserver);
    }

    @Override
    public void deleteRole(RoleGrpc request, StreamObserver<RoleResponseGrpc> responseObserver) {
        super.deleteRole(request, responseObserver);
    }

    @Override
    public void roleByUser(RoleByUserGrpc request, StreamObserver<RoleResponseGrpc> responseObserver) {
        try {
            List<RoleGrpc> roleGrpcs = roleService.getRoleByUsername(request.getUsernameList());
            responseObserver.onNext(RoleResponseGrpc.newBuilder().addAllData(roleGrpcs).build());
        } catch (Exception e) {
            responseObserver.onError(e);
        }
        responseObserver.onCompleted();
    }
}
