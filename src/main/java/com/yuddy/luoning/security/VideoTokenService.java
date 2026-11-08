package com.yuddy.luoning.security;

import com.yuddy.luoning.exception.ApiException;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;

// Token ngan han chi dung de xem video cua mot bai giang.
// The <video> khong gui duoc header Authorization nen token nam tren URL.
@Service
public class VideoTokenService {

    public static final String PURPOSE_CLAIM = "purpose";
    private static final String PURPOSE_VIDEO = "video";
    private static final String LESSON_CLAIM = "lessonId";
    private static final Duration EXPIRATION = Duration.ofHours(2);

    private final JwtEncoder jwtEncoder;
    private final NimbusJwtDecoder videoDecoder;

    public VideoTokenService(JwtEncoder jwtEncoder, SecretKey jwtSecretKey) {
        this.jwtEncoder = jwtEncoder;

        // Chi chap nhan token co purpose = video
        OAuth2TokenValidator<Jwt> isVideoToken = jwt -> PURPOSE_VIDEO.equals(jwt.getClaimAsString(PURPOSE_CLAIM))
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Khong phai token video", null));

        this.videoDecoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        this.videoDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(), isVideoToken));
    }

    public String createToken(Long userId, Long lessonId) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("luoning")
                .issuedAt(now)
                .expiresAt(now.plus(EXPIRATION))
                .subject(userId.toString())
                .claim(PURPOSE_CLAIM, PURPOSE_VIDEO)
                .claim(LESSON_CLAIM, lessonId.toString())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    // Token sai, het han, hoac cua bai khac thi tu choi
    public void verify(String token, Long lessonId) {
        Jwt jwt;
        try {
            jwt = videoDecoder.decode(token);
        } catch (JwtException e) {
            throw ApiException.forbidden("Link video khong hop le hoac da het han");
        }
        if (!lessonId.toString().equals(jwt.getClaimAsString(LESSON_CLAIM))) {
            throw ApiException.forbidden("Link video khong danh cho bai giang nay");
        }
    }
}
