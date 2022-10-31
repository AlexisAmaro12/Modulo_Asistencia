package com.metro.modasistencia.repositorio;

import com.metro.modasistencia.modelo.Registro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.util.List;


//Repositorio para nuestras acciones CRUD del modelo Registro
public interface RegistroRepositorio extends JpaRepository<Registro, Integer> {

    //Metodo para solo mostrar los registros de una fecha y un usuario especifico
    Registro findByFechaAndUsuario_Expediente(LocalDate fecha, Integer expediente);

    // Metodo para contar cuantos registros existen dentro de una fecha determinada
    Integer countAllByFechaBetween(LocalDate fechaInicio, LocalDate fechaTermino);

    //Metodo para utilizar la paginacion, dandole un objeto Pageable
    Page<Registro> findByUsuario_ExpedienteOrderByFecha(Integer expediente, Pageable pageable);

    Page<Registro> findAllByOrderByFecha(Pageable pageable);

}
