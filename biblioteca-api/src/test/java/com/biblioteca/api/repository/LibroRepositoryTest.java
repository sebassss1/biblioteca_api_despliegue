package com.biblioteca.api.repository;

import com.biblioteca.api.entity.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LibroRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private LibroRepository libroRepository;

    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    void setUp() {
        libroRepository.deleteAll();

        libro1 = new Libro();
        libro1.setTitulo("1984");
        libro1.setIsbn("978-0451524935");
        libro1.setAutor("George Orwell");
        libro1.setAnioPublicacion(1949);
        libro1.setGenero("Distopía");
        libro1.setDisponible(true);

        libro2 = new Libro();
        libro2.setTitulo("El Quijote");
        libro2.setIsbn("978-8420412146");
        libro2.setAutor("Miguel de Cervantes");
        libro2.setAnioPublicacion(1605);
        libro2.setGenero("Novela");
        libro2.setDisponible(false);
    }

    @Test
    void testGuardarLibro() {
        // Act
        Libro libroGuardado = libroRepository.save(libro1);

        // Assert
        assertNotNull(libroGuardado.getId());
        assertEquals("1984", libroGuardado.getTitulo());
        assertEquals("George Orwell", libroGuardado.getAutor());
    }

    @Test
    void testBuscarPorId() {
        // Arrange
        Libro libroGuardado = libroRepository.save(libro1);

        // Act
        Optional<Libro> resultado = libroRepository.findById(libroGuardado.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("1984", resultado.get().getTitulo());
    }

    @Test
    void testBuscarPorDisponible() {
        // Arrange
        libroRepository.save(libro1);
        libroRepository.save(libro2);

        // Act
        List<Libro> disponibles = libroRepository.findByDisponible(true);

        // Assert
        assertEquals(1, disponibles.size());
        assertTrue(disponibles.get(0).getDisponible());
    }

    @Test
    void testBuscarPorGenero() {
        // Arrange
        libroRepository.save(libro1);
        libroRepository.save(libro2);

        // Act
        List<Libro> distopias = libroRepository.findByGenero("Distopía");

        // Assert
        assertEquals(1, distopias.size());
        assertEquals("1984", distopias.get(0).getTitulo());
    }

    @Test
    void testBuscarPorAutor() {
        // Arrange
        libroRepository.save(libro1);
        libroRepository.save(libro2);

        // Act
        List<Libro> librosOrwell = libroRepository.findByAutor("George Orwell");

        // Assert
        assertEquals(1, librosOrwell.size());
        assertEquals("1984", librosOrwell.get(0).getTitulo());
    }

    @Test
    void testBuscarPorIsbn() {
        // Arrange
        libroRepository.save(libro1);

        // Act
        Optional<Libro> resultado = libroRepository.findByIsbn("978-0451524935");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("1984", resultado.get().getTitulo());
    }

    @Test
    void testExistePorIsbn() {
        // Arrange
        libroRepository.save(libro1);

        // Act
        boolean existe = libroRepository.existsByIsbn("978-0451524935");
        boolean noExiste = libroRepository.existsByIsbn("978-9999999999");

        // Assert
        assertTrue(existe);
        assertFalse(noExiste);
    }

    @Test
    void testEliminarLibro() {
        // Arrange
        Libro libroGuardado = libroRepository.save(libro1);
        Long id = libroGuardado.getId();

        // Act
        libroRepository.deleteById(id);

        // Assert
        Optional<Libro> resultado = libroRepository.findById(id);
        assertFalse(resultado.isPresent());
    }
}
