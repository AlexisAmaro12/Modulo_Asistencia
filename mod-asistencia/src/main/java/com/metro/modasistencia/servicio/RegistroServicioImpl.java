package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Registro;
import com.metro.modasistencia.repositorio.RegistroRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistroServicioImpl implements RegistroServicio{

    @Autowired
    private RegistroRepositorio registroRepositorio;

    @Override
    public List<Registro> findAll() {
        return registroRepositorio.findAll();
    }

    @Override
    public Page<Registro> findAll(Pageable pageable) {
        return registroRepositorio.findAll(pageable);
    }

    @Override
    public Registro findOne(Integer expediente) {
        return registroRepositorio.findById(expediente).orElse(null);
    }

    @Override
    public Registro findByFechaAndExpediente(LocalDate fecha, Integer expediente) {
        return registroRepositorio.findByFechaAndUsuario_Expediente(fecha, expediente);
    }

    @Override
    public Integer countByFechaBetween(LocalDate fechasInicio, LocalDate fechaFinal) {
        return registroRepositorio.countAllByFechaBetween(fechasInicio, fechaFinal);
    }

    @Override
    public void save(Registro registro) {
        registroRepositorio.save(registro);
    }

    @Override
    public void delete(Integer expediente) {
        registroRepositorio.deleteById(expediente);
    }
}
