package com.metro.modasistencia.Controlador;

import com.metro.modasistencia.modelo.Incidencia;
import com.metro.modasistencia.modelo.Registro;
import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UsuarioControlador {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/")
    public String mostrarPaginaInicio(Model modelo) {
        return "index";
    }

    @GetMapping("/administrar")
    public String mostrarOpcionesAdministrador(Model modelo) {
        return "administrar";
    }

    @GetMapping("/usuarios")
    public String mostrarListaUsuarios(Model modelo) {
        List<Usuario> listaUsuarios = usuarioRepositorio.findAll();
        modelo.addAttribute("listausuarios", listaUsuarios);

        return "usuario";
    }

    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevoUsuario(Model modelo) {
        Usuario usuario = new Usuario();
        modelo.addAttribute("usuario", usuario);

        return "usuario_nuevo_formulario";
    }

    @PostMapping("/usuarios/nuevo")
    public String guardarUsuario(@Validated Usuario usuario, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("usuario", usuario);
            return "usuario_nuevo_formulario";
        }
        usuarioRepositorio.save(usuario);
        redirect.addFlashAttribute("msgExito", "El usuario ha sido agregado con exito");
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/editar/{expediente}")
    public String mostrarFormularioEditarUsuario(@PathVariable Integer expediente, Model modelo) {
        Usuario usuario = usuarioRepositorio.findById(expediente).get();
        modelo.addAttribute("usuario", usuario);

        return "usuario_editar_formulario";
    }

    @PostMapping("/usuarios/editar/{expediente}")
    public String actualizarUsuario(@PathVariable Integer expediente, @Validated Usuario usuario, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        Usuario usuarioDB = usuarioRepositorio.findById(expediente).get();

        if (bindingResult.hasErrors()) {
            modelo.addAttribute("usuario", usuario);
            return "usuario_editar_formulario";
        }

        usuarioDB.setNombre(usuario.getNombre());
        usuarioDB.setHoraEntrada(usuario.getHoraEntrada());
        usuarioDB.setHoraSalida(usuario.getHoraSalida());
        usuarioDB.setPassword(usuario.getPassword());
        usuarioDB.setEstado(usuario.getEstado());

        usuarioRepositorio.save(usuarioDB);
        redirect.addFlashAttribute("msgExito", "El usuario ha sido actualizado con exito");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/eliminar/{expediente}")
    public String eliminarUsuario(@PathVariable Integer expediente, RedirectAttributes redirect, Model modelo) {
        usuarioRepositorio.deleteById(expediente);
        redirect.addFlashAttribute("msgExito", "El contacto ha sido eliminado correctamente");

        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/desactivar/{expediente}")
    public String desactivarUsuario(@PathVariable Integer expediente, RedirectAttributes redirect, Model modelo) {
        Usuario usuarioBaja = usuarioRepositorio.findById(expediente).get();
        if (usuarioBaja.getEstado().equals("Inactivo")) {
            redirect.addFlashAttribute("msgFallo", "El usuario ya se encuentra desactivado");

            return "redirect:/usuarios";
        }
        usuarioBaja.setEstado("Inactivo");
        usuarioRepositorio.save(usuarioBaja);
        redirect.addFlashAttribute("msgExito", "El usuario se ha dado de baja correctamente");

        return "redirect:/usuarios";
    }
}