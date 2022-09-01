package com.metro.modasistencia.Controlador;

import com.metro.modasistencia.modelo.Registro;
import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.servicio.RegistroServicio;
import com.metro.modasistencia.servicio.UsuarioServicio;
import com.metro.modasistencia.utilerias.RegistroUtileria;
import com.metro.modasistencia.utilerias.paginacion.PageRender;
import com.metro.modasistencia.utilerias.reportes.RegistroExportarExcel;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import static com.metro.modasistencia.utilerias.RegistroUtileria.comprobarHora;

@Controller
public class RegistroControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private RegistroServicio registroServicio;

    @GetMapping("/registros")
    public String listarRegistros(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo) {
        Pageable pageRequest = PageRequest.of(page, 7);
        Page<Registro> registros = registroServicio.findAll(pageRequest);
        PageRender<Registro> registroPageRender = new PageRender<>("/registros", registros);

        modelo.addAttribute("listaRegistros", registros);
        modelo.addAttribute("page", registroPageRender);
        return "/registro";
    }

    @GetMapping("/registros/nuevo")
    public String mostrarFormularioNuevoRegistro(Model modelo) {
        Registro registro = new Registro();
        modelo.addAttribute("registro", registro);

        return "registro_formulario";
    }

    @PostMapping("/registros/nuevo")
    public String guardarRegistro(@RequestParam(value = "usuario", defaultValue = "0") Integer  exp, @RequestParam(value = "password", defaultValue = "null") String pass, @Validated Registro registro, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        Usuario usuarioExiste = usuarioServicio.findByExpedienteAndPassword(exp, pass);
        if (usuarioExiste == null) {
            modelo.addAttribute("msgError", "Expediente o contraseña incorrectos");
            return "registro_formulario";
        }
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("registro", registro);
            return "registro_formulario";
        }

        LocalTime horaEntrada = usuarioExiste.getHoraEntrada();
        LocalTime horaSalida = usuarioExiste.getHoraSalida();
        LocalTime horaRegistro = LocalTime.now();
        List<Registro> registrosExistentes = registroServicio.findByFechaAndExpediente(LocalDate.now(), exp);
        RegistroUtileria registroUtileria = comprobarHora(registrosExistentes, horaRegistro, horaEntrada, horaSalida);
        if(!registroUtileria.isExito()) {
            modelo.addAttribute("registro", registro);
            modelo.addAttribute("msgError", registroUtileria.getMensaje());

            return "registro_formulario";
        }

        registro.setTipo(registroUtileria.getTipo());
        registroServicio.save(registro);
        redirect.addFlashAttribute("msgExito", registroUtileria.getMensaje());
        return "redirect:/";
    }

    @GetMapping("/registros/editar/{id}")
    public String mostrarFormularioEditarRegistro(@PathVariable ("id") Integer id, Model modelo) {
        Registro registro = registroServicio.findOne(id);
        modelo.addAttribute("registro", registro);

        return "registro_formulario";
    }

    @PostMapping("/registros/editar/{id}")
    public String actualizarRegistro(@PathVariable Integer id, @Validated Registro registro, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        Registro registroBD = registroServicio.findOne(id);
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("registro", registro);
            return "registro_formulario";
        }

        registroBD.setUsuario(registro.getUsuario());
        registroBD.setFecha(registro.getFecha());
        registroBD.setHora(registro.getHora());
        registroBD.setTipo(registro.getTipo());

        registroServicio.save(registro);
        redirect.addFlashAttribute("msgExito", "El registro ha sido actualizado con exito");
        return "redirect:/registros";
    }

    @PostMapping("/registros/eliminar/{id}")
    public String eliminarRegistro(@PathVariable Integer id, RedirectAttributes redirect) {
        registroServicio.delete(id);
        redirect.addFlashAttribute("msgExito", "El registro ha sido eliminado correctamente");

        return "redirect:/registros";
    }

    @GetMapping("/registros/exportar")
    public void exportarListaRegistrosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fechaActual = dateFormat.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Registros_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        List<Registro> registros = registroServicio.findAll();

        RegistroExportarExcel exporter = new RegistroExportarExcel(registros);
        exporter.exportar(response);
    }
}
