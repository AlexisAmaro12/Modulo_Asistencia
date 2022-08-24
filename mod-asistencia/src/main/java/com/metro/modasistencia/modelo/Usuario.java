package com.metro.modasistencia.modelo;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Usuario {

    @Id
    @Column(length = 10)
    @NotNull(message = "Debe ingresar el expediente")
    private Integer expediente;

    @Column(length = 45, unique = true)
    @NotBlank(message = "Debe ingresar el nombre")
    private String nombre;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "hora_entrada")
    @NotNull(message = "Debe ingresar la hora de entrada")
    private LocalTime horaEntrada;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "hora_salida")
    @NotNull(message = "Debe ingresar la hora de salida")
    private LocalTime horaSalida;

    @NotBlank(message = "Debe ingresar la contraseña")
    private String password;

    @Column(length = 10)
    private String estado;

    @OneToMany(mappedBy = "usuario", cascade= CascadeType.ALL)
    private Set<Registro> registros = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade= CascadeType.ALL)
    private Set<Incidencia> incidencias = new HashSet<>();

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
