package com.xxx.user.service.utils.security.grpc;

import com.xxx.user.service.annotation.PublicGrpc;
import com.xxx.user.service.utils.security.TokenProvider;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GrpcServerJwtInterceptor implements ServerInterceptor {

    private final TokenProvider tokenProvider;
    private final ApplicationContext applicationContext;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String fullMethodName = serverCall.getMethodDescriptor().getFullMethodName();
        String[] split = StringUtils.split(fullMethodName, "/");
        String serviceName = split[0];
        String methodName = split[1];
        try {
            Object service = getGrpcServiceBeanByName(serviceName);
            if (Objects.nonNull(service)) {
                Method method = findGrpcMethod(service, methodName);

                if (Objects.nonNull(method) && (method.isAnnotationPresent(PublicGrpc.class)
                        || method.getDeclaringClass().isAnnotationPresent(PublicGrpc.class))) {
                    return serverCallHandler.startCall(serverCall, metadata);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        String token = metadata.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));
        if (StringUtils.isBlank(token) || !tokenProvider.validateToken(token.substring(7))) {
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Unauthenticated"), metadata);
            return new ServerCall.Listener<ReqT>() {
            };
        }
        return serverCallHandler.startCall(serverCall, metadata);
    }

    private Object getGrpcServiceBeanByName(String serviceName) {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(GrpcService.class);
        for (Object bean : beans.values()) {
            if (bean.getClass().getSimpleName().equals(serviceName)) {
                return bean;
            }
        }
        return null;
    }

    private Method findGrpcMethod(Object service, String methodName) {
//        for (Method method : service.getClass().getMethods()) {
//            if (method.getName().equals(serviceName)) {
//                return method;
//            }
//        }

        Class<?> actualClass = AopProxyUtils.ultimateTargetClass(service); // get implement class
        for (Method method : actualClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}
