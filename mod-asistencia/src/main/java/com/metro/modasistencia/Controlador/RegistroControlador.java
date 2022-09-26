package com.metro.modasistencia.Controlador;

import com.metro.modasistencia.modelo.Registro;
import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.servicio.RegistroServicio;
import com.metro.modasistencia.servicio.UsuarioServicio;
import com.metro.modasistencia.util.registros.RegistroUtileria;
import com.metro.modasistencia.util.paginacion.PageRender;
import com.metro.modasistencia.util.reportes.RegistroExportarExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

import static com.metro.modasistencia.util.registros.RegistroUtileria.comprobarHora;

@Controller //Controlador para interactuar con los registros
public class RegistroControlador {

    @Autowired //Inyeccion del servicio de los usuarios
    private UsuarioServicio usuarioServicio;

    @Autowired //Inyeccion del servicio de los registros
    private RegistroServicio registroServicio;

    @Autowired //Inyeccion del encriptador de passwords
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //Peticion para listar los registros
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR','ROLE_USER')") //Se solicita autorizacion por roles
    @GetMapping("/registros")
    public String listarRegistros(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo) {
        //Pageable paginacion
        Pageable pageRequest = PageRequest.of(page, 7);
        Page<Registro> registros;

        //Se recuperan los roles y el expediente del usuario que hace la peticion
        String roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        String expediente = SecurityContextHolder.getContext().getAuthentication().getName();

        //Se comprueba que el usuario solo tenga rol de USER
        if (roles.equals("[ROLE_USER]")) {
            //Se buscan todos los registros del expediente del usuario
            registros = registroServicio.findByExpediente(Integer.parseInt(expediente), pageRequest);
        } else {
            //Se buscan todos los registros
            registros = registroServicio.findAll(pageRequest);
        }
        PageRender<Registro> registroPageRender = new PageRender<>("/registros", registros);
        //Se envia la lista de los registros a la vista
        modelo.addAttribute("listaRegistros", registros);
        modelo.addAttribute("page", registroPageRender);
        return "registro/registro-lista"; //Se dirige al HTML registro
    }

    //Peticion para mostrar el formulario para realizar un nuevo registro
    @GetMapping("/registros/nuevo")
    public String mostrarFormularioNuevoRegistro(Model modelo) {
        //Se crea un nuevo Registro
        Registro registro = new Registro();
        //Se manda a la vista el registro
        modelo.addAttribute("registro", registro);

        return "registro/registro-formulario"; //Se dirige al HTML registro_formulario
    }

