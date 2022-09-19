package com.metro.modasistencia.util.reportes;

import com.metro.modasistencia.modelo.Registro;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

//Clase encargada de realizar los reportes de excel de los Registros
public class RegistroExportarExcel {

    private XSSFWorkbook libro;
    private XSSFSheet hoja;

    private List<Registro> listaRegistros;

    //Metodo que sera usado en el controlador para descargar el reporte de excel
    public RegistroExportarExcel(List<Registro> listaRegistros) {
        this.listaRegistros = listaRegistros;
        libro = new XSSFWorkbook();
        hoja = libro.createSheet("Registros");
    }

    //Metodo usado por la misma clase para realizar la cabecera de la tabla
    private void escribirCabeceraTabla() {
        Row fila = hoja.createRow(0);

        CellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setFontHeight(14);
        estilo.setFont(fuente);

        Cell celda = fila.createCell(0);
        celda.setCellValue("Expediente");
        celda.setCellStyle(estilo);

        celda = fila.createCell(1);
        celda.setCellValue("Nombre");
        celda.setCellStyle(estilo);

        celda = fila.createCell(2);
        celda.setCellValue("Fecha");
        celda.setCellStyle(estilo);

        celda = fila.createCell(3);
        celda.setCellValue("Hora de Entrada");
        celda.setCellStyle(estilo);

        celda = fila.createCell(4);
        celda.setCellValue("Hora de Salida");
        celda.setCellStyle(estilo);
    }

    //Metodo usado por la misma clase para escribir el contenido de la tabla
    private void escribirContenidoTabla() {
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");
        int numeroFilas = 1;
        CellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setFontHeight(10);
        estilo.setFont(fuente);


        for (Registro registro : listaRegistros) {
            Row fila = hoja.createRow(numeroFilas++);

            Cell celda = fila.createCell(0);
            celda.setCellValue(registro.getUsuario().getExpediente());
            hoja.autoSizeColumn(0);
            celda.setCellStyle(estilo);

            celda = fila.createCell(1);
            celda.setCellValue(registro.getUsuario().getNombre());
            hoja.autoSizeColumn(1);
            celda.setCellStyle(estilo);

            celda = fila.createCell(2);
            celda.setCellValue(registro.getFecha().toString());
            hoja.autoSizeColumn(2);
            celda.setCellStyle(estilo);

            celda = fila.createCell(3);
            if (registro.getHoraEntrada() == null){
                celda.setBlank();
            } else {
                celda.setCellValue(registro.getHoraEntrada().format(formatoHora));
            }
            hoja.autoSizeColumn(3);
            celda.setCellStyle(estilo);

            celda = fila.createCell(4);
            if (registro.getHoraSalida() == null){
                celda.setBlank();
            } else {
                celda.setCellValue(registro.getHoraSalida().format(formatoHora));
            }
            hoja.autoSizeColumn(4);
            celda.setCellStyle(estilo);
        }
    }

    //Metodo que sera usado por la misma clase para realizar la genereacion y descarga del reporte
    public void exportar(HttpServletResponse response) throws IOException {
        escribirCabeceraTabla();
        escribirContenidoTabla();

        ServletOutputStream outputStream = response.getOutputStream();
        libro.write(outputStream);

        libro.close();
        outputStream.close();
    }
}
