package com.metro.modasistencia.repositorio;

import com.metro.modasistencia.modelo.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidenciaRepositorio extends JpaRepository<Incidencia, Integer> {
}
