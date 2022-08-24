package com.metro.modasistencia.Controlador;

import com.metro.modasistencia.modelo.Registro;
import com.metro.modasistencia.repositorio.RegistroRepositorio;
import com.metro.modasistencia.repositorio.UsuarioRepositorio;
import com.metro.modasistencia.utilerias.RegistroUtileria;
import com.metro.modasistencia.utilerias.reportes.RegistroExportarExcel;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RegistroRepositorio registroRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/registros")
    public String listarRegistros(Model modelo) {
        List<Registro> listaRegistros = registroRepositorio.findAll();
        modelo.addAttribute("listaRegistros", listaRegistros);
        return "/registro";
    }

    @GetMapping("/registros/nuevo")
    public String mostrarFormularioNuevoRegistro(Model modelo) {
        Registro registro = new Registro();
        modelo.addAttribute("registro", registro);

        return "registro_formulario";
    }

    @PostMapping("/registros/nuevo")
    public String guardarRegistro(@RequestParam(value = "usuario", defaultValue = "0") Integer exp, @Validated Registro registro, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("registro", registro);
            return "registro_formulario";
        }
        LocalTime horaEntrada = usuarioRepositorio.findById(exp).get().getHoraEntrada();
        LocalTime horaSalida = usuarioRepositorio.findById(exp).get().getHoraSalida();
        LocalTime horaRegistro = LocalTime.now();
        List<Registro> registrosExistentes = registroRepositorio.findByFechaAndUsuario_Expediente(LocalDate.now(), exp);
        RegistroUtileria registroUtileria = comprobarHora(registrosExistentes, horaRegistro, horaEntrada, horaSalida);
        if(!registroUtileria.isExito()) {
            modelo.addAttribute("registro", registro);
            modelo.addAttribute("msgError", registroUtileria.getMensaje());

            return "registro_formulario";
        }

        registro.setTipo(registroUtileria.getTipo());
        registroRepositorio.save(registro);
        redirect.addFlashAttribute("msgExito", registroUtileria.getMensaje());
        return "redirect:/";
    }

    @GetMapping("/registros/editar/{id}")
    public String mostrarFormularioEditarRegistro(@PathVariable ("id") Integer id, Model modelo) {
        Registro registro = registroRepositorio.findById(id).get();
        modelo.addAttribute("registro", registro);

        return "registro_formulario";
    }

    @PostMapping("/registros/editar/{id}")
    public String actualizarRegistro(@PathVariable Integer id, @Validated Registro registro, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        Registro registroBD = registroRepositorio.findById(id).get();
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("registro", registro);
            return "registro_formulario";
        }

        registroBD.setUsuario(registro.getUsuario());
        registroBD.setFecha(registro.getFecha());
        registroBD.setHora(registro.getHora());
        registroBD.setTipo(registro.getTipo());

        registroRepositorio.save(registro);
        redirect.addFlashAttribute("msgExito", "El registro ha sido actualizado con exito");
        return "redirect:/registros";
    }

    @PostMapping("/registros/eliminar/{id}")
    public String eliminarRegistro(@PathVariable Integer id, RedirectAttributes redirect, Model modelo) {
        registroRepositorio.deleteById(id);
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

        List<Registro> registros = registroRepositorio.findAll();

        RegistroExportarExcel exporter = new RegistroExportarExcel(registros);
        exporter.exportar(response);
    }
}
