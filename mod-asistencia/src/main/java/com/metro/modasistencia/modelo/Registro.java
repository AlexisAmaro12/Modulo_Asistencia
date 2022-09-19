package com.metro.modasistencia.modelo;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity //Clase para indicar la entidad de Registro y sus metodos getter, setter
public class Registro {

    @Id
    @Column(name = "id_registro")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reg_hora_entrada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime horaEntrada;

    @Column(name = "reg_hora_salida")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime horaSalida;

    @Column(name = "reg_fecha")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "exp_usuario")
    @NotNull(message = "Debes ingresar un expediente valido")
    private Usuario usuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @PrePersist
    public void asignarHoraFechaRegistro() {
        fecha = LocalDate.now();
    }

    public Registro(Integer id, LocalTime horaEntrada, LocalTime horaSalida, LocalDate fecha, Usuario usuario) {
        this.id = id;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.fecha = fecha;
        this.usuario = usuario;
    }

    public Registro() {
    }
}