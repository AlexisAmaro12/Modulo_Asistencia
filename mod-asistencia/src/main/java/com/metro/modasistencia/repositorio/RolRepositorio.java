package com.metro.modasistencia.repositorio;

import com.metro.modasistencia.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

//Repositorio para nuestras acciones CRUD del modelo Rol
public interface RolRepositorio extends JpaRepository<Rol, Long> {
}