    //PEticion POST para guardar un registro nuevo
    @PostMapping("/registros/nuevo")
    public String guardarRegistro(@RequestParam(value = "usuario", defaultValue = "0") Integer  exp, @RequestParam(value = "password", defaultValue = "null") String pass, @Validated Registro registro, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        //Se verifica que el formulario no tenga errores
        if (bindingResult.hasErrors()) {
            //Se le envian los mensajes de error a la vista
            modelo.addAttribute("registro", registro);
            return "registro/registro-formulario"; //Se dirige al HTML registro_formulario
        }
        String usuarioExiste = usuarioServicio.getPass(exp);//Se obtiene el password del usuario por su expediente
        //Se verifica que el usuario exista ademas de que su contraseña sea la correcta
        if (usuarioExiste == null || !bCryptPasswordEncoder.matches(pass, usuarioExiste)) {
            //Se envia un mensaje de error a la vista
            modelo.addAttribute("msgError", "Expediente o contraseña incorrectos");
            return "registro/registro-formulario"; //Se dirige al HTML registro_formulario
        }
        Usuario usuarioExistente = usuarioServicio.findOne(exp);
        //Se comprueba que el estado del usuario no sea inactivo
        if (usuarioExistente.getEstado().equals("Inactivo")) {
            //Se manda un mensaje de error a la vista
            modelo.addAttribute("msgError", "Su cuenta esta dada de baja, contacte con el administrador");
            return "registro/registro-formulario";
        }

        //Se obtiene un registro por fecha y expediente
        Registro registroExistente = registroServicio.findByFechaAndExpediente(LocalDate.now(), exp);

        boolean entradaRegistrada;
        boolean salidaRegistrada;
        //Se revisa si existe un registro con la fecha y usuario indicado
        if(registroExistente != null) {
            //Se asignan valores true o false, dependiendo cual si el registro ya tiene registro de entrada y/o salida
            entradaRegistrada = registroExistente.getHoraEntrada() != null;
            salidaRegistrada = registroExistente.getHoraSalida() != null;
        } else {
            //Se asignan valores false a los valores de entrada y salida registradas
            entradaRegistrada = false;
            salidaRegistrada = false;
        }

        LocalTime horaEntrada = usuarioExistente.getHoraEntrada();
        LocalTime horaSalida = usuarioExistente.getHoraSalida();
        LocalTime horaRegistro = LocalTime.now();
        //Se utiliza el metodo comprobarHora para revisar si el registro es de entrada o salida, ademas de
        //comprobar que la hora sea permitida para realizar ese registro
        RegistroUtileria registroUtileria = comprobarHora(entradaRegistrada, salidaRegistrada, horaRegistro, horaEntrada, horaSalida);

        //Se comprueba si el registro no se puede realizar
        if(!registroUtileria.isExito()) {
            //Se envia un mensaje de error a la vista
            modelo.addAttribute("registro", registro);
            modelo.addAttribute("msgError", registroUtileria.getMensaje());

            return "registro/registro-formulario"; //Se dirige al HTML registro_formulario
        }
        //Se comprueba que exista un registro
        if(registroExistente != null) {
            //Como existe un registro y aun asi se puede registrar significa que debera guardar la hora de salida
            registroExistente.setHoraSalida(horaRegistro); //Se asigna la hora de salida al registro existente
            registroServicio.save(registroExistente); //Se guarda el registro actualizado
            //Se manda un mensaje de exito a la vista
            redirect.addFlashAttribute("msgExito", registroUtileria.getMensaje());
            return "redirect:/"; //Se dirige a la pagina de inicio
        }
        //Se revisa que el valor que nos devolvio el metodo comprobarHora sea de tipo entrada
        if(registroUtileria.getTipo().equals("Entrada")) {
            registro.setHoraEntrada(horaRegistro); //Se le asigna la hora de entrada
            registroServicio.save(registro); //Se guarda el registro con la hora de entrada asignada
            //Manda un mensaje de exito a la vista
            redirect.addFlashAttribute("msgExito", registroUtileria.getMensaje());
            return "redirect:/"; //Dirige a la pagina de inicio
        }
        registro.setHoraSalida(horaRegistro); //Asigna la hora de salida al registro
        registroServicio.save(registro); //Guarda el registro con la hora de salida asignada
        //Manda mensaje de exito a la vista
        redirect.addFlashAttribute("msgExito", registroUtileria.getMensaje());
        return "redirect:/"; //Dirige a la pagina de inicio
    }

    //Peticion para mostrar el formulario de editar registro
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')")
    @GetMapping("/registros/editar/{id}")
    public String mostrarFormularioEditarRegistro(@PathVariable ("id") Integer id, Model modelo) {
        Registro registro = registroServicio.findOne(id); //Obtiene el registro por su id
        modelo.addAttribute("registro", registro); //Manda a la vista ese registro

        return "registro/registro-formulario"; //Dirige al HTML registro_formulario
    }

    //Peticion POST para actualizar un registro
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')")
    @PostMapping("/registros/editar/{id}")
    public String actualizarRegistro(@PathVariable Integer id, @Validated Registro registro, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        //Comprueba que no existan errores al llenar el formulario
        if (bindingResult.hasErrors()) {
            //Manda el registro a la vista para msotrar los mensajes de error
            modelo.addAttribute("registro", registro);
            return "registro/registro-formulario"; //Dirige al HTML registro_formulario
        }
        //Recupera el registro que se actualizara y se le asignan los valores que se mandaron en el formulario
        Registro registroBD = registroServicio.findOne(id);
        registroBD.setUsuario(registro.getUsuario());
        registroBD.setFecha(registro.getFecha());
        registroBD.setHoraEntrada(registro.getHoraEntrada());
        registroBD.setHoraSalida(registro.getHoraSalida());

        registroServicio.save(registro); //Guarda el registro actualizado
        //Manda mensaje de exito a la vista
        redirect.addFlashAttribute("msgExito", "El registro ha sido actualizado con exito");
        return "redirect:/registros"; //Dirige a la URL /registros
    }

    //Peticion para eliminar un registro
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')")
    @PostMapping("/registros/eliminar/{id}")
    public String eliminarRegistro(@PathVariable Integer id, RedirectAttributes redirect) {
        registroServicio.delete(id); //Elimina registro por id
        //Manda mensaje de exito al eliminar el registro
        redirect.addFlashAttribute("msgExito", "El registro ha sido eliminado correctamente");

        return "redirect:/registros"; //Dirige a la URL /registros
    }

    //Peticion para exportar la lista de registros en EXCEL
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')")
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
