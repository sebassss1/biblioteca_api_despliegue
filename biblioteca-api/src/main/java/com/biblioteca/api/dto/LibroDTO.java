package com.biblioteca.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibroDTO {
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    
    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;
    
    @NotBlank(message = "El autor es obligatorio")
    private String autor;
    
    @Min(value = 1000, message = "El año de publicación debe ser válido")
    private Integer anioPublicacion;
    
    @NotBlank(message = "El género es obligatorio")
    private String genero;
    
    @NotNull(message = "El estado de disponibilidad es obligatorio")
    private Boolean disponible;
}
