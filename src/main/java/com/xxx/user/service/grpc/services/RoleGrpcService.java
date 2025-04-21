package com.xxx.user.service.grpc.services;

import com.xxx.user.grpc.RoleGrpc;
import com.xxx.user.grpc.RoleGrpcResponse;
import com.xxx.user.grpc.RoleGrpcServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.advice.GrpcAdvice;

@GrpcAdvice
public class RoleGrpcService extends RoleGrpcServiceGrpc.RoleGrpcServiceImplBase {
    @Override
    public void findRole(RoleGrpc request, StreamObserver<RoleGrpcResponse> responseObserver) {
        super.findRole(request, responseObserver);
    }
}
