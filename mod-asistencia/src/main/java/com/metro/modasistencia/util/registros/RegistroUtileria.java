package com.metro.modasistencia.util.registros;

import java.time.LocalTime;

//Clase para comprobar si el registro sera de entrada o salida, ademas de verificar que este dentro de las horas permitidas para el registro
public class RegistroUtileria {

    private boolean exito; //Indica si el registro esta dentro de las horas permitidas y se puede realizar
    private String mensaje; //Mensaje que sera enviado a la vista
    private String tipo; //Indica si es de tipo de entrada o salida


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

    //Metodo para revisar si es un registro de entrada o salida y comprobar que sea una hora permitida
    public static RegistroUtileria comprobarHora(boolean entradaRegistrada, boolean salidaRegistrada, LocalTime horaRegistro, LocalTime horaEntrada, LocalTime horaSalida) {

        boolean valorExito;
        String conMensaje;
        String conTipo;


        int registroMinutos = ((horaRegistro.getHour()*60) + horaRegistro.getMinute());
        int entradaMinutos = ((horaEntrada.getHour()*60) + horaEntrada.getMinute());
        int salidaMinutos = ((horaSalida.getHour()*60) +  horaSalida.getMinute());

        int diferenciaMinutosEntrada = Math.abs(registroMinutos - entradaMinutos);
        int diferenciaMinutosSalida = Math.abs(salidaMinutos - registroMinutos);

        //Tiene ambos registros ya
        if(entradaRegistrada && salidaRegistrada) {
            valorExito = false;
            conMensaje = "No se puede realizar el registro, ya cuenta con un registro de entrada " +
                    "y uno de salida hoy";
            return new RegistroUtileria(valorExito, conMensaje);
        }
        //No ha registrado su entrada puede hacerlo
        else if(!entradaRegistrada && horaRegistro.equals(horaEntrada)) {
            valorExito  = true;
            conMensaje = "Registro de entrada realizado con exito";
            conTipo = "Entrada";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        }
        //Ya registro su entrada y puede registrar salida
        else if (entradaRegistrada && !salidaRegistrada && horaRegistro.equals(horaSalida)) {
            valorExito  = true;
            conMensaje = "Registro de salida realizado con exito, ha realizado su registro de entrada y salida hoy";
            conTipo = "Salida";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        }
        //No ha registrado su entrada pero puede registrar su salida
        else if (!entradaRegistrada && !salidaRegistrada && horaRegistro.equals(horaSalida)) {
            valorExito  = true;
            conMensaje = "Registro de salida realizado con exito, tiene pendiente el registro de entrada, debe de " +
                    "registrarlo en la seccion de incidencias, su hora de entrada ya paso";
            conTipo = "Salida";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        }
        //No ha registrado su entrada puede hacerlo
        else if (!entradaRegistrada && horaRegistro.isBefore(horaEntrada) && diferenciaMinutosEntrada <= 30) {
            valorExito  = true;
            conMensaje = "Registro de entrada realizado con exito";
            conTipo = "Entrada";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        }
        //No ha registrado su entrada puede hacerlo
        else if (!entradaRegistrada && horaRegistro.isAfter(horaEntrada) && diferenciaMinutosEntrada <= 10) {
            valorExito  = true;
            conMensaje = "Registro de entrada realizado con exito";
            conTipo = "Entrada";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        }
        //Ya registro su entrada y puede registrar su salida
        else if (entradaRegistrada && !salidaRegistrada && horaRegistro.isBefore(horaSalida) && diferenciaMinutosSalida <= 10) {
            valorExito  = true;
            conMensaje = "Registro de salida realizado con exito, ha realizado su registro de entrada y salida hoy";
            conTipo = "Salida";

            return new RegistroUtileria(valorExito, conMensaje,conTipo);
        }
        //Ya registro su entrada y puede registrar su salida
        else if (entradaRegistrada && !salidaRegistrada && horaRegistro.isAfter(horaSalida) && diferenciaMinutosSalida <= 30) {
            valorExito  = true;
            conMensaje = "Registro de salida realizado con exito, ha realizado su registro de entrada y salida hoy";
            conTipo = "Salida";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        }
        //No ha registrado su entrada pero puede registrar su salida
        else if (!entradaRegistrada && !salidaRegistrada && horaRegistro.isAfter(horaSalida) && diferenciaMinutosSalida <= 30) {
            valorExito  = true;
            conMensaje = "Registro de salida realizado con exito, tiene pendiente el registro de entrada, debe de " +
                    "registrarlo en la seccion de incidencias, su hora de entrada ya paso";
            conTipo = "Salida";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        }
        //No ha registrado su entrada pero puede registrar su salida
        else if (!entradaRegistrada && !salidaRegistrada && horaRegistro.isBefore(horaSalida) && diferenciaMinutosSalida <= 10) {
            valorExito  = true;
            conMensaje = "Registro de salida realizado con exito, tiene pendiente el registro de entrada, debe de " +
                    "registrarlo en la seccion de incidencias, su hora de entrada ya paso";
            conTipo = "Salida";

            return new RegistroUtileria(valorExito, conMensaje, conTipo);
        }
        //No ha registrado su entrada pero es demasiado pronto para hacerlo
        else if (!entradaRegistrada && horaRegistro.isBefore(horaEntrada) && diferenciaMinutosEntrada > 30) {
            valorExito  = false;
            conMensaje = "No se puede realizar el registro de entrada es demasiado pronto para registrarte, " +
                    "el registro de entrada se debe realizar 30 minutos antes o 10 minutos " +
                    "despues de tu hora de entrada: " + horaEntrada;

            return new RegistroUtileria(valorExito, conMensaje);
        }
        //No ha registrado su entrada pero es demasiado tarde para hacerlo
        else if (!entradaRegistrada && horaRegistro.isAfter(horaEntrada) && diferenciaMinutosEntrada > 10  && diferenciaMinutosEntrada < diferenciaMinutosSalida) {
            valorExito  = false;
            conMensaje = "No se puede realizar el registro de entrada es demasiado tarde para registrarte, " +
                    "el registro de entrada se debe realizar 30 minutos antes o 10 minutos " +
                    "despues de tu hora de entrada: " + horaEntrada;

            return new RegistroUtileria(valorExito, conMensaje);
        }
        //Ya registro su entrada y es muy pronto para registrar su salida
        else if (entradaRegistrada && !salidaRegistrada && horaRegistro.isBefore(horaSalida) && diferenciaMinutosSalida > 10) {
            valorExito  = false;
            conMensaje = "Ya cuenta con un registro de entrada. No se puede realizar el registro de salida es " +
                    "demasiado pronto para registrarte, el registro de salida se debe realizar 10 minutos antes " +
                    "o 30 minutos despues de tu hora de salida: " + horaSalida;

            return new RegistroUtileria(valorExito, conMensaje);
        }
        //Ya registro su entrada y es muy tarde para registrar su salida
        else if (entradaRegistrada && !salidaRegistrada && horaRegistro.isAfter(horaSalida) && diferenciaMinutosSalida > 30) {
            valorExito  = false;
            conMensaje = "Ya cuenta con un registro de entrada. No se puede realizar el registro de salida es " +
                    "demasiado tarde para registrarte, el registro de salida se debe realizar 10 minutos antes " +
                    "o 30 minutos despues de tu hora de salida: " + horaSalida;

            return new RegistroUtileria(valorExito, conMensaje);
        }
        //No registro su entrada y es muy pronto para registrar su salida
        else if (!entradaRegistrada && !salidaRegistrada && horaRegistro.isBefore(horaSalida) && diferenciaMinutosSalida > 10) {
            valorExito  = false;
            conMensaje = "Tiene pendiente el registro de entrada de hoy, realizarlo en la seccion de incidencia. " +
                    "No se puede realizar el registro de salida es demasiado pronto para registrarte, el registro " +
                    "de salida se debe realizar 10 minutos antes o 30 minutos despues de tu hora de salida: " + horaSalida;

            return new RegistroUtileria(valorExito, conMensaje);
        }
        //No registro su entrada y es muy tarde para registrar su salida
        else if (!entradaRegistrada && !salidaRegistrada && horaRegistro.isAfter(horaSalida) && diferenciaMinutosSalida > 30) {
            valorExito  = false;
            conMensaje = "Tiene pendiente el registro de entrada de hoy, realizarlo en la seccion de incidencia. " +
                    "No se puede realizar el registro de salida es demasiado tarde para registrarte, el registro " +
                    "de salida se debe realizar 10 minutos antes o 30 minutos despues de tu hora de salida: " + horaSalida;

            return new RegistroUtileria(valorExito, conMensaje);
        }//No registro su entrada, pero ya registro su salida y es muy tarde para registrar su entrada
        else if (!entradaRegistrada && salidaRegistrada && horaRegistro.isAfter(horaEntrada) && diferenciaMinutosSalida > 10) {
            valorExito  = false;
            conMensaje = "Tiene pendiente el registro de entrada de hoy, realizarlo en la seccion de incidencia. " +
                    "Ya hizo su registro de salida hoy";

            return new RegistroUtileria(valorExito, conMensaje);
        }
        else {
            valorExito  = false;
            conMensaje = "Fallo al intentar registrarse";

            return new RegistroUtileria(valorExito, conMensaje);
        }
    }
}
