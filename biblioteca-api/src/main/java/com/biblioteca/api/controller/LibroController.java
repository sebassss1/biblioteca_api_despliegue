package com.biblioteca.api.controller;

import com.biblioteca.api.dto.LibroDTO;
import com.biblioteca.api.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
@Tag(name = "Libros", description = "API para gestión de libros")
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    @Operation(summary = "Obtener todos los libros")
    public ResponseEntity<List<LibroDTO>> obtenerTodosLosLibros() {
        return ResponseEntity.ok(libroService.obtenerTodosLosLibros());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un libro por ID")
    public ResponseEntity<LibroDTO> obtenerLibroPorId(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerLibroPorId(id));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Obtener libros disponibles")
    public ResponseEntity<List<LibroDTO>> obtenerLibrosDisponibles() {
        return ResponseEntity.ok(libroService.obtenerLibrosDisponibles());
    }

    @GetMapping("/genero/{genero}")
    @Operation(summary = "Obtener libros por género")
    public ResponseEntity<List<LibroDTO>> obtenerLibrosPorGenero(@PathVariable String genero) {
        return ResponseEntity.ok(libroService.obtenerLibrosPorGenero(genero));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo libro")
    public ResponseEntity<LibroDTO> crearLibro(@Valid @RequestBody LibroDTO libroDTO) {
        LibroDTO nuevoLibro = libroService.crearLibro(libroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un libro existente")
    public ResponseEntity<LibroDTO> actualizarLibro(
            @PathVariable Long id,
            @Valid @RequestBody LibroDTO libroDTO) {
        return ResponseEntity.ok(libroService.actualizarLibro(id, libroDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un libro")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build();
    }
}
