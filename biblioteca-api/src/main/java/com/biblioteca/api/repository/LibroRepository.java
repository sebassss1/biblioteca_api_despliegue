package com.biblioteca.api.repository;

import com.biblioteca.api.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByDisponible(Boolean disponible);
    List<Libro> findByGenero(String genero);
    List<Libro> findByAutor(String autor);
    Optional<Libro> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
}
