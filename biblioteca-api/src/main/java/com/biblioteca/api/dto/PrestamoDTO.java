package com.biblioteca.api.dto;

import com.biblioteca.api.entity.Prestamo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoDTO {
    private Long id;
    
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    
    @NotNull(message = "El estado es obligatorio")
    private Prestamo.EstadoPrestamo estado;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;
    
    @NotNull(message = "El ID del libro es obligatorio")
    private Long libroId;
    
    // Información adicional para la respuesta
    private String nombreUsuario;
    private String tituloLibro;
}
