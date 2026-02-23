package com.biblioteca.api.service;

import com.biblioteca.api.dto.LibroDTO;
import com.biblioteca.api.entity.Libro;
import com.biblioteca.api.exception.BadRequestException;
import com.biblioteca.api.exception.ResourceNotFoundException;
import com.biblioteca.api.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    private Libro libro;
    private LibroDTO libroDTO;

    @BeforeEach
    void setUp() {
        libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Cien años de soledad");
        libro.setIsbn("978-0307474728");
        libro.setAutor("Gabriel García Márquez");
        libro.setAnioPublicacion(1967);
        libro.setGenero("Realismo mágico");
        libro.setDisponible(true);

        libroDTO = new LibroDTO();
        libroDTO.setTitulo("Cien años de soledad");
        libroDTO.setIsbn("978-0307474728");
        libroDTO.setAutor("Gabriel García Márquez");
        libroDTO.setAnioPublicacion(1967);
        libroDTO.setGenero("Realismo mágico");
        libroDTO.setDisponible(true);
    }

    @Test
    void testObtenerTodosLosLibros() {
        // Arrange
        List<Libro> libros = Arrays.asList(libro);
        when(libroRepository.findAll()).thenReturn(libros);

        // Act
        List<LibroDTO> resultado = libroService.obtenerTodosLosLibros();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Cien años de soledad", resultado.get(0).getTitulo());
        verify(libroRepository, times(1)).findAll();
    }

    @Test
    void testObtenerLibroPorId_Exitoso() {
        // Arrange
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

        // Act
        LibroDTO resultado = libroService.obtenerLibroPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Cien años de soledad", resultado.getTitulo());
        verify(libroRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerLibroPorId_NoEncontrado() {
        // Arrange
        when(libroRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            libroService.obtenerLibroPorId(999L);
        });
        verify(libroRepository, times(1)).findById(999L);
    }

    @Test
    void testCrearLibro_Exitoso() {
        // Arrange
        when(libroRepository.existsByIsbn(anyString())).thenReturn(false);
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        // Act
        LibroDTO resultado = libroService.crearLibro(libroDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Cien años de soledad", resultado.getTitulo());
        assertTrue(resultado.getDisponible());
        verify(libroRepository, times(1)).existsByIsbn(anyString());
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    void testCrearLibro_ISBNDuplicado() {
        // Arrange
        when(libroRepository.existsByIsbn(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            libroService.crearLibro(libroDTO);
        });
        verify(libroRepository, times(1)).existsByIsbn(anyString());
        verify(libroRepository, never()).save(any(Libro.class));
    }

    @Test
    void testActualizarLibro_Exitoso() {
        // Arrange
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(libroRepository.existsByIsbn(anyString())).thenReturn(false);
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        libroDTO.setId(1L);
        libroDTO.setTitulo("Título actualizado");

        // Act
        LibroDTO resultado = libroService.actualizarLibro(1L, libroDTO);

        // Assert
        assertNotNull(resultado);
        verify(libroRepository, times(1)).findById(1L);
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    void testEliminarLibro_Exitoso() {
        // Arrange
        when(libroRepository.existsById(1L)).thenReturn(true);
        doNothing().when(libroRepository).deleteById(1L);

        // Act
        libroService.eliminarLibro(1L);

        // Assert
        verify(libroRepository, times(1)).existsById(1L);
        verify(libroRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarLibro_NoEncontrado() {
        // Arrange
        when(libroRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            libroService.eliminarLibro(999L);
        });
        verify(libroRepository, times(1)).existsById(999L);
        verify(libroRepository, never()).deleteById(anyLong());
    }

    @Test
    void testObtenerLibrosDisponibles() {
        // Arrange
        List<Libro> librosDisponibles = Arrays.asList(libro);
        when(libroRepository.findByDisponible(true)).thenReturn(librosDisponibles);

        // Act
        List<LibroDTO> resultado = libroService.obtenerLibrosDisponibles();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getDisponible());
        verify(libroRepository, times(1)).findByDisponible(true);
    }
}
