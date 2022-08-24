package com.metro.modasistencia.repositorio;

import com.metro.modasistencia.modelo.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroRepositorio extends JpaRepository<Registro, Integer> {

    List<Registro> findByFechaAndUsuario_Expediente(LocalDate fecha, Integer expediente);
}
