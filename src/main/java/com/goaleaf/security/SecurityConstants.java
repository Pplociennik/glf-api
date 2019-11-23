package com.goaleaf.security;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final String PASSWORD_RECOVERY_SECRET = "shouldPracticeMemoryKey";
    public static final long PASSWORD_RECOVERY_SECRET_EXPIRATION_TIME = 900_000; // 15 minutes
    public static final long EXPIRATION_TIME = 86_400_000; // 1 day
//    public static final String TOKEN_PREFIX = "Bearer ";
//    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/register";
    public static final String SWAGGER_URL = "/swagger-ui.html#/";
}
