package com.biblioteca.api.service;

import com.biblioteca.api.dto.PrestamoDTO;
import com.biblioteca.api.entity.Libro;
import com.biblioteca.api.entity.Prestamo;
import com.biblioteca.api.entity.Prestamo.EstadoPrestamo;
import com.biblioteca.api.entity.Usuario;
import com.biblioteca.api.exception.BadRequestException;
import com.biblioteca.api.exception.ResourceNotFoundException;
import com.biblioteca.api.repository.LibroRepository;
import com.biblioteca.api.repository.PrestamoRepository;
import com.biblioteca.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    @Transactional(readOnly = true)
    public List<PrestamoDTO> obtenerTodosLosPrestamos() {
        return prestamoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrestamoDTO obtenerPrestamoPorId(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));
        return convertirADTO(prestamo);
    }

    @Transactional(readOnly = true)
    public List<PrestamoDTO> obtenerPrestamosActivosDeUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId);
        }
        return prestamoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoPrestamo.ACTIVO).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrestamoDTO crearPrestamo(PrestamoDTO prestamoDTO) {
        // Validar que existe el usuario
        Usuario usuario = usuarioRepository.findById(prestamoDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + prestamoDTO.getUsuarioId()));

        // Validar que existe el libro
        Libro libro = libroRepository.findById(prestamoDTO.getLibroId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + prestamoDTO.getLibroId()));

        // Validar que el libro está disponible
        if (!libro.getDisponible()) {
            throw new BadRequestException("El libro no está disponible para préstamo");
        }

        // Crear el préstamo
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        // Marcar el libro como no disponible
        libro.setDisponible(false);
        libroRepository.save(libro);

        Prestamo prestamoGuardado = prestamoRepository.save(prestamo);
        return convertirADTO(prestamoGuardado);
    }

    @Transactional
    public PrestamoDTO devolverLibro(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BadRequestException("Este préstamo ya ha sido devuelto");
        }

        // Actualizar el préstamo
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDate.now());

        // Marcar el libro como disponible
        Libro libro = prestamo.getLibro();
        libro.setDisponible(true);
        libroRepository.save(libro);

        Prestamo prestamoActualizado = prestamoRepository.save(prestamo);
        return convertirADTO(prestamoActualizado);
    }

    @Transactional
    public void eliminarPrestamo(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));

        // Si el préstamo está activo, devolver el libro antes de eliminar
        if (prestamo.getEstado() == EstadoPrestamo.ACTIVO) {
            Libro libro = prestamo.getLibro();
            libro.setDisponible(true);
            libroRepository.save(libro);
        }

        prestamoRepository.deleteById(id);
    }

    // Métodos auxiliares
    private PrestamoDTO convertirADTO(Prestamo prestamo) {
        PrestamoDTO dto = new PrestamoDTO();
        dto.setId(prestamo.getId());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaDevolucion(prestamo.getFechaDevolucion());
        dto.setEstado(prestamo.getEstado());
        dto.setUsuarioId(prestamo.getUsuario().getId());
        dto.setLibroId(prestamo.getLibro().getId());
        dto.setNombreUsuario(prestamo.getUsuario().getNombre());
        dto.setTituloLibro(prestamo.getLibro().getTitulo());
        return dto;
    }
}
