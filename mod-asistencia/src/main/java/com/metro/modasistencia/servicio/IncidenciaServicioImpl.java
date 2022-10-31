package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Incidencia;
import com.metro.modasistencia.repositorio.IncidenciaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service //Implementacion de los metodos declarados en la interface IncidenciaServicio
public class IncidenciaServicioImpl implements IncidenciaServicio{

    @Autowired  //Inyecccion del repositorio de incidencia
    private IncidenciaRepositorio incidenciaRepositorio;

    @Override  //Listar todas las incidencias
    public List<Incidencia> findAll() {
        return incidenciaRepositorio.findAll();
    }

    @Override
    public List<Incidencia> findAllByOrderByFecha() {
        return incidenciaRepositorio.findAllByOrderByFecha();
    }

    @Override  //Listar todas las incidencias agregando paginacion
    public Page<Incidencia> findAll(Pageable pageable) {
        return incidenciaRepositorio.findAll(pageable);
    }

    @Override
    public Page<Incidencia> findAllByOrderByFecha(Pageable pageable) {
        return incidenciaRepositorio.findAllByOrderByEstado(pageable);
    }

    @Override  //Listar todas las incidencias de un usuario determinado y con paginacion
    public Page<Incidencia> findByExpediente(Integer expediente, Pageable pageable) {
        return incidenciaRepositorio.findByUsuario_Expediente(expediente, pageable);
    }

    @Override  //Buscamos una incidencia en particular dependiendo el usuario
    public Incidencia findOne(Integer id) {
        return incidenciaRepositorio.findById(id).orElse(null);
    }

    @Override  //Guardamos el registro de incidencia
    public void save(Incidencia incidencia) {
        incidenciaRepositorio.save(incidencia);
    }

    @Override  //Eliminamos una incidencia
    public void delete(Integer id) {
        incidenciaRepositorio.deleteById(id);
    }
}
