package com.metro.modasistencia.Controlador;

import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.servicio.RegistroServicio;
import com.metro.modasistencia.servicio.UsuarioServicio;
import com.metro.modasistencia.utilerias.paginacion.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;


@Controller
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private RegistroServicio registroServicio;

    @GetMapping("/")
    public String mostrarPaginaInicio() {
        return "index";
    }

    @GetMapping("/administrar")
    public String mostrarOpcionesAdministrador() {
        return "administrar";
    }

    @GetMapping("/estadisticas")
    public String mostrarOpcionesGraficas(Model modelo) {
        Integer numeroUsuarios = usuarioServicio.countByEstado();
        modelo.addAttribute("numeroUsuarios", numeroUsuarios);
        LocalDate fechaInicio = LocalDate.of(2022,8,30);
        LocalDate fechaFinal = LocalDate.of(2022,9,1);
        Long diasPasados = ChronoUnit.DAYS.between(fechaInicio, fechaFinal) + 1;
        Integer numeroAsistencias = registroServicio.countByFechaBetween(fechaInicio, fechaFinal);
        Long asistenciasTotales = (numeroUsuarios.longValue() * diasPasados) * 2;
        Long faltas = asistenciasTotales - numeroAsistencias.longValue();
        modelo.addAttribute("asistenciasTotales", asistenciasTotales);
        modelo.addAttribute("numeroAsistencias", numeroAsistencias);
        modelo.addAttribute("faltas", faltas);
        return "estadistica";
    }

    @GetMapping("/usuarios")
    public String mostrarListaUsuarios(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo) {
        Pageable pageRequest = PageRequest.of(page, 7);
        Page<Usuario> usuarios = usuarioServicio.findAll(pageRequest);
        PageRender<Usuario> registroPageRender = new PageRender<>("/usuarios", usuarios);

        modelo.addAttribute("listaUsuarios", usuarios);
        modelo.addAttribute("page", registroPageRender);
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
        Usuario usuarioExiste = usuarioServicio.findOne(usuario.getExpediente());
        if (usuarioExiste != null) {
            modelo.addAttribute("msgFallo", "El usuario ya existe");
            return "usuario_nuevo_formulario";
        }
        usuarioServicio.save(usuario);
        redirect.addFlashAttribute("msgExito", "El usuario ha sido agregado con exito");
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/editar/{expediente}")
    public String mostrarFormularioEditarUsuario(@PathVariable Integer expediente, Model modelo) {
        Usuario usuario = usuarioServicio.findOne(expediente);
        modelo.addAttribute("usuario", usuario);

        return "usuario_editar_formulario";
    }

    @PostMapping("/usuarios/editar/{expediente}")
    public String actualizarUsuario(@PathVariable Integer expediente, @Validated Usuario usuario, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        Usuario usuarioDB = usuarioServicio.findOne(expediente);

        if (bindingResult.hasErrors()) {
            modelo.addAttribute("usuario", usuario);
            return "usuario_editar_formulario";
        }

        usuarioDB.setNombre(usuario.getNombre());
        usuarioDB.setHoraEntrada(usuario.getHoraEntrada());
        usuarioDB.setHoraSalida(usuario.getHoraSalida());
        usuarioDB.setPassword(usuario.getPassword());
        usuarioDB.setEstado(usuario.getEstado());

        usuarioServicio.save(usuarioDB);
        redirect.addFlashAttribute("msgExito", "El usuario ha sido actualizado con exito");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/eliminar/{expediente}")
    public String eliminarUsuario(@PathVariable Integer expediente, RedirectAttributes redirect) {
        usuarioServicio.delete(expediente);
        redirect.addFlashAttribute("msgExito", "El contacto ha sido eliminado correctamente");

        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/desactivar/{expediente}")
    public String desactivarUsuario(@PathVariable Integer expediente, RedirectAttributes redirect) {
        Usuario usuarioBaja = usuarioServicio.findOne(expediente);
        if (usuarioBaja.getEstado().equals("Inactivo")) {
            redirect.addFlashAttribute("msgFallo", "El usuario ya se encuentra desactivado");

            return "redirect:/usuarios";
        }
        usuarioBaja.setEstado("Inactivo");
        usuarioServicio.save(usuarioBaja);
        redirect.addFlashAttribute("msgExito", "El usuario se ha dado de baja correctamente");

        return "redirect:/usuarios";
    }
}