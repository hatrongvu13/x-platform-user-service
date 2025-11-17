package com.xxx.user.service.grpc.services;

import com.htv.proto.user.*;
import com.xxx.user.service.services.role.RoleService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
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
            List<RoleGrpc> roleGrpcs = roleService.getRoleByUsername(request.getUsername());
            responseObserver.onNext(RoleResponseGrpc.newBuilder().addAllData(roleGrpcs).build());
        } catch (Exception e) {
            responseObserver.onError(e);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<RoleByUsersGrpc> rolesByUser(StreamObserver<RolesByUserResponseGrpc> responseObserver) {
        return new StreamObserver<>() {
            private final List<String> usernames = new ArrayList<>();
            @Override
            public void onNext(RoleByUsersGrpc roleByUsersGrpc) {
                if (CollectionUtils.isEmpty(roleByUsersGrpc.getUsernamesList())) {
                    responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Empty usernames list").asException());
                }
                usernames.addAll(roleByUsersGrpc.getUsernamesList());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                try {
                    responseObserver.onNext(RolesByUserResponseGrpc.newBuilder()
                                    .setMessage("Success")
                                    .addAllData(roleService.getRolesByUsername(usernames))
                            .build());
                } catch (Exception e) {
                    responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asException());
                }
                responseObserver.onCompleted();
            }
        };
    }
}
