package com.goaleaf.services;

public interface JwtService {

    public boolean Validate(String token, String secret);
}
