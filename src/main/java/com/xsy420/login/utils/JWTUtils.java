package com.xsy420.login.utils;

import com.xsy420.login.domain.dto.JwtUserDTO;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@Slf4j
@Getter
public class JWTUtils {

    /**
     * 获取加密key
     * 默认使用 login
     */
    @Value("${jwt.secret-key:login}")
    private String key;
    @Value("${jwt.expiration}")
    private long expiration;

    public String createJwt(JwtUserDTO jwtUserDTO) {
        // 计算jwt过期时间
        long current = System.currentTimeMillis() + expiration;
        // 生产jwt token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        JwtBuilder jwtBuilder = Jwts.builder()
                .claim("userId", jwtUserDTO.getUserId())
                .claim("username", jwtUserDTO.getUsername())
                .claim("startTime", jwtUserDTO.getStartTime())
                .claim("endTime", jwtUserDTO.getEndTime())
                .setExpiration(new Date(current))
                .signWith(signatureAlgorithm, key);
        return jwtBuilder.compact();
    }

}
