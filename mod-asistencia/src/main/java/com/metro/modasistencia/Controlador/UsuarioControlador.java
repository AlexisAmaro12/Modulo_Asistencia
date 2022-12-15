package com.metro.modasistencia.Controlador;

import com.metro.modasistencia.modelo.Rol;
import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.repositorio.RolRepositorio;
import com.metro.modasistencia.servicio.RegistroServicio;
import com.metro.modasistencia.servicio.UsuarioServicio;
import com.metro.modasistencia.util.RangoDias;
import com.metro.modasistencia.util.paginacion.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//Controlador para interactuar con el modelo Usuario
@Controller
public class UsuarioControlador {


    @Autowired //Inyeccion de los servicios de los usuarios
    private UsuarioServicio usuarioServicio;

    @Autowired //Inyeccion de los servicios de los roles
    private RolRepositorio rolRepositorio;

    @Autowired //Inyeccion de los servicios de los registros
    private RegistroServicio registroServicio;

    @Autowired //Inyeccion del encriptador de password
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //Dirige a la pagina de inicio
    @GetMapping("/")
    public String paginaInicio() {
        return "redirect:/registros/nuevo"; //Dirige a la pagina de inicio
    }

    //Peticion para iniciar sesion
    @GetMapping("/login")
    public String mostrarLogin() {
        return "usuario/usuario-login"; //Dirige al HTML login
    }

    //Peticion para mostrar opciones
    @GetMapping("/administrar")
    public String mostrarOpciones(HttpServletRequest request, Model modelo) {
        //Obtenemos el expediente del usuario que inicio sesion
        Integer expediente = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        String estadoUsuario = usuarioServicio.findOne(expediente).getEstado(); //Obtenemos el estado del usuario que inicio sesion
        if(estadoUsuario.equals("Inactivo")) {
            modelo.addAttribute("mensaje", "Se encuentra inactivo");
            new SecurityContextLogoutHandler().logout(request, null, null);
            return "usuario/usuario-login";
        }
        //Obtencion de los roles del usuario que inicio sesion
        String roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        //Comprueba que su rol sea de administrador
        if (roles.contains("ROLE_ADMIN")) {
            //Manda mensaje de bienvenida a la vista
            modelo.addAttribute("msgBienvenida", "Administrador");
        }
        //Comprueba que su rol sea de supervisor
        else if (roles.contains("ROLE_SUPERVISOR")){
            //Manda mensaje de bienvenida a la vista
            modelo.addAttribute("msgBienvenida", "Supervisor");
        }
        else {
            //Manda mensaje de bienvenida a la vista
            modelo.addAttribute("msgBienvenida", "Usuario");
        }
        return "usuario/usuario-administrar"; //Dirige al HTML administrar
    }

