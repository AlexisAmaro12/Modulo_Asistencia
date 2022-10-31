package com.metro.modasistencia.repositorio;

import com.metro.modasistencia.modelo.Incidencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Repositorio para nuestras acciones CRUD del modelo Incidencia
public interface IncidenciaRepositorio extends JpaRepository<Incidencia, Integer> {

    //Metodo para utilizar la paginacion, dandole un objeto Pageable
    Page<Incidencia> findByUsuario_Expediente(Integer expediente, Pageable pageable);

    Page<Incidencia> findAllByOrderByEstado(Pageable pageable);

    List<Incidencia> findAllByOrderByFecha();
}
