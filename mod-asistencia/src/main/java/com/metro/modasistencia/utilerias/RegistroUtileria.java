package com.metro.modasistencia.utilerias;

import com.metro.modasistencia.modelo.Registro;

import java.time.LocalTime;
import java.util.List;

public class RegistroUtileria {

    private boolean exito;
    private String mensaje;
    private String tipo;

    public RegistroUtileria() {
    }

    public RegistroUtileria(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public RegistroUtileria(boolean exito, String mensaje, String tipo) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public static RegistroUtileria comprobarHora(List<Registro> registrosExistentes, LocalTime horaRegistro, LocalTime horaEntrada, LocalTime horaSalida) {

        int canRegistros = registrosExistentes.size();
        boolean valorExito;
        String conMensaje;
        String conTipo;


        int registroMinutos = ((horaRegistro.getHour()*60) + horaRegistro.getMinute());
        int entradaMinutos = ((horaEntrada.getHour()*60) + horaEntrada.getMinute());
        int salidaMinutos = ((horaSalida.getHour()*60) +  horaSalida.getMinute());

        int diferenciaMinutosEntrada = Math.abs(registroMinutos - entradaMinutos);
        int diferenciaMinutosSalida = Math.abs(salidaMinutos - registroMinutos);

        if(canRegistros >= 2) {
            valorExito = false;
            conMensaje = "No se puede realizar el registro, ya cuenta con un registro de entrada " +
                    "y uno de salida hoy";
            return new RegistroUtileria(valorExito, conMensaje);

        } else if(canRegistros == 0 && horaRegistro.equals(horaEntrada)) {
            valorExito  = true;
            conMensaje = "Registro de entrada realizado con exito";
            conTipo = "Entrada";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        } else if (canRegistros == 1 && horaRegistro.equals(horaSalida)) {
            valorExito  = true;
            conMensaje = "Registro de salida realizado con exito";
            conTipo = "Salida";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        } else if (canRegistros == 0 && horaRegistro.isBefore(horaEntrada) && diferenciaMinutosEntrada <= 30) {
            valorExito  = true;
            conMensaje = "Registro de entrada realizado con exito";
            conTipo = "Entrada";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        } else if (canRegistros == 0 && horaRegistro.isAfter(horaEntrada) && diferenciaMinutosEntrada <= 10) {
            valorExito  = true;
            conMensaje = "Registro de entrada realizado con exito";
            conTipo = "Entrada";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        } else if (canRegistros == 1 && horaRegistro.isBefore(horaSalida) && diferenciaMinutosSalida <= 10) {
            valorExito  = true;
            conMensaje = "Registro de salida realizado con exito";
            conTipo = "Salida";

            return new RegistroUtileria(valorExito, conMensaje,conTipo);
        }  else if (canRegistros == 1 && horaRegistro.isAfter(horaSalida) && diferenciaMinutosSalida <= 30) {
            valorExito  = true;
            conMensaje = "Registro de salida realizado con exito";
            conTipo = "Salida";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        } else if (canRegistros == 0 && horaRegistro.isBefore(horaEntrada) && diferenciaMinutosEntrada > 30) {
            valorExito  = false;
            conMensaje = "No se puede realizar el registro de entrada es demasiado pronto para registrarte, " +
                    "el registro de entrada se debe realizar 30 minutos antes o 10 minutos " +
                    "despues de tu hora de entrada: " + horaEntrada;

            return new RegistroUtileria(valorExito, conMensaje);
        } else if (canRegistros == 0 && horaRegistro.isAfter(horaEntrada) && diferenciaMinutosEntrada > 10) {
            valorExito  = false;
            conMensaje = "No se puede realizar el registro de entrada es demasiado tarde para registrarte, " +
                    "el registro de entrada se debe realizar 30 minutos antes o 10 minutos " +
                    "despues de tu hora de entrada: " + horaEntrada;

            return new RegistroUtileria(valorExito, conMensaje);
        } else if (canRegistros == 1 && horaRegistro.isBefore(horaSalida) && diferenciaMinutosSalida > 10) {
            valorExito  = false;
            conMensaje = "Ya cuenta con un registro de entrada. No se puede realizar el registro de salida es " +
                    "demasiado pronto para registrarte, el registro de salida se debe realizar 10 minutos antes " +
                    "o 30 minutos despues de tu hora de salida: " + horaSalida;

            return new RegistroUtileria(valorExito, conMensaje);
        } else if (canRegistros == 1 && horaRegistro.isAfter(horaSalida) && diferenciaMinutosSalida > 30) {
            valorExito  = false;
            conMensaje = "Ya cuenta con un registro de entrada. No se puede realizar el registro de salida es " +
                    "demasiado tarde para registrarte, el registro de salida se debe realizar 10 minutos antes " +
                    "o 30 minutos despues de tu hora de salida: " + horaSalida;

            return new RegistroUtileria(valorExito, conMensaje);
        } else {
            System.out.println("No puede registrarse");
            valorExito  = false;
            conMensaje = "Fallo al intentar registrarse";

            return new RegistroUtileria(valorExito, conMensaje);
        }
    }
}
