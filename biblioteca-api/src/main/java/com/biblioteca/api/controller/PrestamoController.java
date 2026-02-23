package com.biblioteca.api.controller;

import com.biblioteca.api.dto.PrestamoDTO;
import com.biblioteca.api.service.PrestamoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
@Tag(name = "Préstamos", description = "API para gestión de préstamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    @GetMapping
    @Operation(summary = "Obtener todos los préstamos")
    public ResponseEntity<List<PrestamoDTO>> obtenerTodosLosPrestamos() {
        return ResponseEntity.ok(prestamoService.obtenerTodosLosPrestamos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un préstamo por ID")
    public ResponseEntity<PrestamoDTO> obtenerPrestamoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtenerPrestamoPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}/activos")
    @Operation(summary = "Obtener préstamos activos de un usuario")
    public ResponseEntity<List<PrestamoDTO>> obtenerPrestamosActivosDeUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(prestamoService.obtenerPrestamosActivosDeUsuario(usuarioId));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo préstamo")
    public ResponseEntity<PrestamoDTO> crearPrestamo(@Valid @RequestBody PrestamoDTO prestamoDTO) {
        PrestamoDTO nuevoPrestamo = prestamoService.crearPrestamo(prestamoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPrestamo);
    }

    @PutMapping("/{id}/devolver")
    @Operation(summary = "Devolver un libro prestado")
    public ResponseEntity<PrestamoDTO> devolverLibro(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.devolverLibro(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un préstamo")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        prestamoService.eliminarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}
