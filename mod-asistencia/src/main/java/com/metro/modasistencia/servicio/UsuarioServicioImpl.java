package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service //Implementacion de los metodos declarados en la interface UsuarioServicio
public class UsuarioServicioImpl implements UsuarioServicio{

    @Autowired //Inyeccion del repositorio de usuario
    private UsuarioRepositorio usuarioRepositorio;

    @Override //Listar todos los usuarios
    public List<Usuario> findAll() {
        return usuarioRepositorio.findAll();
    }

    @Override //Listar todos los usuarios usando paginacion
    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepositorio.findAll(pageable);
    }

    @Override //Buscar un usuario por su expediente
    public Usuario findOne(Integer expediente) {
        return usuarioRepositorio.findById(expediente).orElse(null);
    }

    @Override //Obtener el password de un usuario por su expediente
    public String getPass(Integer expediente) {
        try {
            String pass = usuarioRepositorio.getPass(expediente);
            return pass;
        } catch (Exception e) {
            return null;
        }
    }

    @Override //Contar los usuarios con estado Activo
    public Integer countByEstado() {
        return usuarioRepositorio.countByEstadoEquals("Activo");
    }

    @Override //Guardar un usuario
    public void save(Usuario usuario) {
        usuarioRepositorio.save(usuario);
    }

    @Override //Eliminar un usuario
    public void delete(Integer id) {
        usuarioRepositorio.deleteById(id);
    }
}
