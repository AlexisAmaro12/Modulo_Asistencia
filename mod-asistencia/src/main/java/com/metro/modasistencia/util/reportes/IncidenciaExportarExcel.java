package com.metro.modasistencia.util.reportes;

import com.metro.modasistencia.modelo.Incidencia;
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

//Clase encargada de realizar los reportes de excel de las incidencias
public class IncidenciaExportarExcel {

    private XSSFWorkbook libro;
    private XSSFSheet hoja;

    private List<Incidencia> listaIncidencias;

    //Metodo que sera usado en el controlador para descargar el reporte de excel
    public IncidenciaExportarExcel(List<Incidencia> listaIncidencias) {
        this.listaIncidencias = listaIncidencias;
        libro = new XSSFWorkbook();
        hoja = libro.createSheet("Registros de Incidencias");
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
        celda.setCellValue("Hora");
        celda.setCellStyle(estilo);

        celda = fila.createCell(4);
        celda.setCellValue("Tipo");
        celda.setCellStyle(estilo);

        celda = fila.createCell(5);
        celda.setCellValue("Estado");
        celda.setCellStyle(estilo);

        celda = fila.createCell(6);
        celda.setCellValue("Detalles");
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


        for (Incidencia incidencia : listaIncidencias) {
            Row fila = hoja.createRow(numeroFilas++);

            Cell celda = fila.createCell(0);
            celda.setCellValue(incidencia.getUsuario().getExpediente());
            hoja.autoSizeColumn(0);
            celda.setCellStyle(estilo);

            celda = fila.createCell(1);
            celda.setCellValue(incidencia.getUsuario().getNombre());
            hoja.autoSizeColumn(1);
            celda.setCellStyle(estilo);

            celda = fila.createCell(2);
            celda.setCellValue(incidencia.getFecha().toString());
            hoja.autoSizeColumn(2);
            celda.setCellStyle(estilo);

            celda = fila.createCell(3);
            celda.setCellValue(incidencia.getHora().format(formatoHora));
            hoja.autoSizeColumn(3);
            celda.setCellStyle(estilo);

            celda = fila.createCell(4);
            celda.setCellValue(incidencia.getTipo());
            hoja.autoSizeColumn(4);
            celda.setCellStyle(estilo);

            celda = fila.createCell(5);
            celda.setCellValue(incidencia.getEstado());
            hoja.autoSizeColumn(5);
            celda.setCellStyle(estilo);

            celda = fila.createCell(6);
            celda.setCellValue(incidencia.getDetalles());
            hoja.autoSizeColumn(6);
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
