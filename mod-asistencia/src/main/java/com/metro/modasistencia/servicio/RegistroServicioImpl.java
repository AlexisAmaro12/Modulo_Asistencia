package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Registro;
import com.metro.modasistencia.repositorio.RegistroRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service //Implementacion de los metodos declarados en la interface RegistroServicio
public class RegistroServicioImpl implements RegistroServicio{

    @Autowired //Inyeccion del repositorio del modelo Registro
    private RegistroRepositorio registroRepositorio;

    @Override //Listar todos los registros
    public List<Registro> findAll() {
        return registroRepositorio.findAll();
    }

    @Override //Listar todos los registros utilizando paginacion
    public Page<Registro> findAll(Pageable pageable) {
        return registroRepositorio.findAll(pageable);
    }

    @Override //Listar todos los registros de un usuario especifico usando paginacion
    public Page<Registro> findByExpediente(Integer expediente, Pageable pageable) {
        return registroRepositorio.findByUsuario_Expediente(expediente,pageable);
    }

    @Override //Buscar un registro especifico por su id
    public Registro findOne(Integer id) {
        return registroRepositorio.findById(id).orElse(null);
    }

    @Override //Buscar un registro por su fecha y expediente del usuario
    public Registro findByFechaAndExpediente(LocalDate fecha, Integer expediente) {
        return registroRepositorio.findByFechaAndUsuario_Expediente(fecha, expediente);
    }

    @Override //Contar todos los registros en un lapso de tiempo
    public Integer countByFechaBetween(LocalDate fechasInicio, LocalDate fechaFinal) {
        return registroRepositorio.countAllByFechaBetween(fechasInicio, fechaFinal);
    }

    @Override //Guardar un registro
    public void save(Registro registro) {
        registroRepositorio.save(registro);
    }

    @Override //Eliminar un registro
    public void delete(Integer expediente) {
        registroRepositorio.deleteById(expediente);
    }
}
