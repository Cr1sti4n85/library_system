package com.soracel.library_system.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    public static final String JWT_HEADER = "Authorization";

    @Value("${jwt.secret.key}")
    public static final String SECRET_KEY = "";

}
