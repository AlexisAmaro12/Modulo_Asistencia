package com.metro.modasistencia.servicio;

import com.metro.modasistencia.modelo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

//Interface para darle los servicios que tendra el modelo Usuario
public interface UsuarioServicio {

    //Listar todos los usuarios
    List<Usuario> findAll();

    //Listar todos los usuarios utilizando paginacion
    Page<Usuario> findAll(Pageable pageable);

    //Buscar un usuario por su expediente
    Usuario findOne(Integer expediente);

    //Obtener el password de un usuario por su expediente
    String getPass(Integer expediente);

    //Contar los usuarios por su estado
    Integer countByEstado();

    //Guardar un usuario
    void save(Usuario usuario);

    //Eliminar un usuario por su expediente
    void delete(Integer expediente);
}
