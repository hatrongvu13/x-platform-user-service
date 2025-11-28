package com.xxx.user.service.utils.security.ecdsa;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EcdsaConfig {

    @Bean
    public ECKey ecKey() throws JOSEException {
        return new ECKeyGenerator(Curve.P_256)
                .keyID("jaxtony")
                .generate();
    }

    @Bean
    public JWKSet jwkSet() throws JOSEException {
        // Only expose PUBLIC key
        return new JWKSet(ecKey().toPublicJWK());
    }
}
