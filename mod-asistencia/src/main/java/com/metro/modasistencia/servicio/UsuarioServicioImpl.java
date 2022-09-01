package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServicioImpl implements UsuarioServicio{

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepositorio.findAll(pageable);
    }

    @Override
    public Usuario findOne(Integer id) {
        return usuarioRepositorio.findById(id).orElse(null);
    }

    @Override
    public Usuario findByExpedienteAndPassword(Integer expediente, String password) {
        return usuarioRepositorio.findByExpedienteAndPassword(expediente, password);
    }

    @Override
    public Integer countByEstado() {
        return usuarioRepositorio.countByEstadoEquals("Activo");
    }

    @Override
    public void save(Usuario usuario) {
        usuarioRepositorio.save(usuario);
    }

    @Override
    public void delete(Integer id) {
        usuarioRepositorio.deleteById(id);
    }
}
