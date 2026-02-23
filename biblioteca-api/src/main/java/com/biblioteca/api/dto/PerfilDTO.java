package com.biblioteca.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilDTO {
    private Long id;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;
    
    @NotBlank(message = "El código postal es obligatorio")
    private String codigoPostal;
    
    private String preferenciasLectura;
}
