package com.metro.modasistencia.modelo;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime hora;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @Column(length = 10)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_exp")
    @NotNull(message = "Debes ingresar un expediente valido")
    private Usuario usuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @PrePersist
    public void asignarHoraFechaRegistro() {
        hora = LocalTime.now();
        fecha = LocalDate.now();
    }

    public Registro(Integer id, LocalTime hora, LocalDate fecha, String tipo, Usuario usuario) {
        this.id = id;
        this.hora = hora;
        this.fecha = fecha;
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public Registro() {
    }
}