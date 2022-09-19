package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Registro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

//Interface para darle los servicios que tendra el modelo Registro
public interface RegistroServicio {

    //Listar todos los registros comunes
    List<Registro> findAll();

    //Listar todos los registros comunes utilizando paginacion
    Page<Registro> findAll(Pageable pageable);

    //Listar todos los registros de un usuario con paginacion
    Page<Registro> findByExpediente(Integer expediente, Pageable pageable);


    //Buscar un registro por su id
    Registro findOne(Integer id);

    //Buscar todos los registros por una fecha y un expediente especifico
    Registro findByFechaAndExpediente(LocalDate fecha, Integer expediente);

    //Contar los registros en un lapso de tiempo
    Integer countByFechaBetween(LocalDate fechasInicio, LocalDate fechaFinal);

    //Guardar un registro
    void save(Registro registro);

    //Eliminar un registro
    void delete(Integer expediente);
}