    //Peticion para ver las estadisticas
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')")
    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model modelo) {
        RangoDias rangoDias = new RangoDias().obtenerDiasDelPeriodo();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM");
        int numeroUsuarios = usuarioServicio.countByEstado();
        int diasHabiles = rangoDias.calcularDiasHabiles(rangoDias, new ArrayList<>());
        int asistenciasRegistradas = registroServicio.countByFechaBetween(rangoDias.getFechaInicial(), rangoDias.getFechaFinal());
        int asistenciasTotales = (numeroUsuarios * diasHabiles);
        int faltas = asistenciasTotales - asistenciasRegistradas;
        modelo.addAttribute("fechaInicio", rangoDias.getFechaInicial().format(formatter));
        modelo.addAttribute("fechaFinal", rangoDias.getFechaFinal().format(formatter));
        modelo.addAttribute("numeroAsistencias", asistenciasRegistradas);
        modelo.addAttribute("faltas", faltas);
        return "estadistica";
    }

    //Peticion para mostrar lista de usuarios
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')")
    @GetMapping("/usuarios")
    public String mostrarListaUsuarios(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo) {
        Pageable pageRequest = PageRequest.of(page, 7); //Objeto para la paginacion
        Page<Usuario> usuarios = usuarioServicio.findAll(pageRequest); //Obtiene todos los usuarios con paginacion
        PageRender<Usuario> registroPageRender = new PageRender<>("/usuarios", usuarios);
        //Manda la lista de usuarios a la vista
        modelo.addAttribute("listaUsuarios", usuarios);
        modelo.addAttribute("page", registroPageRender);
        return "usuario/usuario-lista"; //Dirige al HTML de usuario
    }

    //Peticion para iniciar sesion
    @GetMapping("/cambiar-password")
    public String mostrarFormularioPassword(Model model) {
        return "usuario/usuario-password"; //Dirige al HTML login
    }
    //Peticion para iniciar sesion
    @PostMapping("/cambiar-password")
    public String cambiarPassword(Model model, @RequestParam String password, @RequestParam String nuevoPassword, @RequestParam String verPassword, RedirectAttributes redirect) {
        //Obtenemos el expediente del usuario que inicio sesion
        Integer expediente = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        String passwordUsuario = usuarioServicio.getPass(expediente);//Se obtiene el password del usuario por su expediente
        if (!bCryptPasswordEncoder.matches(password, passwordUsuario)) {
            //Se envia un mensaje de error a la vista
            model.addAttribute("msgError", "Contraseña incorrecta");
            return "usuario/usuario-password"; //Se dirige al HTML registro_formulario
        }
        if(!nuevoPassword.equals(verPassword)) {
            model.addAttribute("msgError", "La contraseña nueva no coincide");
            return "usuario/usuario-password"; //Dirige al HTML usuario_nuevo_formulario
        }
        Usuario usuario = usuarioServicio.findOne(expediente);
        String passwordEncode = bCryptPasswordEncoder.encode(nuevoPassword); //Encripta el password
        usuario.setPassword(passwordEncode); //Asigna el password encriptado al usuario
        usuarioServicio.save(usuario); //guarda al nuevo usuario
        redirect.addFlashAttribute("msgExito", "La contraseña se actualizo con exito");
        return "redirect:/administrar"; //Dirige al HTML login
    }

    //Peticion para mostrar el formulario para un usuario nuevo
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevoUsuario(Model modelo) {
        Usuario usuario = new Usuario(); //Crea un usuario el cual se enviara a la vista
        modelo.addAttribute("usuario", usuario);
        List<Rol> roles = rolRepositorio.findAll();
        modelo.addAttribute("roles", roles);
        return "usuario/usuario-nuevo"; //Dirige al HTML usuario_nuevo_formulario
    }

    //Peticion POST para guardar un usuario
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/usuarios/nuevo")
    public String guardarUsuario(@RequestParam(value = "verPassword", defaultValue = "") String verPassword, @Validated Usuario usuario, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        //Verifica que no existan errores en el formulario
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("usuario", usuario);
            List<Rol> roles = rolRepositorio.findAll();
            modelo.addAttribute("roles", roles);
            return "usuario/usuario-nuevo";
        }
        //Obtiene un usuario por su expediente
        Usuario usuarioExiste = usuarioServicio.findOne(usuario.getExpediente());
        //Comprueba que el usuario exista
        if (usuarioExiste != null) {
            //Manda mensaje de fallo a la vista
            modelo.addAttribute("usuario", usuario);
            List<Rol> roles = rolRepositorio.findAll();
            modelo.addAttribute("roles", roles);
            modelo.addAttribute("msgFallo", "El usuario ya existe");
            return "usuario/usuario-nuevo"; //Dirige al HTML usuario_nuevo_formulario
        }
        if(!usuario.getPassword().equals(verPassword)) {
            //Manda mensaje de fallo a la vista
            modelo.addAttribute("usuario", usuario);
            List<Rol> roles = rolRepositorio.findAll();
            modelo.addAttribute("roles", roles);
            modelo.addAttribute("msgError", "El password no coincide");
            return "usuario/usuario-nuevo"; //Dirige al HTML usuario_nuevo_formulario
        }
        //Obtiene el password ingresado
        String pass = usuario.getPassword();
        String passwordEncode = bCryptPasswordEncoder.encode(pass); //Encripta el password
        usuario.setPassword(passwordEncode); //Asigna el password encriptado al usuario
        usuarioServicio.save(usuario); //guarda al nuevo usuario
        //Manda mensaje de exito a la vista
        redirect.addFlashAttribute("msgExito", "El usuario ha sido agregado con exito");
        return "redirect:/usuarios"; //Dirige a la URL /usuarios
    }

    //Peticion para mostrar el formualrio para editar un usuario
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')")
    @GetMapping("/usuarios/editar/{expediente}")
    public String mostrarFormularioEditarUsuario(@PathVariable Integer expediente, Model modelo) {
        Usuario usuario = usuarioServicio.findOne(expediente); //Recupera un usuario por su expeidnte
        modelo.addAttribute("usuario", usuario); //Pasa a la vista ese usuario
        List<Rol> roles = rolRepositorio.findAll();
        modelo.addAttribute("roles", roles);
        return "usuario/usuario-editar"; //Dirige al HTML usuario_editar_formulario
    }

    //Peticion POST para actualizar un usuario
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')")
    @PostMapping("/usuarios/editar/{expediente}")
    public String actualizarUsuario(@RequestParam(value = "verPassword", defaultValue = "") String verPassword,@PathVariable Integer expediente, @Validated Usuario usuario, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        //Verifica que no existan errores en el formulario
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("usuario", usuario);
            List<Rol> roles = rolRepositorio.findAll();
            modelo.addAttribute("roles", roles);
            return "usuario/usuario-editar"; //Dirige a el HTML usuario_editar_formulario
        }

        Usuario usuarioDB = usuarioServicio.findOne(expediente); //Obtiene el usuario existente
        //Asignan algunos valores que se cambiaron en el formulario
        usuarioDB.setNombre(usuario.getNombre());
        usuarioDB.setHoraEntrada(usuario.getHoraEntrada());
        usuarioDB.setHoraSalida(usuario.getHoraSalida());

        //Comprueba que no esten vacios los campos del usuario pasado por el formulario
        if (usuario.getRoles() != null && usuario.getEstado() != null) {
            //Asigna los nuevos estados y roles al usuario
            usuarioDB.setEstado(usuario.getEstado());
            usuarioDB.setRoles(usuario.getRoles());
        }
        //Comprueba que el password haya sido modificado
        if (!usuario.getPassword().equals("")) {
            if(!usuario.getPassword().equals(verPassword)) {
                //Manda mensaje de fallo a la vista
                modelo.addAttribute("usuario", usuario);
                List<Rol> roles = rolRepositorio.findAll();
                modelo.addAttribute("roles", roles);
                modelo.addAttribute("msgError", "El password no coincide");
                return "usuario/usuario-editar"; //Dirige al HTML usuario_nuevo_formulario
            }
            String pass = usuario.getPassword(); //Obtiene el password ingresado
            String passwordEncode = bCryptPasswordEncoder.encode(pass); //Encripta el password
            usuarioDB.setPassword(passwordEncode); //Asigna el password al usuario
        }

        usuarioServicio.save(usuarioDB); //Guarda el usuario actualizado
        //Manda mensaje de exito a la vista
        redirect.addFlashAttribute("msgExito", "El usuario ha sido actualizado con exito");
        return "redirect:/usuarios"; //Dirige a la URL /usuarios
    }

    //Peticion para eliminar un usuario
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/usuarios/eliminar/{expediente}")
    public String eliminarUsuario(@PathVariable Integer expediente, RedirectAttributes redirect) {
        usuarioServicio.delete(expediente); //Elimina un usuario por su expediente
        //Manda mensaje de exito a la vista
        redirect.addFlashAttribute("msgExito", "El contacto ha sido eliminado correctamente");

        return "redirect:/usuarios"; //Dirige a la URL /usuarios
    }

    //Peticion para activar un usuario y que este pueda hacer sus registros
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/usuarios/activar/{expediente}")
    public String activarUsuario(@PathVariable Integer expediente, RedirectAttributes redirect) {
        Usuario usuarioAlta = usuarioServicio.findOne(expediente); //Obtiene un usuario por su expediente
        usuarioAlta.setEstado("Activo"); //Asigna el estado activo al usuario
        usuarioServicio.save(usuarioAlta); //Guarda el usuario con estado activo
        //Manda mensaje de exito a la vista
        redirect.addFlashAttribute("msgExito", "El usuario se ha activado correctamente");

        return "redirect:/usuarios"; //Dirige a la URL /usuarios
    }

    //Peticion para desactivar un usuario y que este ya no pueda crear nuevos registros comunes o de incidencias
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/usuarios/desactivar/{expediente}")
    public String desactivarUsuario(@PathVariable Integer expediente, RedirectAttributes redirect) {
        Usuario usuarioBaja = usuarioServicio.findOne(expediente); //Obtiene un usuario por su expediente
        usuarioBaja.setEstado("Inactivo"); //Asigna el estado inactivo al usuario
        usuarioServicio.save(usuarioBaja); //Guarda el usuario con estado inactivo
        //Manda mensaje de exito a la vista
        redirect.addFlashAttribute("msgExito", "El usuario se ha dado de baja correctamente");

        return "redirect:/usuarios"; //Dirige a la URL /usuarios
    }
}