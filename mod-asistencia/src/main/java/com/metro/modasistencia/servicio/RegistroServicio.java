package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Registro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RegistroServicio {

    List<Registro> findAll();

    Page<Registro> findAll(Pageable pageable);

    Registro findOne(Integer expediente);

    List<Registro> findByFechaAndExpediente(LocalDate fecha, Integer expediente);

    Integer countByFechaBetween(LocalDate fechasInicio, LocalDate fechaFinal);

    void save(Registro registro);

    void delete(Integer expediente);
}
