package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Incidencia;
import com.metro.modasistencia.repositorio.IncidenciaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidenciaServicioImpl implements IncidenciaServicio{

    @Autowired
    private IncidenciaRepositorio incidenciaRepositorio;

    @Override
    public List<Incidencia> findAll() {
        return incidenciaRepositorio.findAll();
    }

    @Override
    public Page<Incidencia> findAll(Pageable pageable) {
        return incidenciaRepositorio.findAll(pageable);
    }

    @Override
    public Incidencia findOne(Integer id) {
        return incidenciaRepositorio.findById(id).orElse(null);
    }

    @Override
    public void save(Incidencia incidencia) {
        incidenciaRepositorio.save(incidencia);
    }

    @Override
    public void delete(Integer id) {
        incidenciaRepositorio.deleteById(id);
    }
}
