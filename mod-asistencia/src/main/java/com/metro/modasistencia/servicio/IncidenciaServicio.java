package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Incidencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IncidenciaServicio {

    List<Incidencia> findAll();

    Page<Incidencia> findAll(Pageable pageable);

    Incidencia findOne(Integer id);

    void save(Incidencia incidencia);

    void delete(Integer id);
}
