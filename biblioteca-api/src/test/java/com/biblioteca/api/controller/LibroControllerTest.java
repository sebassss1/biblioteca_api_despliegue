package com.biblioteca.api.controller;

import com.biblioteca.api.dto.LibroDTO;
import com.biblioteca.api.service.LibroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibroController.class)
class LibroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LibroService libroService;

    private LibroDTO libroDTO;

    @BeforeEach
    void setUp() {
        libroDTO = new LibroDTO();
        libroDTO.setId(1L);
        libroDTO.setTitulo("Don Quijote de la Mancha");
        libroDTO.setIsbn("978-8420412146");
        libroDTO.setAutor("Miguel de Cervantes");
        libroDTO.setAnioPublicacion(1605);
        libroDTO.setGenero("Novela");
        libroDTO.setDisponible(true);
    }

    @Test
    void testObtenerTodosLosLibros() throws Exception {
        // Arrange
        List<LibroDTO> libros = Arrays.asList(libroDTO);
        when(libroService.obtenerTodosLosLibros()).thenReturn(libros);

        // Act & Assert
        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].titulo").value("Don Quijote de la Mancha"))
                .andExpect(jsonPath("$[0].autor").value("Miguel de Cervantes"));

        verify(libroService, times(1)).obtenerTodosLosLibros();
    }

    @Test
    void testObtenerLibroPorId() throws Exception {
        // Arrange
        when(libroService.obtenerLibroPorId(1L)).thenReturn(libroDTO);

        // Act & Assert
        mockMvc.perform(get("/api/libros/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Don Quijote de la Mancha"));

        verify(libroService, times(1)).obtenerLibroPorId(1L);
    }

    @Test
    void testCrearLibro() throws Exception {
        // Arrange
        when(libroService.crearLibro(any(LibroDTO.class))).thenReturn(libroDTO);

        // Act & Assert
        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Don Quijote de la Mancha"))
                .andExpect(jsonPath("$.isbn").value("978-8420412146"));

        verify(libroService, times(1)).crearLibro(any(LibroDTO.class));
    }

    @Test
    void testCrearLibro_DatosInvalidos() throws Exception {
        // Arrange
        LibroDTO libroInvalido = new LibroDTO();
        libroInvalido.setTitulo(""); // Título vacío - inválido

        // Act & Assert
        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroInvalido)))
                .andExpect(status().isBadRequest());

        verify(libroService, never()).crearLibro(any(LibroDTO.class));
    }

    @Test
    void testActualizarLibro() throws Exception {
        // Arrange
        when(libroService.actualizarLibro(anyLong(), any(LibroDTO.class))).thenReturn(libroDTO);

        // Act & Assert
        mockMvc.perform(put("/api/libros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Don Quijote de la Mancha"));

        verify(libroService, times(1)).actualizarLibro(anyLong(), any(LibroDTO.class));
    }

    @Test
    void testEliminarLibro() throws Exception {
        // Arrange
        doNothing().when(libroService).eliminarLibro(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/libros/1"))
                .andExpect(status().isNoContent());

        verify(libroService, times(1)).eliminarLibro(1L);
    }

    @Test
    void testObtenerLibrosDisponibles() throws Exception {
        // Arrange
        List<LibroDTO> librosDisponibles = Arrays.asList(libroDTO);
        when(libroService.obtenerLibrosDisponibles()).thenReturn(librosDisponibles);

        // Act & Assert
        mockMvc.perform(get("/api/libros/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].disponible").value(true));

        verify(libroService, times(1)).obtenerLibrosDisponibles();
    }

    @Test
    void testObtenerLibrosPorGenero() throws Exception {
        // Arrange
        List<LibroDTO> libros = Arrays.asList(libroDTO);
        when(libroService.obtenerLibrosPorGenero("Novela")).thenReturn(libros);

        // Act & Assert
        mockMvc.perform(get("/api/libros/genero/Novela"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].genero").value("Novela"));

        verify(libroService, times(1)).obtenerLibrosPorGenero("Novela");
    }
}
