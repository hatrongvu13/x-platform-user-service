package com.xxx.user.service.utils.security.grpc;

import com.xxx.user.service.utils.security.SecurityUtils;
import io.grpc.*;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientJwtInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + SecurityUtils.getToken());
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(responseListener) {}, headers);
            }
        };
    }
}
