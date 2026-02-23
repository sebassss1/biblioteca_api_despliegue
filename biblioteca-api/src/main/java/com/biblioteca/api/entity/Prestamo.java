package com.biblioteca.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPrestamo estado;

    // Relación N:1 con Usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación N:1 con Libro
    @ManyToOne
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    public enum EstadoPrestamo {
        ACTIVO,
        DEVUELTO
    }
}
