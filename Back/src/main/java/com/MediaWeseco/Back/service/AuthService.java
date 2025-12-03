package com.MediaWeseco.Back.service;

import com.MediaWeseco.Back.dtos.AuthResponse;
import com.MediaWeseco.Back.dtos.GoogleCompleteDto;
import com.MediaWeseco.Back.dtos.LoginDto;
import com.MediaWeseco.Back.dtos.RegisterInitDto;
import com.MediaWeseco.Back.dtos.SetPasswordDto;
import com.MediaWeseco.Back.dtos.VerifyCodeDto;

public interface AuthService {

    // Paso 1: Registro Inicial (Guarda datos y genera código)
    String registerInit(RegisterInitDto dto);

    // Paso 2: Verificar el código del correo
    void verifyCode(VerifyCodeDto dto);

    // Paso 3: Crear contraseña final
    void setPassword(SetPasswordDto dto);

    // Paso Extra: Completar perfil de Google
    AuthResponse completeGoogleProfile(GoogleCompleteDto dto);

    // Login: Devuelve el Token JWT (String)
    AuthResponse login(LoginDto dto);

    // Devuelve el JWT si existe, o lanza una excepción especial si es nuevo
    AuthResponse loginWithGoogle(String googleToken);
}