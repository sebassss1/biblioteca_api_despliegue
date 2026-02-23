package com.biblioteca.api.service;

import com.biblioteca.api.dto.PerfilDTO;
import com.biblioteca.api.dto.UsuarioDTO;
import com.biblioteca.api.entity.Perfil;
import com.biblioteca.api.entity.Usuario;
import com.biblioteca.api.exception.BadRequestException;
import com.biblioteca.api.exception.ResourceNotFoundException;
import com.biblioteca.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return convertirADTO(usuario);
    }

    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new BadRequestException("Ya existe un usuario con el email: " + usuarioDTO.getEmail());
        }

        Usuario usuario = convertirAEntidad(usuarioDTO);
        usuario.setFechaRegistro(LocalDate.now());

        // Si viene perfil, establecer la relación bidireccional
        if (usuarioDTO.getPerfil() != null) {
            Perfil perfil = convertirPerfilAEntidad(usuarioDTO.getPerfil());
            perfil.setUsuario(usuario);
            usuario.setPerfil(perfil);
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertirADTO(usuarioGuardado);
    }

    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Validar email duplicado
        if (!usuario.getEmail().equals(usuarioDTO.getEmail()) && 
            usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new BadRequestException("Ya existe un usuario con el email: " + usuarioDTO.getEmail());
        }

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefono(usuarioDTO.getTelefono());

        // Actualizar perfil si viene
        if (usuarioDTO.getPerfil() != null) {
            if (usuario.getPerfil() == null) {
                Perfil nuevoPerfil = convertirPerfilAEntidad(usuarioDTO.getPerfil());
                nuevoPerfil.setUsuario(usuario);
                usuario.setPerfil(nuevoPerfil);
            } else {
                actualizarPerfil(usuario.getPerfil(), usuarioDTO.getPerfil());
            }
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirADTO(usuarioActualizado);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // Métodos auxiliares
    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setFechaRegistro(usuario.getFechaRegistro());

        if (usuario.getPerfil() != null) {
            dto.setPerfil(convertirPerfilADTO(usuario.getPerfil()));
        }

        return dto;
    }

    private Usuario convertirAEntidad(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        return usuario;
    }

    private PerfilDTO convertirPerfilADTO(Perfil perfil) {
        PerfilDTO dto = new PerfilDTO();
        dto.setId(perfil.getId());
        dto.setDireccion(perfil.getDireccion());
        dto.setCiudad(perfil.getCiudad());
        dto.setCodigoPostal(perfil.getCodigoPostal());
        dto.setPreferenciasLectura(perfil.getPreferenciasLectura());
        return dto;
    }

    private Perfil convertirPerfilAEntidad(PerfilDTO dto) {
        Perfil perfil = new Perfil();
        perfil.setDireccion(dto.getDireccion());
        perfil.setCiudad(dto.getCiudad());
        perfil.setCodigoPostal(dto.getCodigoPostal());
        perfil.setPreferenciasLectura(dto.getPreferenciasLectura());
        return perfil;
    }

    private void actualizarPerfil(Perfil perfil, PerfilDTO perfilDTO) {
        perfil.setDireccion(perfilDTO.getDireccion());
        perfil.setCiudad(perfilDTO.getCiudad());
        perfil.setCodigoPostal(perfilDTO.getCodigoPostal());
        perfil.setPreferenciasLectura(perfilDTO.getPreferenciasLectura());
    }
}
