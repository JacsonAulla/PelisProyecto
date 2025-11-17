package com.Proyecto.Peliculas.dtos.Usuario;

import lombok.Data;

@Data
public class JwtLoginRespuestaDTO {

    private String tokenDeAcceso;
    private String tipoDeToken = "Bearer";

    public JwtLoginRespuestaDTO(String tokenDeAcceso) {
        this.tokenDeAcceso = tokenDeAcceso;
    }
}