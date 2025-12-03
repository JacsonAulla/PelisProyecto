package com.MediaWeseco.Back.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "canales")
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Canal extends Contenido {

    @Column(name = "url_stream")
    private String urlStream;

    @Column(name = "tipo_transmision")
    private String tipoTransmision; // "HLS" por defecto

    private String pais;
}