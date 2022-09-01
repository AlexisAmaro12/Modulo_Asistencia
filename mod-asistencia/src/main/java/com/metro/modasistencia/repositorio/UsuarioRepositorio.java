package com.metro.modasistencia.repositorio;

import com.metro.modasistencia.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    Usuario findByExpedienteAndPassword(Integer expediente, String password);

    Integer countByEstadoEquals(String estado);
}
