package com.xxx.user.service.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "security.authentication")
public class SecurityProperties {
    List<String> permitAll = new ArrayList<>();
    List<String> crossOrigin = new ArrayList<>();
    boolean credentials;
    List<String> allowedMethods = new ArrayList<>();
    List<String> allowedHeaders = new ArrayList<>();
}
