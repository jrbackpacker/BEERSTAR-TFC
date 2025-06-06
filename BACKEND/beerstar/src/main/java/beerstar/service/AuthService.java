package beerstar.service;

import beerstar.dto.AuthResponse;
import beerstar.dto.LoginRequest;
import beerstar.dto.RegisterRequest; // Si vas a tener registro público
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AuthService {
    Logger logger = LoggerFactory.getLogger(AuthService.class);
    AuthResponse authenticateUser(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest); // Si necesitas esta función
}