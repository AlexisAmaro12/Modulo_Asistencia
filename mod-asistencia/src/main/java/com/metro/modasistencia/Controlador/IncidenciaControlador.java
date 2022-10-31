package com.metro.modasistencia.Controlador;

import com.metro.modasistencia.modelo.Incidencia;
import com.metro.modasistencia.modelo.Registro;
import com.metro.modasistencia.modelo.Usuario;
import com.metro.modasistencia.servicio.IncidenciaServicio;
import com.metro.modasistencia.servicio.RegistroServicio;
import com.metro.modasistencia.servicio.UsuarioServicio;
import com.metro.modasistencia.util.paginacion.PageRender;
import com.metro.modasistencia.util.reportes.IncidenciaExportarExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.Date;
import java.util.List;

@Controller //Controlador para interactuar con las incidencias
public class IncidenciaControlador {

    @Autowired //Inyeccion de los servicios de las incidencias
    private IncidenciaServicio incidenciaServicio;

    @Autowired //Inyeccion de los servicios de los registros
    private RegistroServicio registroServicio;

    @Autowired //Inyeccion de los servicios de los usuarios
    private UsuarioServicio usuarioServicio;

    @Autowired //Inyeccion del encriptador de passowrd
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //Peticion para listar las incidencias
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR','ROLE_USER')") //Se solicita una autorizacion por rol
    @GetMapping("/incidencias")
    public String listaIncidencias(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo) {
        //Para la paginacion
        Pageable pageRequest = PageRequest.of(page, 7, Sort.by("fecha"));
        Page<Incidencia> incidencias;

        //Se recuperan los roles y el expediente del usuario
        String roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        String expediente = SecurityContextHolder.getContext().getAuthentication().getName();

        //Se comprueba si es un usuario con rol de USER.
        if (roles.equals("[ROLE_USER]")) {
            //Lista las incidencias de solo ese usuario
            incidencias = incidenciaServicio.findByExpediente(Integer.parseInt(expediente), pageRequest);
        } else {
            //Lista todas las incidencias
            incidencias = incidenciaServicio.findAllByOrderByFecha(pageRequest);
        }
        PageRender<Incidencia> registroPageRender = new PageRender<>("/incidencias", incidencias);
        //Se envia a la vista la lista de incidencias
        modelo.addAttribute("listaIncidencias", incidencias);
        modelo.addAttribute("page", registroPageRender);
        return "incidencia/incidencia-lista"; //Se dirige al HTML incidencia
    }

    //Peticion para mostrar el formulario para una nueva incidencia
    @GetMapping("/incidencias/nueva")
    public String mostrarFormularioNuevaIncidencia(Model modelo) {
        //Se crea un objeto incidencia y se manda a la vista
        Incidencia incidencia = new Incidencia();
        modelo.addAttribute("incidencia", incidencia);

        return "incidencia/incidencia-formulario"; //Se dirige al HTML incidencia_formulario
    }

