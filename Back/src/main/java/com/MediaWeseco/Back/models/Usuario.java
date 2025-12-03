package com.MediaWeseco.Back.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.MediaWeseco.Back.enums.Provider;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(unique = true, length = 50)
    private String username;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String password;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_id")
    private String providerId;

    @Builder.Default
    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Builder.Default
    @Column(name = "esta_verificado", nullable = false)
    private Boolean estaVerificado = false;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();
}