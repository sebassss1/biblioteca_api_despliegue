package com.biblioteca.api.service;

import com.biblioteca.api.dto.LibroDTO;
import com.biblioteca.api.entity.Libro;
import com.biblioteca.api.exception.BadRequestException;
import com.biblioteca.api.exception.ResourceNotFoundException;
import com.biblioteca.api.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    @Transactional(readOnly = true)
    public List<LibroDTO> obtenerTodosLosLibros() {
        return libroRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LibroDTO obtenerLibroPorId(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
        return convertirADTO(libro);
    }

    @Transactional(readOnly = true)
    public List<LibroDTO> obtenerLibrosDisponibles() {
        return libroRepository.findByDisponible(true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LibroDTO> obtenerLibrosPorGenero(String genero) {
        return libroRepository.findByGenero(genero).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LibroDTO crearLibro(LibroDTO libroDTO) {
        if (libroRepository.existsByIsbn(libroDTO.getIsbn())) {
            throw new BadRequestException("Ya existe un libro con el ISBN: " + libroDTO.getIsbn());
        }

        Libro libro = convertirAEntidad(libroDTO);
        libro.setDisponible(true);
        Libro libroGuardado = libroRepository.save(libro);
        return convertirADTO(libroGuardado);
    }

    @Transactional
    public LibroDTO actualizarLibro(Long id, LibroDTO libroDTO) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));

        // Validar que el ISBN no esté duplicado si se cambia
        if (!libro.getIsbn().equals(libroDTO.getIsbn()) && 
            libroRepository.existsByIsbn(libroDTO.getIsbn())) {
            throw new BadRequestException("Ya existe un libro con el ISBN: " + libroDTO.getIsbn());
        }

        libro.setTitulo(libroDTO.getTitulo());
        libro.setIsbn(libroDTO.getIsbn());
        libro.setAutor(libroDTO.getAutor());
        libro.setAnioPublicacion(libroDTO.getAnioPublicacion());
        libro.setGenero(libroDTO.getGenero());
        libro.setDisponible(libroDTO.getDisponible());

        Libro libroActualizado = libroRepository.save(libro);
        return convertirADTO(libroActualizado);
    }

    @Transactional
    public void eliminarLibro(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado con id: " + id);
        }
        libroRepository.deleteById(id);
    }

    // Métodos auxiliares de conversión
    private LibroDTO convertirADTO(Libro libro) {
        LibroDTO dto = new LibroDTO();
        dto.setId(libro.getId());
        dto.setTitulo(libro.getTitulo());
        dto.setIsbn(libro.getIsbn());
        dto.setAutor(libro.getAutor());
        dto.setAnioPublicacion(libro.getAnioPublicacion());
        dto.setGenero(libro.getGenero());
        dto.setDisponible(libro.getDisponible());
        return dto;
    }

    private Libro convertirAEntidad(LibroDTO dto) {
        Libro libro = new Libro();
        libro.setTitulo(dto.getTitulo());
        libro.setIsbn(dto.getIsbn());
        libro.setAutor(dto.getAutor());
        libro.setAnioPublicacion(dto.getAnioPublicacion());
        libro.setGenero(dto.getGenero());
        libro.setDisponible(dto.getDisponible());
        return libro;
    }
}
