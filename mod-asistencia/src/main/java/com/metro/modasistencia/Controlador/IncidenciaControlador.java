package com.metro.modasistencia.Controlador;

import com.metro.modasistencia.modelo.Incidencia;
import com.metro.modasistencia.modelo.Registro;
import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.servicio.IncidenciaServicio;
import com.metro.modasistencia.servicio.RegistroServicio;
import com.metro.modasistencia.servicio.UsuarioServicio;
import com.metro.modasistencia.utilerias.paginacion.PageRender;
import com.metro.modasistencia.utilerias.reportes.IncidenciaExportarExcel;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class IncidenciaControlador {

    @Autowired
    private IncidenciaServicio incidenciaServicio;

    @Autowired
    private RegistroServicio registroServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/incidencias")
    public String listaIncidencias(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo) {
        Pageable pageRequest = PageRequest.of(page, 7);
        Page<Incidencia> incidencias = incidenciaServicio.findAll(pageRequest);
        PageRender<Incidencia> registroPageRender = new PageRender<>("/incidencias", incidencias);

        modelo.addAttribute("listaIncidencias", incidencias);
        modelo.addAttribute("page", registroPageRender);
        return "incidencia";
    }

    @GetMapping("/incidencias/nueva")
    public String mostrarFormularioNuevaIncidencia(Model modelo) {
        Incidencia incidencia = new Incidencia();
        modelo.addAttribute("incidencia", incidencia);

        return "incidencia_formulario";
    }

    @PostMapping("/incidencias/nueva")
    public String guardarIncidencia(@RequestParam(value = "usuario", defaultValue = "0") Integer  exp, @RequestParam(value = "password", defaultValue = "null") String pass, @Validated Incidencia incidencia, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        Usuario usuarioExiste = usuarioServicio.findByExpedienteAndPassword(exp, pass);
        if(usuarioExiste == null) {
            modelo.addAttribute("msgError", "Expediente o contraseña incorrectos");
            return "incidencia_formulario";
        }
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("incidencia", incidencia);

            return "incidencia_formulario";
        }

        incidenciaServicio.save(incidencia);
        redirect.addFlashAttribute("msgExito", "La incidencia se registro correctamente");
        return "redirect:/";
    }

    @GetMapping("/incidencias/editar/{id}")
    public String mostrarFormularioEditarIncidencia(@PathVariable Integer id, Model modelo) {
        Incidencia incidencia = incidenciaServicio.findOne(id);
        modelo.addAttribute("incidencia", incidencia);

        return "incidencia_formulario";
    }

    @PostMapping("/incidencias/editar/{id}")
    public String actualizarIncidencia(@PathVariable Integer id, @Validated Incidencia incidencia, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        Incidencia incidenciaBD = incidenciaServicio.findOne(id);
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

        incidenciaServicio.save(incidenciaBD);
        redirect.addFlashAttribute("msgExito", "El registro de incidencia ha sido actualizado correctamente");
        return "redirect:/incidencias";
    }

    @PostMapping("/incidencias/eliminar/{id}")
    public String eliminarIncidencia(@PathVariable Integer id, RedirectAttributes redirect) {
        incidenciaServicio.delete(id);
        redirect.addFlashAttribute("msgExito", "El registro de incidencia fue eliminado correctamente");

        return "redirect:/incidencias";
    }

    @PostMapping("/incidencias/validar/{id}")
    public String validarIncidencia(@PathVariable Integer id, RedirectAttributes redirect) {
        Incidencia incidenciaValidado = incidenciaServicio.findOne(id);
        if (incidenciaValidado.getEstado().equals("Validado")) {
            redirect.addFlashAttribute("msgFallo", "El registro de incidencia ya ha sido validado");

            return "redirect:/incidencias";
        }
        incidenciaValidado.setEstado("Validado");
        Registro registroExiste = registroServicio.findByFechaAndExpediente(incidenciaValidado.getFecha(), incidenciaValidado.getUsuario().getExpediente());
        if (registroExiste == null) {
            Registro registroCreado = new Registro();
            registroCreado.setUsuario(incidenciaValidado.getUsuario());
            if (incidenciaValidado.getTipo().equals("Entrada")) {
                registroCreado.setHoraEntrada(incidenciaValidado.getHora());
            }
            if (incidenciaValidado.getTipo().equals("Salida")) {
                registroCreado.setHoraSalida(incidenciaValidado.getHora());
            }
            registroServicio.save(registroCreado);
            registroCreado.setFecha(incidenciaValidado.getFecha());
            incidenciaServicio.save(incidenciaValidado);
            registroServicio.save(registroCreado);
            redirect.addFlashAttribute("msgExito", "El registro de incidencia se ha validado correctamente");

            return "redirect:/incidencias";
        }
        if (incidenciaValidado.getTipo().equals("Entrada")) {
            registroExiste.setHoraEntrada(incidenciaValidado.getHora());
        }
        if (incidenciaValidado.getTipo().equals("Salida")) {
            registroExiste.setHoraSalida(incidenciaValidado.getHora());
        }
        registroServicio.save(registroExiste);
        incidenciaServicio.save(incidenciaValidado);
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

        List<Incidencia> incidencias = incidenciaServicio.findAll();

        IncidenciaExportarExcel exporter = new IncidenciaExportarExcel(incidencias);
        exporter.exportar(response);
    }
}