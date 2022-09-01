package com.metro.modasistencia.modelo;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @NotNull(message = "Debe ingresar la hora")
    private LocalTime hora;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Debe ingresar la fecha")
    private LocalDate fecha;

    @Column(length = 10)
    @NotBlank(message = "Debe ingresar el tipo")
    private String tipo;

    @Column(length = 15)
    private String estado;

    @NotBlank(message = "Debe ingresar los detalles")
    private String detalles;

    @ManyToOne
    @JoinColumn(name = "usuario_exp")
    @NotNull(message = "Debe ingresar un expediente valido")
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @PrePersist
    public void asignarEstado() {
        estado = "No validado";
    }
    public Incidencia() {
    }

    public Incidencia(Integer id, LocalTime hora, LocalDate fecha, String tipo, String estado, String detalles, Usuario usuario) {
        this.id = id;
        this.hora = hora;
        this.fecha = fecha;
        this.tipo = tipo;
        this.estado = estado;
        this.detalles = detalles;
        this.usuario = usuario;
    }
}