    //Peticion POST para guardar una nueva incidencia
    @PostMapping("/incidencias/nueva")
    public String guardarIncidencia(@RequestParam(value = "usuario", defaultValue = "0") Integer  exp, @RequestParam(value = "password", defaultValue = "null") String pass, @Validated Incidencia incidencia, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {

        String usuarioExiste = usuarioServicio.getPass(exp); //Se obtiene el password del usuario por el expediente ingresado
        //Se verifica que exista un usuario y que su contraseña sea correcta
        if(usuarioExiste == null || !bCryptPasswordEncoder.matches(pass, usuarioExiste)) {
            //Se manda un mensaje de error a la vista
            modelo.addAttribute("msgError", "Expediente o contraseña incorrectos");
            return "incidencia/incidencia-formulario"; //Se regresa al formulario
        }

        //Valida que no existan errores al llenar el formulario
        if (bindingResult.hasErrors()) {
            //Se mandan los errores de validacion a la vista
            modelo.addAttribute("incidencia", incidencia);
            return "incidencia/incidencia-formulario"; //Se regresa al formulario
        }

        Usuario usuarioExistente = usuarioServicio.findOne(exp); //Se obtiene un usuario por el expediente
        //Se revisa que el usuario no tenga el estado Inactivo
        if (usuarioExistente.getEstado().equals("Inactivo")) {
            //Se manda mensaje de error a la vista por usuario inactivo
            modelo.addAttribute("msgError", "Su cuenta esta dada de baja, contacte con el administrador");
            return "incidencia/incidencia-formulario"; //Se regresa al formulario
        }

        //Se guarda la incidencia
        incidenciaServicio.save(incidencia);
        //Se manda un mensaje de exito a la vista
        redirect.addFlashAttribute("msgExito", "La incidencia se registro correctamente");
        return "redirect:/registros/nuevo"; //Se nos redirige a la pagina de inicio
    }

    //Peticion para mostrar el formulario para editar una incidencia
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')") //Se solicita una autorizacion por roles
    @GetMapping("/incidencias/editar/{id}")
    public String mostrarFormularioEditarIncidencia(@PathVariable Integer id, Model modelo) {
        Incidencia incidencia = incidenciaServicio.findOne(id); //Se busca una incidencia por id
        //Se manda a la vista el modelo de la incidencia encontrada
        modelo.addAttribute("incidencia", incidencia);

        return "incidencia/incidencia-formulario"; //Se manda al HTML incidencia_formulario
    }

    //Peticion POST para actualizar una incidencia
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')") //Se solicita una autorizacion por roles
    @PostMapping("/incidencias/editar/{id}")
    public String actualizarIncidencia(@PathVariable Integer id, @Validated Incidencia incidencia, BindingResult bindingResult, RedirectAttributes redirect, Model modelo) {
        //Se revisa que no tenga errores de validacion
        if (bindingResult.hasErrors()) {
            //Se manda a la vista el modelo de incidencia
            modelo.addAttribute("incidencia", incidencia);

            return "incidencia/incidencia-formulario"; //Se dirige al formulario
        }
        //Se busca la incidencia a actualizar por id
        Incidencia incidenciaBD = incidenciaServicio.findOne(id);
        //Se actualizan los datos ingresados en la incidencia existente
        incidenciaBD.setEstado(incidencia.getEstado());
        incidenciaBD.setFecha(incidencia.getFecha());
        incidenciaBD.setHora(incidencia.getHora());
        incidenciaBD.setUsuario(incidencia.getUsuario());
        incidenciaBD.setDetalles(incidencia.getDetalles());
        incidenciaBD.setTipo(incidencia.getTipo());

        //Se guarda la incidencia ya actualizada
        incidenciaServicio.save(incidenciaBD);
        //Se le manda un mensaje de exito
        redirect.addFlashAttribute("msgExito", "El registro de incidencia ha sido actualizado correctamente");
        return "redirect:/incidencias"; //Se redirecciona a la url /incidencias
    }

    //Peticion para eliminar una incidencia
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')") //Se solicita una autorizacion por roles
    @PostMapping("/incidencias/eliminar/{id}")
    public String eliminarIncidencia(@PathVariable Integer id, RedirectAttributes redirect) {
        incidenciaServicio.delete(id); //Se elimina la incidencia por el id ingresado
        //Se manda mensaje de exito
        redirect.addFlashAttribute("msgExito", "El registro de incidencia fue eliminado correctamente");

        return "redirect:/incidencias"; //Se redirecciona a la url /incidencias
    }

    //Peticion para validar una incidencia
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')") //Se solicita una autorizacion por roles
    @PostMapping("/incidencias/validar/{id}")
    public String validarIncidencia(@PathVariable Integer id, RedirectAttributes redirect) {
        Incidencia incidenciaValidado = incidenciaServicio.findOne(id); //Se obtiene la incidencia que se desea validar
        //Se verifica que la incidencia no se encuentre validada ya
        if (incidenciaValidado.getEstado().equals("Validado")) {
            //Se manda un mensaje de error a la vista
            redirect.addFlashAttribute("msgFallo", "El registro de incidencia ya ha sido validado");

            return "redirect:/incidencias"; //Se redirecciona a la url incidencias
        }
        incidenciaValidado.setEstado("Validado"); //Se le asigna el estado de validado a la incidencia
        //Se busca un registro por su fecha y expediente del usuario
        Registro registroExiste = registroServicio.findByFechaAndExpediente(incidenciaValidado.getFecha(), incidenciaValidado.getUsuario().getExpediente());
        //Se comprueba que no exista el registro
        if (registroExiste == null) {
            Registro registroCreado = new Registro(); //Se crea un nuevo registro
            registroCreado.setUsuario(incidenciaValidado.getUsuario()); //Se le asigna el usuario al nuevo registro
            //Se comprueba si es una incidencia de tipo Entrada
            if (incidenciaValidado.getTipo().equals("Entrada")) {
                //Se asignara la hora de la incidencia a la hora de entrada del registro
                registroCreado.setHoraEntrada(incidenciaValidado.getHora());
            }
            //Se comprueba si es una incidencia de tipo Salida
            if (incidenciaValidado.getTipo().equals("Salida")) {
                //Se asigna la hora de la incidencia a la hora de salida del registro
                registroCreado.setHoraSalida(incidenciaValidado.getHora());
            }
            //Se guarda el registro nuevo y la incidencia con el estado de Validado
            registroServicio.save(registroCreado);
            registroCreado.setFecha(incidenciaValidado.getFecha());
            incidenciaServicio.save(incidenciaValidado);
            registroServicio.save(registroCreado);
            //Se manda un mensaje de exito a la vista
            redirect.addFlashAttribute("msgExito", "El registro de incidencia se ha validado correctamente");

            return "redirect:/incidencias"; //Se redirecciona a la URL /incidencias
        }
        //Se comprueba si es una incidencia de tipo Entrada
        if (incidenciaValidado.getTipo().equals("Entrada")) {
            //Se asignara la hora de la incidencia a la hora de entrada del registro
            registroExiste.setHoraEntrada(incidenciaValidado.getHora());
        }
        //Se comprueba si es una incidencia de tipo Salida
        if (incidenciaValidado.getTipo().equals("Salida")) {
            //Se asigna la hora de la incidencia a la hora de salida del registro
            registroExiste.setHoraSalida(incidenciaValidado.getHora());
        }
        //Se guarda el registro actualizado con la hora asignada por la incidencia
        registroServicio.save(registroExiste);
        incidenciaServicio.save(incidenciaValidado);//Se guarda la incidencia con el estado validado
        //Se manda mensaje de exito a la vista
        redirect.addFlashAttribute("msgExito", "El registro de incidencia se ha validado correctamente");

        return "redirect:/incidencias"; //Se redirecciona a la URL /incidencias
    }

    //Peticion para descargar un EXCEL con la lista de las incidencias
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR')") //Se solicita una autorizacion por roles
    @GetMapping("/incidencias/exportar")
    public void exportarListaIncidenciasExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fechaActual = dateFormat.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Incidencias_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        List<Incidencia> incidencias = incidenciaServicio.findAllByOrderByFecha();

        IncidenciaExportarExcel exporter = new IncidenciaExportarExcel(incidencias);
        exporter.exportar(response);
    }
}