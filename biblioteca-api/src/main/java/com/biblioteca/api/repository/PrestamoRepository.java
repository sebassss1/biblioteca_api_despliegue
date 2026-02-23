package com.biblioteca.api.repository;

import com.biblioteca.api.entity.Prestamo;
import com.biblioteca.api.entity.Prestamo.EstadoPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByUsuarioId(Long usuarioId);
    List<Prestamo> findByLibroId(Long libroId);
    List<Prestamo> findByEstado(EstadoPrestamo estado);
    List<Prestamo> findByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);
}
