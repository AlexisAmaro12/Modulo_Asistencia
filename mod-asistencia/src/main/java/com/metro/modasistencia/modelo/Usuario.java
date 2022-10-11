package com.metro.modasistencia.modelo;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;



@Entity //Clase para indicar la entidad de usuario y sus metodos getter, setter
public class Usuario {

    @Id
    @Column(name = "exp_usuario",length = 10)
    @NotNull(message = "Debe ingresar el expediente") //Agregamos la validacion y los mensajes de error con las anotaciones
    private Integer expediente;

    @Column(name = "usuario_nombre", length = 45)
    @NotBlank(message = "Debe ingresar el nombre")
    private String nombre;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "usuario_hora_entrada")
    @NotNull(message = "Debe ingresar la hora de entrada")
    private LocalTime horaEntrada;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "usuario_hora_salida")
    @NotNull(message = "Debe ingresar la hora de salida")
    private LocalTime horaSalida;

    @Column(name = "usuario_password")
    private String password;

    @Column(name = "usuario_estado", length = 10)
    private String estado;

    @OneToMany(mappedBy = "usuario", cascade= CascadeType.ALL)
    private Set<Registro> registros = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade= CascadeType.ALL)
    private Set<Incidencia> incidencias = new HashSet<>();

    @Size(min=1, message = "Debe asignar un rol")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_roles",
            joinColumns=@JoinColumn(name="exp_usuario"),
            inverseJoinColumns=@JoinColumn(name="id_rol"))
    private Set<Rol> roles;

    public Integer getExpediente() {
        return expediente;
    }

    public void setExpediente(Integer expediente) {
        this.expediente = expediente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Set<Registro> getRegistros() {
        return registros;
    }

    public void setRegistros(Set<Registro> registros) {
        this.registros = registros;
    }

    public Set<Incidencia> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(Set<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }


    @PrePersist
    public void asignarEstado() {
        estado = "Activo";
    }

    public Usuario(Integer expediente, String nombre, LocalTime horaEntrada, LocalTime horaSalida, String password, String estado, Set<Registro> registros, Set<Incidencia> incidencias) {
        this.expediente = expediente;
        this.nombre = nombre;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.password = password;
        this.estado = estado;
        this.registros = registros;
        this.incidencias = incidencias;
    }

    public Usuario() {
    }
}
