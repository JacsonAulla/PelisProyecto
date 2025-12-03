package com.MediaWeseco.Back.service;

import com.MediaWeseco.Back.dtos.UserResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    // 1. Listar todos (Para la tabla)
    Page<UserResponseDto> getAllUsers(Pageable pageable);

    // 2. Cambiar estado (Banear/Desbanear)
    UserResponseDto toggleUserStatus(Long userId);

    // 3. Cambiar Rol (Ascender/Degradar)
    UserResponseDto changeUserRole(Long userId, String newRoleName);
}