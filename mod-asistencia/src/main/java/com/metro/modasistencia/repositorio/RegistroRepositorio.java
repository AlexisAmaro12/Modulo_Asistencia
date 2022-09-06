package com.metro.modasistencia.repositorio;

import com.metro.modasistencia.modelo.Registro;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;



public interface RegistroRepositorio extends JpaRepository<Registro, Integer> {

    Registro findByFechaAndUsuario_Expediente(LocalDate fecha, Integer expediente);
    Integer countAllByFechaBetween(LocalDate fechaInicio, LocalDate fechaTermino);
}
