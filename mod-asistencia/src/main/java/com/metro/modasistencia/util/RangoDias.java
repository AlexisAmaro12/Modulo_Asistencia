package com.metro.modasistencia.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class RangoDias {
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;

    public RangoDias() {
    }

    public LocalDate getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(LocalDate fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public LocalDate getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(LocalDate fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    //Metodo para obtener el rango de dias del cual se sacara la estadistica, dependiendo de la fecha actual
    public RangoDias obtenerDiasDelPeriodo () {
        LocalDate fechaActual = LocalDate.now();
        RangoDias rangoDias = new RangoDias();
        if (fechaActual.getDayOfMonth() > 15) {
            rangoDias.setFechaInicial(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
            rangoDias.setFechaFinal(LocalDate.now().withDayOfMonth(15));
        } else {
            fechaActual = fechaActual.minusMonths(1);
            rangoDias.setFechaInicial(fechaActual.withDayOfMonth(16));
            rangoDias.setFechaFinal(fechaActual.with(TemporalAdjusters.lastDayOfMonth()));
        }
        return rangoDias;
    }
    //Metodo para calcular los dias habiles, es decir quita los sabados domingos y dias festivos
    public int calcularDiasHabiles(RangoDias rangoDias, List<LocalDate> diasFestivos) {

        int diasTrabajables = 0;
        boolean diaHabil = false;
        LocalDate fechaInicial = rangoDias.getFechaInicial();
        LocalDate fechaFinal = rangoDias.getFechaFinal();
        while (fechaInicial.isBefore(fechaFinal) || fechaInicial.equals(fechaFinal)) {

            if(!diasFestivos.isEmpty()) {
                for(LocalDate diaLibre : diasFestivos) {

                    if(fechaInicial.getDayOfWeek() != DayOfWeek.SUNDAY && fechaInicial.getDayOfWeek() != DayOfWeek.SATURDAY &&
                            !diaLibre.equals(fechaInicial)) {
                        diaHabil = true;
                    } else {
                        diaHabil = false;
                        break;
                    }
                }
            } else {
                if(fechaInicial.getDayOfWeek() != DayOfWeek.SUNDAY && fechaInicial.getDayOfWeek() != DayOfWeek.SATURDAY) {
                    diasTrabajables++;
                }
            }
            if (diaHabil) {
                diasTrabajables++;
            }
            fechaInicial = fechaInicial.plusDays(1);
        }
        return diasTrabajables;
    }
}
