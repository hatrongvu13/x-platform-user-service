package com.xxx.user.service.utils.security.grpc;

import com.xxx.user.service.utils.security.TokenProvider;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GrpcServerJwtInterceptor implements ServerInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String token = metadata.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));
        if (StringUtils.isBlank(token) || !tokenProvider.validateToken(token.substring(7)))
        {
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Unauthenticated"), metadata);
            return new ServerCall.Listener<ReqT>() {};
        }
        return serverCallHandler.startCall(serverCall, metadata);
    }
}
