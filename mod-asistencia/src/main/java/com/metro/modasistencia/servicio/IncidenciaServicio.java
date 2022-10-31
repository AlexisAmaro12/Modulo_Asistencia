package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Incidencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

//Interface para darle los servicios que tendra el modelo Incidencia
public interface IncidenciaServicio {

    //Listar todas las incidencias
    List<Incidencia> findAll();
    List<Incidencia> findAllByOrderByFecha();

    //Listar todas las incidencias utilizando un objeto Pageable para la paginacion
    Page<Incidencia> findAll(Pageable pageable);

    Page<Incidencia> findAllByOrderByFecha(Pageable pageable);

    //Buscar todas las incidencias de un usuario determinado, ademas que agrega paginacion
    Page<Incidencia> findByExpediente(Integer expediente, Pageable pageable);

    //Buscar un registro de incidencia especifico
    Incidencia findOne(Integer id);

    //Guardar un registro de incidencia
    void save(Incidencia incidencia);

    //Eliminar un registro de incidencia
    void delete(Integer id);
}
