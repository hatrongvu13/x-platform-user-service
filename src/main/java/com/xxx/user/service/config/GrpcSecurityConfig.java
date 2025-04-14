package com.xxx.user.service.config;

import com.xxx.user.service.utils.security.grpc.GrpcClientJwtInterceptor;
import com.xxx.user.service.utils.security.grpc.GrpcServerJwtInterceptor;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.annotation.Nullable;

@Configuration
@RequiredArgsConstructor
public class GrpcSecurityConfig {

    private final GrpcClientJwtInterceptor grpcClientJwtInterceptor;
    private final GrpcServerJwtInterceptor grpcServerJwtInterceptor;

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return new GrpcAuthenticationReader() {
            @Nullable
            @Override
            public Authentication readAuthentication(ServerCall<?, ?> call, Metadata headers) throws AuthenticationException {
                return null;
            }
        };
    }

    @Bean
    public GlobalClientInterceptorConfigurer globalClientInterceptorConfigurer() {
        return registry -> registry.add(grpcClientJwtInterceptor);
    }

    @Bean
    public GlobalServerInterceptorConfigurer globalServerInterceptorConfigurer() {
        return registry -> registry.add(grpcServerJwtInterceptor);
    }
}
