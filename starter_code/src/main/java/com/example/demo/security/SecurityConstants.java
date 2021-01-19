package com.example.demo.security;

public class SecurityConstants {
    public static final String HEADER_STRING = "Authorization";
    public static final String SECRET = "VoiciMonSecret";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final long EXPIRATION_TIME = 864_000_000; //10 days in millisecond

    public static final String SIGN_UP_URL = "/api/user/create";
}
