package com.xxx.user.service.utils.security.jwt.ecdsa;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class EcdsaTokenProvider {

    private final ECKey ecKey;


    // ==========================
    // CREATE TOKEN (ES256)
    // ==========================
    public String createToken(String subject) throws Exception {

        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(3600)))
                .issuer("your-app")
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .keyID(ecKey.getKeyID())
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claims);

        JWSSigner signer = new ECDSASigner(ecKey.toECPrivateKey());

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    // ==========================
    // VALIDATE TOKEN (verify ES256)
    // ==========================
    public boolean validate(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            JWSVerifier verifier = new ECDSAVerifier(ecKey.toECPublicKey());

            return signedJWT.verify(verifier)
                    && signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    // ==========================
    // GET SUBJECT
    // ==========================
    public String getSubject(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}
