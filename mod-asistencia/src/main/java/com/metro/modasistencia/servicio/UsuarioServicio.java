package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsuarioServicio {

    List<Usuario> findAll();

    Page<Usuario> findAll(Pageable pageable);

    Usuario findOne(Integer id);

    Usuario findByExpedienteAndPassword(Integer expediente, String password);

    Integer countByEstado();

    void save(Usuario usuario);

    void delete(Integer id);
}
