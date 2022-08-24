package com.metro.modasistencia.Controlador;

import com.metro.modasistencia.modelo.Incidencia;
import com.metro.modasistencia.modelo.Registro;
import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.repositorio.IncidenciaRepositorio;
import com.metro.modasistencia.repositorio.RegistroRepositorio;
import com.metro.modasistencia.repositorio.UsuarioRepositorio;
import com.metro.modasistencia.utilerias.reportes.IncidenciaExportarExcel;
import com.metro.modasistencia.utilerias.reportes.RegistroExportarExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class IncidenciaControlador {

    @Autowired
    IncidenciaRepositorio incidenciaRepositorio;

    @Autowired
    RegistroRepositorio registroRepositorio;

    @GetMapping("/incidencias")
    public String listaIncidencias(Model modelo) {
        List<Incidencia> listaIncidencias = incidenciaRepositorio.findAll();
        modelo.addAttribute("listaIncidencias", listaIncidencias);

        return "incidencia";
    }

    @GetMapping("/incidencias/nueva")
    public String mostrarFormularioNuevaIncidencia(Model modelo) {
        Incidencia incidencia = new Incidencia();
        modelo.addAttribute("incidencia", incidencia);

        return "incidencia_formulario";
    }

    @PostMapping("/incidencias/nueva")
    public String guardarIncidencia(@Validated Incidencia incidencia, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("incidencia", incidencia);

            return "incidencia_formulario";
        }

        incidenciaRepositorio.save(incidencia);
        redirect.addFlashAttribute("msgExito", "La incidencia se registro correctamente");
        return "redirect:/";
    }

    @GetMapping("/incidencias/editar/{id}")
    public String mostrarFormularioEditarIncidencia(@PathVariable Integer id, Model modelo) {
        Incidencia incidencia = incidenciaRepositorio.findById(id).get();
        modelo.addAttribute("incidencia", incidencia);

        return "incidencia_formulario";
    }

    @PostMapping("/incidencias/editar/{id}")
    public String actualizarIncidencia(@PathVariable Integer id, @Validated Incidencia incidencia, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        Incidencia incidenciaBD = incidenciaRepositorio.findById(id).get();
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("incidencia", incidencia);

            return "incidencia_formulario";
        }

        incidenciaBD.setEstado(incidencia.getEstado());
        incidenciaBD.setFecha(incidencia.getFecha());
        incidenciaBD.setHora(incidencia.getHora());
        incidenciaBD.setUsuario(incidencia.getUsuario());
        incidenciaBD.setDetalles(incidencia.getDetalles());
        incidenciaBD.setTipo(incidencia.getTipo());

        incidenciaRepositorio.save(incidenciaBD);
        redirect.addFlashAttribute("msgExito", "El registro de incidencia ha sido actualizado correctamente");
        return "redirect:/incidencias";
    }

    @PostMapping("/incidencias/eliminar/{id}")
    public String eliminarIncidencia(@PathVariable Integer id, RedirectAttributes redirect, Model modelo) {
        incidenciaRepositorio.deleteById(id);
        redirect.addFlashAttribute("msgExito", "El registro de incidencia fue eliminado correctamente");

        return "redirect:/incidencias";
    }

    @PostMapping("/incidencias/validar/{id}")
    public String validarIncidencia(@PathVariable Integer id, RedirectAttributes redirect, Model modelo) {
        Incidencia incidenciaValidado = incidenciaRepositorio.findById(id).get();
        if (incidenciaValidado.getEstado().equals("Validado")) {
            redirect.addFlashAttribute("msgFallo", "El registro de incidencia ya ha sido validado");

            return "redirect:/incidencias";
        }
        incidenciaValidado.setEstado("Validado");
        Registro registroCreado = new Registro();
        registroCreado.setUsuario(incidenciaValidado.getUsuario());
        registroCreado.setTipo(incidenciaValidado.getTipo());
        registroRepositorio.save(registroCreado);
        registroCreado.setHora(incidenciaValidado.getHora());
        registroCreado.setFecha(incidenciaValidado.getFecha());

        incidenciaRepositorio.save(incidenciaValidado);
        registroRepositorio.save(registroCreado);
        redirect.addFlashAttribute("msgExito", "El registro de incidencia se ha validado correctamente");

        return "redirect:/incidencias";
    }

    @GetMapping("/incidencias/exportar")
    public void exportarListaRegistrosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fechaActual = dateFormat.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Incidencias_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        List<Incidencia> incidencias = incidenciaRepositorio.findAll();

        IncidenciaExportarExcel exporter = new IncidenciaExportarExcel(incidencias);
        exporter.exportar(response);
    }
}