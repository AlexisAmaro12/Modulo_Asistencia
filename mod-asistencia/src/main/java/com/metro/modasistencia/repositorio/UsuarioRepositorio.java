package com.metro.modasistencia.repositorio;

import com.metro.modasistencia.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


//Repositorio para nuestras acciones CRUD del modelo usuario
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    //Metodo para contar los registros por su estado
    Integer countByEstadoEquals(String estado);

    //En esta variable guardaremos las instrucciones para una Query que nos recupere el password de un usuario determinado
    String FIND_PROJECTS = "SELECT usuario_password FROM usuario WHERE exp_usuario = (:expediente)";
    @Query(value = FIND_PROJECTS, nativeQuery = true) //Metodo para recuperar el password de un usuario dando su expediente
    String getPass(Integer expediente);
}
