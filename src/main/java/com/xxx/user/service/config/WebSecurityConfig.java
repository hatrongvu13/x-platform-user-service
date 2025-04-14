package com.xxx.user.service.config;

import com.xxx.user.service.utils.security.TokenProvider;
import com.xxx.user.service.utils.security.jwt.JwtAuthentication;
import com.xxx.user.service.utils.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthentication authentication;
    private final SecurityPropertiesConfig securityPropertiesConfig;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtFilter customFilter = new JwtFilter(tokenProvider);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> corsConfigurationSource())
//                .cors(Customizer.withDefaults())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authentication))
                .authorizeHttpRequests(authentication ->
                        {
                            if (!CollectionUtils.isEmpty(securityPropertiesConfig.getPermitAll())) {
                                securityPropertiesConfig.getPermitAll().forEach(securityPermit -> authentication.requestMatchers(securityPermit).permitAll());
                            }
                            authentication.anyRequest().authenticated();
                        }

                )
                .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withUsername("demo").password("demo").roles("USER").build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        if (securityPropertiesConfig.getCrossOrigin().size() == 1) {
            configuration.setAllowedOrigins(securityPropertiesConfig.getCrossOrigin());
        } else {
            configuration.addAllowedOrigin(securityPropertiesConfig.crossOrigin.get(0));
        }
        configuration.setAllowedMethods(securityPropertiesConfig.getAllowedMethods());
        configuration.setAllowedHeaders(securityPropertiesConfig.getAllowedHeaders());
        configuration.setAllowCredentials(securityPropertiesConfig.credentials);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
